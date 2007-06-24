package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyRangeFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> {

    public static final String LABEL = "Ranges";


    public OWLDataPropertyRangeFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLDataPropertyRangeAxiom createAxiom(OWLDataRange object) {
        return getOWLDataFactory().getOWLDataPropertyRangeAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLDataRange> getObjectEditor() {
        return new OWLDataRangeEditor(getOWLEditorKit());
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        for (OWLDataPropertyRangeAxiom ax : ontology.getDataPropertyRangeAxiom(getRootObject())) {
            addRow(new OWLDataPropertyRangeFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }


    public void visit(OWLDataPropertyRangeAxiom axiom) {
        if (axiom.getProperty().equals(getRootObject())) {
            reset();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange>> getRowComparator() {
        return null;
    }
}
