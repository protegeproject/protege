package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.util.CollectionFactory;


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
        Set<OWLDescription> operands = new HashSet<OWLDescription>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLSubClassAxiom ax : ont.getSubClassAxiomsForLHS(selClass)) {
                changes.add(new RemoveAxiom(ont, ax));
                operands.add(ax.getSuperClass());
            }
        }
        if (operands.isEmpty()) {
            return;
        }
        OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        OWLDescription equCls;
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
