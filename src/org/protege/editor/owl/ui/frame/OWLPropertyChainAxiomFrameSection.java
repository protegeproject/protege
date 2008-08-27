package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLPropertyChainAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLObjectPropertyChainSubPropertyAxiom, List<OWLObjectPropertyExpression>> {

    public static final String LABEL = "Property chains";


    public OWLPropertyChainAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLObjectProperty> frame) {
        super(owlEditorKit, LABEL, frame);
        setCacheEditor(false); // needs to be recreated every time
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLObjectPropertyChainSubPropertyAxiom ax : ontology.getPropertyChainSubPropertyAxioms()) {
            if (ax.getSuperProperty().equals(getRootObject())) {
                addRow(new OWLPropertyChainAxiomFrameSectionRow(getOWLEditorKit(),
                                                                this,
                                                                ontology,
                                                                getRootObject(),
                                                                ax));
            }
        }
    }


    protected OWLObjectPropertyChainSubPropertyAxiom createAxiom(List<OWLObjectPropertyExpression> object) {
        return getOWLDataFactory().getOWLObjectPropertyChainSubPropertyAxiom(object, getRootObject());
    }


    public OWLFrameSectionRowObjectEditor<List<OWLObjectPropertyExpression>> getObjectEditor() {
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
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLObjectPropertyChainSubPropertyAxiom, List<OWLObjectPropertyExpression>>> getRowComparator() {
        return null;
    }


    public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
        if (axiom.getSuperProperty().equals(getRootObject())) {
            reset();
        }
    }
}
