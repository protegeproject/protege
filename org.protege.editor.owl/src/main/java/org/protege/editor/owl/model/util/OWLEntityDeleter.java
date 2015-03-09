package org.protege.editor.owl.model.util;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class OWLEntityDeleter {

    public static void deleteEntities(Iterable<? extends OWLEntity> entities, OWLModelManager modelManager) {
        List<OWLOntologyChange> changeList = new ArrayList<>();
        for(OWLEntity entity : entities) {
            for(OWLOntology ontology : modelManager.getOntologies()) {
                OWLObjectRemover remover = new OWLObjectRemover();
                changeList.addAll(remover.getChangesToRemoveObject(entity, ontology));
            }
        }
        modelManager.applyChanges(changeList);
    }
}
