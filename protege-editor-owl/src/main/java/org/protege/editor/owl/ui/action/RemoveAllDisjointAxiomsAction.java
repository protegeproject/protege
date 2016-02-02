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


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Jun-2006<br><br>
 * <p/>
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


    public void actionPerformed(ActionEvent e) {
        try {
            UIHelper uiHelper = new UIHelper(getOWLEditorKit());
            int result = uiHelper.showOptionPane("Include imported ontologies?",
                                                 "Do you want to remove the disjoint axioms from \n" + "imported ontologies?",
                                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);

            Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
            if (result == JOptionPane.YES_OPTION) {
                ontologies.addAll(getOWLModelManager().getActiveOntologies());
            }
            else if (result == JOptionPane.NO_OPTION) {
                ontologies = Collections.singleton(getOWLModelManager().getActiveOntology());
            }
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (OWLOntology ont : ontologies) {
                for (OWLDisjointClassesAxiom ax : ont.getAxioms(AxiomType.DISJOINT_CLASSES)) {
                    changes.add(new RemoveAxiom(ont, ax));
                }
            }
            getOWLModelManager().applyChanges(changes);
        }
        catch (Exception ex) {
            logger.error("An error occurred whilst attempting to remove all disjoint classes axioms.", ex);
        }
    }


    private void updateState() {
        setEnabled(getOWLModelManager().isActiveOntologyMutable());
    }


    public void initialise() throws Exception {
        getOWLModelManager().addListener(listener);
        updateState();
    }


    public void dispose() {
        getOWLModelManager().removeListener(listener);
    }
}
