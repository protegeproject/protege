package org.protege.editor.owl.ui.frame.annotationproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.*;
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
public class OWLSubAnnotationPropertyFrameSection extends AbstractOWLFrameSection<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty> {

    public static final String LABEL = "Superproperties";

    private Set<OWLAnnotationProperty> added = new HashSet<OWLAnnotationProperty>();

    private OWLAnnotationPropertyEditor editor;


    public OWLSubAnnotationPropertyFrameSection(OWLEditorKit editorKit, OWLFrame<OWLAnnotationProperty> frame) {
        super(editorKit, LABEL, "Superproperties", frame);
    }


    protected OWLSubAnnotationPropertyOfAxiom createAxiom(OWLAnnotationProperty superProp) {
        return getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(getRootObject(), superProp);
    }


    protected Set<OWLSubAnnotationPropertyOfAxiom> getAxioms(OWLOntology ontology) {
        return ontology.getSubAnnotationPropertyOfAxioms(getRootObject());
    }


    public OWLObjectEditor<OWLAnnotationProperty> getObjectEditor() {
        if (editor == null){
            editor = new OWLAnnotationPropertyEditor(getOWLEditorKit());
        }
        return editor;
    }


    public final boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLAnnotationProperty)) {
                return false;
            }
        }
        return true;
    }


    public final boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLAnnotationProperty) {
                OWLAnnotationProperty property = (OWLAnnotationProperty) obj;
                OWLAxiom ax = createAxiom(property);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    protected void clear() {
        added.clear();
    }


    protected final void refill(OWLOntology ontology) {
        for (OWLSubAnnotationPropertyOfAxiom ax : getAxioms(ontology)) {
            addRow(new OWLSubAnnotationPropertyFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            added.add(ax.getSuperProperty());
        }
    }


    protected final void refillInferred() {
        // Could do some trivial manual inference to aid the user
    }


    public Comparator<OWLFrameSectionRow<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty>> getRowComparator() {
        return null;
    }


    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().equals(getRootObject())) {
            reset();
        }
    }
}
