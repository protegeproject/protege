package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.stream.Collectors;

import static javax.swing.JOptionPane.*;
import static org.semanticweb.owlapi.model.AxiomType.DISJOINT_CLASSES;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RemoveAllDisjointAxiomsAction extends ProtegeOWLAction {

    private final Logger logger = LoggerFactory.getLogger(RemoveAllDisjointAxiomsAction.class);

    private OWLModelManagerListener listener = event -> {
        if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
            updateState();
        }
    };

    private OWLOntologyChangeListener changeListener = this::handleOntologyChanges;


    public void actionPerformed(ActionEvent e) {
        try {
            UIHelper uiHelper = new UIHelper(getOWLEditorKit());
            int result = uiHelper.showOptionPane("Remove axioms from imported ontologies?",
                                                 "Do you want to remove the disjoint classes axioms from " +
                                                         "imported ontologies?",
                                                 YES_NO_CANCEL_OPTION,
                                                 QUESTION_MESSAGE);

            Set<OWLOntology> ontologies = new HashSet<>();
            if (result == YES_OPTION) {
                ontologies.addAll(getOWLModelManager().getActiveOntologies());
            }
            else if (result == NO_OPTION) {
                ontologies = Collections.singleton(getOWLModelManager().getActiveOntology());
            }
            List<OWLOntologyChange> changes = new ArrayList<>();
            ontologies.forEach(o -> {
                changes.addAll(o.getAxioms(DISJOINT_CLASSES)
                                  .stream()
                                  .map(ax -> new RemoveAxiom(o, ax))
                                  .collect(Collectors.toList()));

            });
            getOWLModelManager().applyChanges(changes);
        }
        catch (Exception ex) {
            logger.error("An error occurred whilst attempting to remove all disjoint classes axioms.", ex);
        }
    }


    private void updateState() {
        boolean containsDisjointClassesAxioms = getOWLModelManager().getActiveOntologies().stream()
                                                                    .filter(o -> o.getAxiomCount(DISJOINT_CLASSES) > 0)
                                                                    .findAny().isPresent();
        setEnabled(getOWLModelManager().isActiveOntologyMutable() && containsDisjointClassesAxioms);
    }

    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        changes.stream()
                .filter(OWLOntologyChange::isAxiomChange)
                .map(OWLOntologyChange::getAxiom)
                .filter(ax -> ax.getAxiomType() == DISJOINT_CLASSES)
                .findAny().ifPresent(ax -> updateState());
    }


    public void initialise() throws Exception {
        getOWLModelManager().addListener(listener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        updateState();
    }


    public void dispose() {
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().removeOntologyChangeListener(changeListener);
    }
}
