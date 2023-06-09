package com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.primitive;

import com.malte3d.suturo.commons.ddd.annotation.ValueObject;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.Position;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.Rotation;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.SmObjectName;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.SmObjectType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a primitive box.
 */
@ValueObject

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@ToString
public class Box extends Primitive {

    /**
     * A default box that is positioned at the origin above the floor.
     */
    public static final Box DEFAULT = new Box(SmObjectName.of("Box"), Position.of(0f, 0.5f, 0f), Rotation.IDENTITY, 1.0f, 1.0f, 1.0f);

    /**
     * The width of the box in meter.
     */
    float width;
    /**
     * The height of the box in meter.
     */
    float height;
    /**
     * The depth of the box in meter.
     */
    float depth;

    public Box(@NonNull SmObjectName name, @NonNull Position position, @NonNull Rotation rotation, float width, float height, float depth) {
        super(name, SmObjectType.BOX, position, rotation);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
