package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RemoveLocalDisjointAxiomsAction extends SelectedOWLClassAction {

    private static final Logger logger = Logger.getLogger(RemoveAllDisjointAxiomsAction.class);


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
            for (OWLClass desc : getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(getOWLClass())){
                changes.addAll(removeDisjointsForClass(desc, ontologies));
            }

            getOWLModelManager().applyChanges(changes);
        }
        catch (Exception e1) {
            logger.error(e1);
        }
    }


    @SuppressWarnings("unchecked")
    private List<OWLOntologyChange> removeDisjointsForClass(OWLClass owlClass, Set<OWLOntology> ontologies) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
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