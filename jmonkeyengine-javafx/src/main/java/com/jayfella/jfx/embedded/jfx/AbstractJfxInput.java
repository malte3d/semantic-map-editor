package com.jayfella.jfx.embedded.jfx;

import com.jayfella.jfx.embedded.jme.JmeOffscreenSurfaceContext;
import com.jme3.input.Input;
import com.jme3.input.RawInputListener;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * The base implementation of the {@link Input} for using in the ImageView.
 *
 * @author JavaSaBr
 */
abstract class AbstractJfxInput implements Input {

    /**
     * The context.
     */
    protected final JmeOffscreenSurfaceContext context;

    /**
     * The raw listener.
     */
    protected RawInputListener listener;

    /**
     * The input node.
     */
    protected Node node;

    /**
     * The scene.
     */
    protected Scene scene;

    /**
     * The flag of initializing this.
     */
    protected boolean initialized;

    public AbstractJfxInput(JmeOffscreenSurfaceContext context) {
        this.context = context;
    }

    /**
     * Checks of existence the node.
     *
     * @return true if the node is existing.
     */
    protected boolean hasNode() {
        return node != null;
    }

    /**
     * Gets the bound node.
     *
     * @return the bound node.
     */
    protected Node getNode() {
        return node;
    }

    /**
     * Gets the raw listener.
     *
     * @return the raw listener.
     */
    protected RawInputListener getListener() {
        return listener;
    }

    /**
     * Bind this input to the node.
     *
     * @param node the node.
     */
    public void bind(Node node) {
        this.node = node;
        this.scene = node.getScene();
    }

    /**
     * Unbind.
     */
    public void unbind() {
        this.node = null;
        this.scene = null;
    }

    @Override
    public void initialize() {

        if (isInitialized())
            return;

        initialized = true;
    }

    @Override
    public void update() {

        if (!context.isRenderable())
            return;

        updateImpl();
    }

    /**
     * Update.
     */
    abstract void updateImpl();

    @Override
    public void destroy() {
        unbind();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    @Override
    public long getInputTimeNanos() {
        return System.nanoTime();
    }
}
