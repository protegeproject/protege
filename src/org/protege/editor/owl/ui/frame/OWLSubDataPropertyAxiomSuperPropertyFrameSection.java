package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLSubDataPropertyAxiomSuperPropertyFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDataSubPropertyAxiom, OWLDataProperty> {

    public static final String LABEL = "Super properties";


    public OWLSubDataPropertyAxiomSuperPropertyFrameSection(OWLEditorKit editorKit,
                                                            OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLDataSubPropertyAxiom createAxiom(OWLDataProperty object) {
        return getOWLDataFactory().getOWLSubDataPropertyAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        return new OWLDataPropertyEditor(getOWLEditorKit());
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        for (OWLDataSubPropertyAxiom ax : ontology.getDataSubPropertyAxiomsForLHS(getRootObject())) {
            addRow(new OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                           this,
                                                                           ontology,
                                                                           getRootObject(),
                                                                           ax));
        }
    }


    public void visit(OWLDataSubPropertyAxiom axiom) {
        if (axiom.getSubProperty().equals(getRootObject())) {
            reset();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLDataSubPropertyAxiom, OWLDataProperty>> getRowComparator() {
        return null;
    }
}
