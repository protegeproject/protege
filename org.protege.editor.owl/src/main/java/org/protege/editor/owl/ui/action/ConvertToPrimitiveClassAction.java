package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ConvertToPrimitiveClassAction extends SelectedOWLClassAction {


    public void actionPerformed(ActionEvent e) {
        // TODO: Factor this out into some kind of API util
        OWLClass selCls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        if (selCls == null) {
            return;
        }
        OWLDataFactory dataFactory = getOWLModelManager().getOWLDataFactory();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLEquivalentClassesAxiom ax : ont.getEquivalentClassesAxioms(selCls)) {
                changes.add(new RemoveAxiom(ont, ax));
                for (OWLClassExpression desc : ax.getClassExpressions()) {
                    if (!desc.equals(selCls)) {
                        if (desc instanceof OWLObjectIntersectionOf) {
                            for (OWLClassExpression op : ((OWLObjectIntersectionOf) desc).getOperands()) {
                                changes.add(new AddAxiom(ont, dataFactory.getOWLSubClassOfAxiom(selCls, op)));
                            }
                        }
                        else {
                            changes.add(new AddAxiom(ont, dataFactory.getOWLSubClassOfAxiom(selCls, desc)));
                        }
                    }
                }
            }
        }
        getOWLModelManager().applyChanges(changes);
    }


    protected void updateState() {
        OWLEntity selEnt = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        setEnabled(selEnt instanceof OWLClass);
    }


    protected void initialiseAction() throws Exception {
    }


    public void dispose() {
    }
}
