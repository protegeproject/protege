package org.protege.editor.owl.model.annotation;

import java.util.Collections;
import java.util.List;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class NullEntityCreationMetadataProvider implements EntityCreationMetadataProvider {

    @Override
    public List<OWLOntologyChange> getEntityCreationMetadataChanges(OWLEntity entity, OWLOntology targetOntology, OWLDataFactory df) {
        return Collections.emptyList();
    }
}
