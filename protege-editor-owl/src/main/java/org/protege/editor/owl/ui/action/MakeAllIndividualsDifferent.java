package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.refactor.AllDifferentCreator;
import org.semanticweb.owlapi.model.HasIndividualsInSignature;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import java.awt.event.ActionEvent;
import java.util.List;

import static org.protege.editor.owl.model.event.EventType.ACTIVE_ONTOLOGY_CHANGED;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MakeAllIndividualsDifferent extends ProtegeOWLAction {

    private final OWLOntologyChangeListener changeListener = this::handleOntologyChanges;

    private final OWLModelManagerListener modelManagerListener = event -> {
        if (event.isType(ACTIVE_ONTOLOGY_CHANGED)) {
            updateState();
        }
    };

    public void actionPerformed(ActionEvent e) {
        OWLModelManager modelManager = getOWLModelManager();
        OWLOntology activeOntology = modelManager.getActiveOntology();
        AllDifferentCreator creator = new AllDifferentCreator(modelManager.getOWLDataFactory(),
                                                              activeOntology,
                                                              modelManager.getActiveOntologies());
        modelManager.applyChanges(creator.getChanges());
        updateState();
    }


    public void initialise() throws Exception {
        getOWLModelManager().addListener(modelManagerListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        updateState();
    }


    public void dispose() {
        getOWLModelManager().removeListener(modelManagerListener);
        getOWLModelManager().removeOntologyChangeListener(changeListener);
    }

    private void updateState() {
        boolean enabled = getOWLModelManager().getActiveOntologies().stream()
                                              .filter(o -> !o.getIndividualsInSignature().isEmpty())
                                              .findAny()
                                              .isPresent();
        setEnabled(enabled);
    }

    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        changes.stream()
               .filter(OWLOntologyChange::isAxiomChange)
               .map(OWLOntologyChange::getAxiom)
               .map(HasIndividualsInSignature::getIndividualsInSignature)
               .filter(inds -> !inds.isEmpty())
               .findAny()
               .ifPresent(ax -> updateState());
    }

}
