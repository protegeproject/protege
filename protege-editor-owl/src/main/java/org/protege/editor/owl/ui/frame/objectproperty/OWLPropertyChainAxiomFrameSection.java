package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.Comparator;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyChainEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLPropertyChainAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLSubPropertyChainOfAxiom, List<OWLObjectPropertyExpression>> {

    public static final String LABEL = "SuperProperty Of (Chain)";


    public OWLPropertyChainAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLObjectProperty> frame) {
        super(owlEditorKit, LABEL, "Property chain", frame);
        setCacheEditor(false); // needs to be recreated every time
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLSubPropertyChainOfAxiom ax : ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
            if (ax.getSuperProperty().equals(getRootObject())) {
                addRow(new OWLPropertyChainAxiomFrameSectionRow(getOWLEditorKit(),
                                                                this,
                                                                ontology,
                                                                getRootObject(),
                                                                ax));
            }
        }
    }


    protected OWLSubPropertyChainOfAxiom createAxiom(List<OWLObjectPropertyExpression> object) {
        return getOWLDataFactory().getOWLSubPropertyChainOfAxiom(object, getRootObject());
    }


    public OWLObjectEditor<List<OWLObjectPropertyExpression>> getObjectEditor() {
        OWLObjectPropertyChainEditor editor = new OWLObjectPropertyChainEditor(getOWLEditorKit());
        editor.setSuperProperty(getRootObject());
        return editor;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLSubPropertyChainOfAxiom, List<OWLObjectPropertyExpression>>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLSubPropertyChainOfAxiom) {
    		return ((OWLSubPropertyChainOfAxiom) axiom).getSuperProperty().equals(getRootObject());
    	}
    	return false;
    }
}
