package org.protege.editor.owl.ui.frame.datatype;

import java.util.Comparator;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

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

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLDatatypeDefinitionAxiom &&
    			((OWLDatatypeDefinitionAxiom) change.getAxiom()).getDatatype().equals(getRootObject());
    }
}
