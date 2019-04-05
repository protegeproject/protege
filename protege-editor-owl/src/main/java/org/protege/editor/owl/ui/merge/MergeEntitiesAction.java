package org.protege.editor.owl.ui.merge;

import com.google.common.collect.ImmutableSet;
import org.protege.editor.owl.model.merge.MergeEntitiesChangeListGenerator;
import org.protege.editor.owl.ui.action.SelectedOWLEntityAction;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-05
 */
public class MergeEntitiesAction extends SelectedOWLEntityAction {

    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        Optional<OWLEntity> targetEntity = MergeEntitiesPanel.showDialog(getOWLEditorKit());
        targetEntity.ifPresent(entity -> mergeEntities(selectedEntity, entity));
    }

    private void mergeEntities(OWLEntity sourceEntity, OWLEntity targetEntity) {
        OWLOntology rootOntology = getOWLModelManager().getActiveOntology();
        OWLDataFactory dataFactory = getOWLModelManager().getOWLDataFactory();
        MergeEntitiesChangeListGenerator gen = new MergeEntitiesChangeListGenerator(rootOntology,
                                                                                    dataFactory,
                                                                                    ImmutableSet.of(sourceEntity),
                                                                                    targetEntity,
                                                                                    MergeStrategy.DELETE_SOURCE_ENTITY);
        List<OWLOntologyChange> changes = gen.generateChanges();
        getOWLModelManager().applyChanges(changes);
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(targetEntity);

    }

    @Override
    protected void disposeAction() throws Exception {

    }
}
