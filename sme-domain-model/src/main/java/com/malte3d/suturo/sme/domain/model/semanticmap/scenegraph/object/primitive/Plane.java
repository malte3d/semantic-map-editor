package com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.primitive;

import com.malte3d.suturo.commons.ddd.annotation.ValueObject;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.Position;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.Rotation;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.SmObjectName;
import com.malte3d.suturo.sme.domain.model.semanticmap.scenegraph.object.SmObjectType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a primitive plane.
 */
@ValueObject

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@ToString
public class Plane extends Primitive {

    /**
     * A default plane that is positioned at the origin above the floor.
     */
    public static final Plane DEFAULT = new Plane(SmObjectName.of("Plane"), Position.of(0f, 0.5f, 0f), Rotation.IDENTITY, 2.0f, 1.0f);

    /**
     * The width of the plane in meter.
     */
    float width;
    /**
     * The height/depth of the plane in meter.
     */
    float height;

    public Plane(@NonNull SmObjectName name, @NonNull Position position, @NonNull Rotation rotation, float width, float height) {
        super(name, SmObjectType.PLANE, position, rotation);
        this.width = width;
        this.height = height;
    }
}
