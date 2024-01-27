package org.protege.editor.owl.ui.action;

import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static org.semanticweb.owlapi.model.AxiomType.DISJOINT_CLASSES;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
