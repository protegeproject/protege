package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLPropertySetEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLKeySection extends AbstractOWLFrameSection<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>> {

    public static final String LABEL = "Target for Key";


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
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
    	return getOWLDataFactory().getOWLHasKeyAxiom(getRootObject(), (Set) properties);
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLHasKeyAxiom) {
    		return ((OWLHasKeyAxiom) axiom).getClassExpression().equals(getRootObject());
    	}
    	return false;
    }



    public OWLObjectEditor<Set<OWLPropertyExpression>> getObjectEditor() {
        return new OWLPropertySetEditor(getOWLEditorKit());
    }


    public Comparator<OWLFrameSectionRow<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>>> getRowComparator() {
        return null;
    }
}
