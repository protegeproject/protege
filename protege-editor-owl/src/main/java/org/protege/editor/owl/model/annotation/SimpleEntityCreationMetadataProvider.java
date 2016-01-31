package org.protege.editor.owl.model.annotation;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class SimpleEntityCreationMetadataProvider implements EntityCreationMetadataProvider {

    private final List<AnnotationProvider> annotationProviders = new ArrayList<>();

    public SimpleEntityCreationMetadataProvider(List<AnnotationProvider> annotationProviders) {
        this.annotationProviders.addAll(annotationProviders);
    }

    @Override
    public List<OWLOntologyChange> getEntityCreationMetadataChanges(OWLEntity entity, OWLOntology targetOntology, OWLDataFactory df) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for(AnnotationProvider annotationProvider : annotationProviders) {
            Optional<OWLAnnotation> annotation = annotationProvider.getAnnotation(df);
            if(annotation.isPresent()) {
                changes.add(new AddAxiom(
                        targetOntology,
                        df.getOWLAnnotationAssertionAxiom(
                                entity.getIRI(),
                                annotation.get()
                        )
                ));
            }
        }
        return changes;
    }
}
