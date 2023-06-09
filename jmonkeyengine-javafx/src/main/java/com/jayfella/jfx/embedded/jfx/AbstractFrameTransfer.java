package com.jayfella.jfx.embedded.jfx;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * The base implementation of a frame transfer.
 *
 * @param <T> the destination's type.
 * @author JavaSaBr
 * @author malte3d
 */
public abstract class AbstractFrameTransfer<T> implements FrameTransfer {

    protected static final int RUNNING_STATE = 1;
    protected static final int WAITING_STATE = 2;
    protected static final int DISPOSING_STATE = 3;
    protected static final int DISPOSED_STATE = 4;

    /**
     * The Frame state.
     */
    protected final AtomicInteger frameState;

    /**
     * The Image state.
     */
    protected final AtomicInteger imageState;

    /**
     * The Frame buffer.
     */
    protected final FrameBuffer frameBuffer;

    /**
     * The Pixel writer.
     */
    protected final PixelWriter pixelWriter;

    /**
     * The Frame byte buffer.
     */
    protected final ByteBuffer frameByteBuffer;

    /**
     * The transfer mode.
     */
    protected final FrameTransferSceneProcessor.TransferMode transferMode;

    /**
     * The byte buffer.
     */
    protected final byte[] byteBuffer;

    /**
     * The image byte buffer.
     */
    protected final byte[] imageByteBuffer;

    /**
     * The prev image byte buffer.
     */
    protected final byte[] prevImageByteBuffer;

    /**
     * How many frames need to write else.
     */
    protected int frameCount;

    /**
     * The width.
     */
    private final int width;

    /**
     * The height.
     */
    private final int height;

    public AbstractFrameTransfer(T destination, FrameTransferSceneProcessor.TransferMode transferMode, FrameBuffer frameBuffer, int width, int height) {

        this.transferMode = transferMode;
        this.frameState = new AtomicInteger(WAITING_STATE);
        this.imageState = new AtomicInteger(WAITING_STATE);
        this.width = frameBuffer != null ? frameBuffer.getWidth() : width;
        this.height = frameBuffer != null ? frameBuffer.getHeight() : height;
        this.frameCount = 0;

        if (frameBuffer != null) {
            this.frameBuffer = frameBuffer;
        } else {
            this.frameBuffer = new FrameBuffer(width, height, 1);
            this.frameBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
            this.frameBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.RGBA8));
            this.frameBuffer.setSrgb(true);
        }

        frameByteBuffer = BufferUtils.createByteBuffer(getWidth() * getHeight() * 4);
        byteBuffer = new byte[getWidth() * getHeight() * 4];
        prevImageByteBuffer = new byte[getWidth() * getHeight() * 4];
        imageByteBuffer = new byte[getWidth() * getHeight() * 4];
        pixelWriter = getPixelWriter(destination, this.frameBuffer, width, height);
    }

    @Override
    public void initFor(Renderer renderer, boolean main) {

        if (main)
            renderer.setMainFrameBufferOverride(frameBuffer);
    }

    /**
     * Get the pixel writer.
     *
     * @param destination the destination.
     * @param frameBuffer the frame buffer.
     * @param width       the width.
     * @param height      the height.
     * @return the pixel writer.
     */
    protected abstract PixelWriter getPixelWriter(T destination, FrameBuffer frameBuffer, int width, int height);

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void copyFrameBufferToImage(RenderManager renderManager) {

        while (!frameState.compareAndSet(WAITING_STATE, RUNNING_STATE)) {
            if (frameState.get() == DISPOSED_STATE) {
                return;
            }
        }

        // Convert screenshot.
        try {

            frameByteBuffer.clear();

            Renderer renderer = renderManager.getRenderer();
            renderer.readFrameBufferWithFormat(frameBuffer, frameByteBuffer, Image.Format.RGBA8);

        } finally {
            if (!frameState.compareAndSet(RUNNING_STATE, WAITING_STATE)) {
                throw new RuntimeException("unknown problem with the frame state");
            }
        }

        synchronized (byteBuffer) {
            frameByteBuffer.get(byteBuffer);

            if (transferMode == FrameTransferSceneProcessor.TransferMode.ON_CHANGES) {

                final byte[] prevBuffer = getPrevImageByteBuffer();

                if (Arrays.equals(prevBuffer, byteBuffer)) {
                    if (frameCount == 0) return;
                } else {
                    frameCount = 2;
                    System.arraycopy(byteBuffer, 0, prevBuffer, 0, byteBuffer.length);
                }

                frameByteBuffer.position(0);
                frameCount--;
            }
        }

        // JfxPlatform.runInFxThread(this::writeFrame);
        Platform.runLater(this::writeFrame);
    }

    /**
     * Write content to image.
     */
    protected void writeFrame() {

        while (!imageState.compareAndSet(WAITING_STATE, RUNNING_STATE)) {
            if (imageState.get() == DISPOSED_STATE) {
                return;
            }
        }

        try {

            byte[] imageByteBuffer = getImageByteBuffer();

            synchronized (byteBuffer) {
                System.arraycopy(byteBuffer, 0, imageByteBuffer, 0, byteBuffer.length);
            }

            IntStream.range(0, width * height).parallel().forEach(i -> {
                int index = i * 4;
                byte r = imageByteBuffer[index];
                byte g = imageByteBuffer[index + 1];
                byte b = imageByteBuffer[index + 2];
                byte a = imageByteBuffer[index + 3];
                imageByteBuffer[index] = b;
                imageByteBuffer[index + 1] = g;
                imageByteBuffer[index + 2] = r;
                imageByteBuffer[index + 3] = a;
            });

            PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraInstance();

            pixelWriter.setPixels(0, 0, width, height, pixelFormat, imageByteBuffer, 0, width * 4);

        } finally {
            if (!imageState.compareAndSet(RUNNING_STATE, WAITING_STATE)) {
                throw new RuntimeException("unknown problem with the image state");
            }
        }
    }

    /**
     * Get the image byte buffer.
     *
     * @return the image byte buffer.
     */
    protected byte[] getImageByteBuffer() {
        return imageByteBuffer;
    }

    /**
     * Get the prev image byte buffer.
     *
     * @return the prev image byte buffer.
     */
    protected byte[] getPrevImageByteBuffer() {
        return prevImageByteBuffer;
    }

    @Override
    public void dispose() {
        while (!frameState.compareAndSet(WAITING_STATE, DISPOSING_STATE)) ;
        while (!imageState.compareAndSet(WAITING_STATE, DISPOSING_STATE)) ;
        disposeImpl();
        frameState.compareAndSet(DISPOSING_STATE, DISPOSED_STATE);
        imageState.compareAndSet(DISPOSING_STATE, DISPOSED_STATE);
    }

    /**
     * Dispose.
     */
    protected void disposeImpl() {
        frameBuffer.dispose();
    }
}
