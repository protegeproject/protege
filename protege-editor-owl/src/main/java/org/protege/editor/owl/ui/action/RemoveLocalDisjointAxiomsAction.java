package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
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
public class RemoveLocalDisjointAxiomsAction extends SelectedOWLClassAction {

    private final Logger logger = LoggerFactory.getLogger(RemoveAllDisjointAxiomsAction.class);


    public void actionPerformed(ActionEvent e) {
        try {
            UIHelper uiHelper = new UIHelper(getOWLEditorKit());
            int result = uiHelper.showOptionPane("Include imported ontologies?",
                                                 "Do you want to remove the disjoint classes axioms from " +
                                                         "imported ontologies?",
                                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);

            Set<OWLOntology> ontologies = new HashSet<>();
            if (result == JOptionPane.YES_OPTION) {
                ontologies.addAll(getOWLModelManager().getActiveOntologies());
            }
            else if (result == JOptionPane.NO_OPTION) {
                ontologies = Collections.singleton(getOWLModelManager().getActiveOntology());
            }

            List<OWLOntologyChange> changes = new ArrayList<>();
            for (OWLClass desc : getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(getOWLClass())){
                changes.addAll(removeDisjointsForClass(desc, ontologies));
            }

            getOWLModelManager().applyChanges(changes);
        }
        catch (Exception ex) {
            logger.error("An error occurred whilst removing the disjoint axioms from the specified ontologies.", ex);
        }
    }


    @SuppressWarnings("unchecked")
    private List<OWLOntologyChange> removeDisjointsForClass(OWLClass owlClass, Set<OWLOntology> ontologies) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLOntology ont : ontologies) {
            for (OWLDisjointClassesAxiom ax : ont.getAxioms(AxiomType.DISJOINT_CLASSES)) {
                if (ax.getClassExpressions().contains(owlClass)){
                    changes.add(new RemoveAxiom(ont, ax));
                }
            }
        }
        return changes;
    }


    protected void initialiseAction() throws Exception {
        // do nothing
    }
}