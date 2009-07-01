package org.protege.editor.owl.ui.frame.annotationproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.IRIAnnotationValueEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.*;

import java.util.Arrays;
import java.util.List;
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
public class OWLAnnotationPropertyDomainFrameSectionRow extends AbstractOWLFrameSectionRow<OWLAnnotationProperty, OWLAnnotationPropertyDomainAxiom, IRI> {

    public OWLAnnotationPropertyDomainFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                OWLOntology ontology, OWLAnnotationProperty property,
                                                OWLAnnotationPropertyDomainAxiom axiom) {
        super(owlEditorKit, section, ontology, property, axiom);
    }


    protected IRIAnnotationValueEditor getObjectEditor() {
        final IRIAnnotationValueEditor editor = new IRIAnnotationValueEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getDomain());
        return editor;
    }


    protected OWLAnnotationPropertyDomainAxiom createAxiom(IRI iri) {
        return getOWLDataFactory().getOWLAnnotationPropertyDomainAxiom(getRootObject(), iri);
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getDomain());
    }
}
