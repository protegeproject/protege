package org.protege.editor.owl.model.annotation;

import java.util.Optional;

import org.protege.editor.owl.model.user.Orcid;
import org.protege.editor.owl.model.user.OrcidProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public class OrcidAnnotationValueProvider implements AnnotationValueProvider {

    private final OrcidProvider orcidProvider;

    public OrcidAnnotationValueProvider(OrcidProvider orcidProvider) {
        this.orcidProvider = orcidProvider;
    }

    @Override
    public Optional<OWLAnnotationValue> getAnnotationValue(OWLDataFactory dataFactory) {
        Optional<Orcid> orcid = orcidProvider.getOrcid();
        if(!orcid.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(IRI.create(Orcid.ORCID_URI_PREFIX + orcid.get().getValue()));
    }
}
