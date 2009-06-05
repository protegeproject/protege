package org.protege.editor.owl.ui.frame.datatype;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDatatype;
import org.semanticweb.owl.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;
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
 * Date: Jun 5, 2009<br><br>
 */
public class OWLDatatypeDefinitionFrameSection extends AbstractOWLFrameSection<OWLDatatype, OWLDatatypeDefinitionAxiom, OWLDataRange> {

    public static final String LABEL = "Datatype Definitions";

    public OWLDatatypeDefinitionFrameSection(OWLEditorKit editorKit, OWLFrame<OWLDatatype> frame) {
        super(editorKit, LABEL, "Datatype Definition", frame);
    }


    protected OWLDatatypeDefinitionAxiom createAxiom(OWLDataRange range) {
        return getOWLDataFactory().getOWLDatatypeDefinitionAxiom(getRootObject(), range);
    }


    public OWLObjectEditor<OWLDataRange> getObjectEditor() {
        return new OWLDataRangeEditor(getOWLEditorKit());
    }


    protected void refill(OWLOntology ontology) {
        for (OWLDatatypeDefinitionAxiom ax : ontology.getDatatypeDefinitions(getRootObject())) {
            addRow(new OWLDatatypeDefinitionFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }


    protected void clear() {
    }


    public Comparator<OWLFrameSectionRow<OWLDatatype, OWLDatatypeDefinitionAxiom, OWLDataRange>> getRowComparator() {
        return null;
    }
}
