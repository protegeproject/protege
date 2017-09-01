package org.protege.editor.owl.model.conf;

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

    /**
     * Expands a string representation of an IRI into an IRI.  OBO Library Identifiers are handled as are built in
     * OWL IRIs.  This means that IAO:0000115 is expanded to http://purl.obolibrary.org/obo/IAO_0000115 for example,
     * and rdfs:label is expanded into its full IRI for example.
     * @param iri The string representation of the IRI.
     * @return The expanded IRI.  If the string representation is null then the empty value is returned.
     */
    public static Optional<IRI> expand(@Nullable String iri) {
        if(iri == null) {
            return Optional.empty();
        }
        if(OboUtilities.isOboId(iri)) {
            return Optional.of(OboUtilities.getOboLibraryIriFromOboId(iri));
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
