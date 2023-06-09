package com.malte3d.suturo.sme.domain.model.semanticmap;

import com.malte3d.suturo.commons.ddd.annotation.ValueObject;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents the name of a semantic map.
 */
@ValueObject

@Value(staticConstructor = "of")
public class SemanticMapName {
    @NonNull
    String value;
}
