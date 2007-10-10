package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.RemoveAxiom;


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

    private static final Logger logger = Logger.getLogger(RemoveAllDisjointAxiomsAction.class);

    private OWLModelManagerListener listener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                updateState();
            }
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
                for (OWLClassAxiom ax : ont.getClassAxioms()) {
                    if (ax instanceof OWLDisjointClassesAxiom) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                }
            }
            getOWLModelManager().applyChanges(changes);
        }
        catch (Exception e1) {
            logger.error(e1);
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
        getOWLModelManager().addListener(listener);
    }
}
