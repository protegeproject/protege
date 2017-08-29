package org.protege.editor.owl.model.deprecation;

import org.protege.editor.owl.model.util.OboUtilities;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class IRIExpander {

    private static Map<String, IRI> builtInVocabulary = new HashMap<>();

    static {
        for(OWLRDFVocabulary v : OWLRDFVocabulary.values()) {
            builtInVocabulary.put(v.getPrefixedName(), v.getIRI());
        }
        for(SKOSVocabulary v : SKOSVocabulary.values()) {
            builtInVocabulary.put(v.getPrefixedName(), v.getIRI());
        }
    }

    public static Optional<IRI> expand(@Nullable String iri) {
        if(iri == null) {
            return Optional.empty();
        }
        if(OboUtilities.isOboId(iri)) {
            return Optional.of(OboUtilities.getIriFromOboId(iri));
        }
        IRI builtInIri = builtInVocabulary.get(iri);
        if(builtInIri != null) {
            return Optional.of(builtInIri);
        }
        else {
            return Optional.of(IRI.create(iri));
        }
    }
}
