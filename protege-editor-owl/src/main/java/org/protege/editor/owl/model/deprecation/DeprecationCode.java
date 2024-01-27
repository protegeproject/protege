package org.protege.editor.owl.model.deprecation;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.conf.IRIExpander;
import org.protege.editor.owl.model.conf.valueset.ValueSet;
import org.semanticweb.owlapi.model.IRI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeprecationCode {

    @Nonnull
    private final String property;

    @Nonnull
    private final ValueSet valueSet;

    @JsonCreator
    public DeprecationCode(@Nonnull @JsonProperty("property") String property,
                           @Nonnull @JsonProperty("valueSet") ValueSet valueSet) {
        this.property = checkNotNull(property);
        this.valueSet = checkNotNull(valueSet);
    }

    @Nonnull
    public IRI getPropertyIri() {
        return IRIExpander.expand(property).orElseThrow(() -> new RuntimeException("property not supplied"));
    }

    @Nonnull
    public ValueSet getValueSet() {
        return valueSet;
    }
}
