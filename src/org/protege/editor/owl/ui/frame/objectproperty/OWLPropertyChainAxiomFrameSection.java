package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyChainEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLPropertyChainAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLSubPropertyChainOfAxiom, List<OWLObjectPropertyExpression>> {

    public static final String LABEL = "Property chains";


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


    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        if (axiom.getSuperProperty().equals(getRootObject())) {
            reset();
        }
    }
}
