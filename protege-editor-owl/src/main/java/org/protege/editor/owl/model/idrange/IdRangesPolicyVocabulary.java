package org.protege.editor.owl.model.idrange;

import com.google.common.collect.ImmutableMap;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 */
public enum IdRangesPolicyVocabulary {

    /**
     * Relates an ontology used to record id policy to the number of digits in the URI.
     * The URI is: the 'has ID prefix" annotation property value concatenated with an integer
     * in the id range (left padded with "0"s to make this many digits)
     */
    ID_DIGIT_COUNT(IRI.create("http://purl.obolibrary.org/obo/IAO_0000596")),

    /**
     * Relates a datatype that encodes a range of integers to the name of the
     * person or organization who can use those ids constructed in that range to define new terms
     */
    ID_RANGE_ALLOCATED_TO(IRI.create("http://purl.obolibrary.org/obo/IAO_0000597")),

    /**
     * Relating an ontology used to record id policy to the ontology namespace whose policy it manages
     */
    ID_POLICY_FOR(IRI.create("http://purl.obolibrary.org/obo/IAO_0000598")),

    /**
     * Relates an ontology used to record id policy to a prefix concatenated with an integer in the id range
     * (left padded with "0"s to make this many digits) to construct an ID for a term being created.
     */
    ID_PREFIX(IRI.create("http://purl.obolibrary.org/obo/IAO_0000599"));


    public static ImmutableMap<IRI, IdRangesPolicyVocabulary> byIri() {
        ImmutableMap.Builder<IRI, IdRangesPolicyVocabulary> builder = ImmutableMap.builder();
        for(IdRangesPolicyVocabulary v : IdRangesPolicyVocabulary.values()) {
            builder.put(v.getIri(), v);
        }
        return builder.build();
    }

    private final IRI iri;

    IdRangesPolicyVocabulary(IRI iri) {
        this.iri = iri;
    }

    @Nonnull
    public IRI getIri() {
        return iri;
    }
}


