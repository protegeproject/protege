package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.model.util.ClosureAxiomFactory;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 24, 2008<br><br>
 */
public class CreateClosureAxiomAction extends OWLFrameListPopupMenuAction<OWLClass> {


    protected String getName() {
        return "Create closure axiom";
    }


    protected void initialise() throws Exception {
    }


    protected void dispose() throws Exception {
    }


    private Set<OWLObjectProperty> getPropertiesFromSelection() {
        ClosureSourceIdentifier closureSourceIdentifier = new ClosureSourceIdentifier();
        for (Object selVal : getFrameList().getSelectedValues()){
            if (selVal instanceof OWLFrameSectionRow) {
                OWLAxiom ax = ((OWLFrameSectionRow) selVal).getAxiom();
                ax.accept(closureSourceIdentifier);
            }
        }
        return closureSourceIdentifier.getPropertiesToClose();
    }


    protected void updateState() {
        setEnabled(!getPropertiesFromSelection().isEmpty());
    }


    public void actionPerformed(ActionEvent e) {
        List<OWLOntologyChange> changes = new ArrayList<>();

        final OWLOntology activeOnt = getOWLModelManager().getActiveOntology();
        final Set<OWLOntology> activeOnts = getOWLModelManager().getActiveOntologies();
        final OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        final OWLClass root = getRootObject();

        for (OWLObjectProperty prop : getPropertiesFromSelection()){
            OWLAxiom ax = ClosureAxiomFactory.getClosureAxiom(root, prop, df, activeOnts);
            if (ax != null && !activeOnt.containsAxiom(ax)){
                changes.add(new AddAxiom(activeOnt, ax));
            }
        }
        if (!changes.isEmpty()){
            getOWLModelManager().applyChanges(changes);
        }
    }


    /**
     * Gets the properties of some, min and exact restrictions from super or equivalent class axioms
     */
    class ClosureSourceIdentifier implements OWLObjectVisitor {

        private final Set<OWLObjectProperty> propertiesToClose = new HashSet<>();

        private Set<OWLObject> visited = new HashSet<>();


        public Set<OWLObjectProperty> getPropertiesToClose() {
            return propertiesToClose;
        }


        public void visit(OWLSubClassOfAxiom owlSubClassAxiom) {
            if (!visited.contains(owlSubClassAxiom)){
                visited.add(owlSubClassAxiom);
                owlSubClassAxiom.getSuperClass().accept(this);
            }
        }


        public void visit(OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
            if (!visited.contains(owlEquivalentClassesAxiom)){
                visited.add(owlEquivalentClassesAxiom);
                for (OWLClassExpression op : owlEquivalentClassesAxiom.getClassExpressions()){
                    op.accept(this);
                }
            }
        }


        public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
            for (OWLClassExpression op : owlObjectIntersectionOf.getOperands()){
                op.accept(this);
            }
        }


        public void visit(OWLObjectSomeValuesFrom restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectMinCardinality restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectExactCardinality restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectHasValue restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectProperty owlObjectProperty) {
            propertiesToClose.add(owlObjectProperty);
        }
    }
}
