package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLPropertyAssertionRowComparator;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Feb-2007<br><br>
 */
public class OWLNegativeDataPropertyAssertionFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLNegativeDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    public static final String LABEL = "Negative data property assertions";

    private OWLDataPropertyRelationshipEditor editor;


    public OWLNegativeDataPropertyAssertionFrameSection(OWLEditorKit editorKit,
                                                        OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, "Negative data property assertion", frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLNegativeDataPropertyAssertionAxiom ax : ontology.getNegativeDataPropertyAssertionAxioms(getRootObject()))
        {
            addRow(new OWLNegativeDataPropertyAssertionFrameSectionRow(getOWLEditorKit(),
                                                                       this,
                                                                       ontology,
                                                                       getRootObject(),
                                                                       ax));
        }
    }


    protected OWLNegativeDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair object) {
        return getOWLDataFactory().getOWLNegativeDataPropertyAssertionAxiom(object.getProperty(),
                                                                            getRootObject(),
                                                                            object.getConstant());
    }


    public OWLObjectEditor<OWLDataPropertyConstantPair> getObjectEditor() {
        if (editor == null) {
            editor = new OWLDataPropertyRelationshipEditor(getOWLEditorKit());
        }
        return editor;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLNegativeDataPropertyAssertionAxiom, OWLDataPropertyConstantPair>> getRowComparator() {
        return new OWLPropertyAssertionRowComparator<>(getOWLModelManager().getOWLObjectComparator());
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLNegativeDataPropertyAssertionAxiom) {
    		return ((OWLNegativeDataPropertyAssertionAxiom) axiom).getSubject().equals(getRootObject());
    	}
    	return false;
    }

}
