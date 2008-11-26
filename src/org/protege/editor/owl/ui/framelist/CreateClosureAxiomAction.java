package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.model.util.ClosureAxiomFactory;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
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
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

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
    class ClosureSourceIdentifier extends OWLObjectVisitorAdapter {

        private final Set<OWLObjectProperty> propertiesToClose = new HashSet<OWLObjectProperty>();

        private Set<OWLObject> visited = new HashSet<OWLObject>();


        public Set<OWLObjectProperty> getPropertiesToClose() {
            return propertiesToClose;
        }


        public void visit(OWLSubClassAxiom owlSubClassAxiom) {
            if (!visited.contains(owlSubClassAxiom)){
                visited.add(owlSubClassAxiom);
                owlSubClassAxiom.getSuperClass().accept(this);
            }
        }


        public void visit(OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
            if (!visited.contains(owlEquivalentClassesAxiom)){
                visited.add(owlEquivalentClassesAxiom);
                for (OWLDescription op : owlEquivalentClassesAxiom.getDescriptions()){
                    op.accept(this);
                }
            }
        }


        public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
            for (OWLDescription op : owlObjectIntersectionOf.getOperands()){
                op.accept(this);
            }
        }


        public void visit(OWLObjectSomeRestriction restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectMinCardinalityRestriction restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectExactCardinalityRestriction restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectValueRestriction restr) {
            restr.getProperty().accept(this);
        }

        public void visit(OWLObjectProperty owlObjectProperty) {
            propertiesToClose.add(owlObjectProperty);
        }
    }
}