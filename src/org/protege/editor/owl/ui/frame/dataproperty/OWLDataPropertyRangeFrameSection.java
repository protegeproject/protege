package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


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


    public OWLFrameSectionRowObjectEditor<OWLDataRange> getObjectEditor() {
        return new OWLDataRangeEditor(getOWLEditorKit());
    }


    protected void clear() {
        addedRanges.clear();
    }


    protected void refill(OWLOntology ontology) {
        for (OWLDataPropertyRangeAxiom ax : ontology.getDataPropertyRangeAxiom(getRootObject())) {
            addRow(new OWLDataPropertyRangeFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            addedRanges.add(ax.getRange());
        }
    }


    protected void refillInferred() {
        try {
            // Inferred stuff
            for (OWLDataRange inferredRange : getInferredRanges()) {
                if (!addedRanges.contains(inferredRange)) {
                    OWLDataPropertyRangeAxiom inferredAxiom = getOWLDataFactory().getOWLDataPropertyRangeAxiom(
                            getRootObject(),
                            inferredRange);
                    addRow(new OWLDataPropertyRangeFrameSectionRow(getOWLEditorKit(),
                                                                     this,
                                                                     null,
                                                                     getRootObject(),
                                                                     inferredAxiom));
                }
                addedRanges.add(inferredRange);
            }
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    private Set<OWLDataRange> getInferredRanges() throws OWLReasonerException {
        return getOWLModelManager().getReasoner().getRanges(getRootObject());
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
