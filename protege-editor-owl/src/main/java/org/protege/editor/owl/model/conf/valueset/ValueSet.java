package org.protege.editor.owl.model.conf.valueset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.protege.editor.owl.model.OWLModelManager;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 *
 * Represents a set of labelled values that can be drawn from an ontology or some other source.
 */
@JsonSubTypes(
        {
                @JsonSubTypes.Type(IndividualsValueSet.class),
                @JsonSubTypes.Type(SubClassesValueSet.class),
        }
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
              property= "type")
public interface ValueSet {

        /**
         * Gets the labelled values in the set.
         * @param modelManager A model manager to provide context for extracting the labelled values.
         * @return The list of labelled values in the set.
         */
        @JsonIgnore
        @Nonnull
        List<LabelledValue> getLabelledValues(@Nonnull OWLModelManager modelManager);
}
