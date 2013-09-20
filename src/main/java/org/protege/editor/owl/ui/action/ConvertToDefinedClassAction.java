package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public class ConvertToDefinedClassAction extends SelectedOWLClassAction {

    private static final Logger logger = Logger.getLogger(ConvertToDefinedClassAction.class);


    protected void initialiseAction() throws Exception {
    }


    public void actionPerformed(ActionEvent e) {
        OWLClass selClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLSubClassOfAxiom ax : ont.getSubClassAxiomsForSubClass(selClass)) {
                changes.add(new RemoveAxiom(ont, ax));
                operands.add(ax.getSuperClass());
            }
        }
        if (operands.isEmpty()) {
            return;
        }
        OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        OWLClassExpression equCls;
        if (operands.size() == 1) {
            equCls = operands.iterator().next();
        }
        else {
            equCls = df.getOWLObjectIntersectionOf(operands);
        }
        OWLAxiom ax = df.getOWLEquivalentClassesAxiom(CollectionFactory.createSet(selClass, equCls));
        changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
        getOWLModelManager().applyChanges(changes);
    }
}
