package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLPropertySetEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import java.util.Comparator;
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
 * Date: Jun 4, 2009<br><br>
 */
public class OWLKeySection extends AbstractOWLFrameSection<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>> {

    public static final String LABEL = "Keys";


    public OWLKeySection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, "Key", frame);
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        for (OWLHasKeyAxiom ax : ontology.getHasKeyAxioms(getRootObject())) {
            addRow(new OWLKeyAxiomFrameSectionRow(getOWLEditorKit(),
                                                  this,
                                                  ontology,
                                                  getRootObject(),
                                                  ax));
        }
    }


    protected OWLHasKeyAxiom createAxiom(Set<OWLPropertyExpression> properties) {
        return getOWLDataFactory().getOWLHasKeyAxiom(getRootObject(), properties);
    }


    public void visit(OWLHasKeyAxiom axiom) {
        if (axiom.getClassExpression().equals(getRootObject())) {
            reset();
        }
    }


    public OWLObjectEditor<Set<OWLPropertyExpression>> getObjectEditor() {
        return new OWLPropertySetEditor(getOWLEditorKit());
    }


    public Comparator<OWLFrameSectionRow<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>>> getRowComparator() {
        return null;
    }
}
