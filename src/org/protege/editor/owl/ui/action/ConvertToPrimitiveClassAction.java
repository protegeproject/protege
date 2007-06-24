package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.RemoveAxiom;


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
                for (OWLDescription desc : ax.getDescriptions()) {
                    if (!desc.equals(selCls)) {
                        if (desc instanceof OWLObjectIntersectionOf) {
                            for (OWLDescription op : ((OWLObjectIntersectionOf) desc).getOperands()) {
                                changes.add(new AddAxiom(ont, dataFactory.getOWLSubClassAxiom(selCls, op)));
                            }
                        }
                        else {
                            changes.add(new AddAxiom(ont, dataFactory.getOWLSubClassAxiom(selCls, desc)));
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
