package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyRangeFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> {

    public static final String LABEL = "Ranges";

    private Set<OWLDataRange> addedRanges = new HashSet<OWLDataRange>();


    public OWLDataPropertyRangeFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, "Range", frame);
    }


    protected OWLDataPropertyRangeAxiom createAxiom(OWLDataRange object) {
        return getOWLDataFactory().getOWLDataPropertyRangeAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLDataRange> getObjectEditor() {
        return new OWLDataRangeEditor(getOWLEditorKit());
    }


    protected void clear() {
        addedRanges.clear();
    }


    protected void refill(OWLOntology ontology) {
        for (OWLDataPropertyRangeAxiom ax : ontology.getDataPropertyRangeAxioms(getRootObject())) {
            addRow(new OWLDataPropertyRangeFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            addedRanges.add(ax.getRange());
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
