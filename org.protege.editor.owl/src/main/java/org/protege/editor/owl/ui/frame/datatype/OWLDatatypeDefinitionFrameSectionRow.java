package org.protege.editor.owl.ui.frame.datatype;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 5, 2009<br><br>
 */
public class OWLDatatypeDefinitionFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDatatype, OWLDatatypeDefinitionAxiom, OWLDataRange> {

    public OWLDatatypeDefinitionFrameSectionRow(OWLEditorKit owlEditorKit, 
    		                                    OWLFrameSection<OWLDatatype, OWLDatatypeDefinitionAxiom, OWLDataRange> section, 
    		                                    OWLOntology ontology,
                                                OWLDatatype rootObject, OWLDatatypeDefinitionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLDatatypeDefinitionAxiom createAxiom(OWLDataRange range) {
        return getOWLDataFactory().getOWLDatatypeDefinitionAxiom(getRootObject(), range);
    }


    protected OWLObjectEditor<OWLDataRange> getObjectEditor() {
        OWLDataRangeEditor editor = new OWLDataRangeEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getDataRange());
        return editor;
    }


    public List<OWLDataRange> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getDataRange());
    }

}
