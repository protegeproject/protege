package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDisjointDataPropertiesFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDisjointDataPropertiesAxiom, OWLDataProperty> {

    public static final String LABEL = "Disjoint properties";


    public OWLDisjointDataPropertiesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLDisjointDataPropertiesAxiom createAxiom(OWLDataProperty object) {
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                 object));
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        for (OWLDisjointDataPropertiesAxiom ax : ontology.getDisjointDataPropertiesAxiom(getRootObject())) {
            addRow(new OWLDisjointDataPropertiesFrameSectionRow(getOWLEditorKit(),
                                                                this,
                                                                ontology,
                                                                getRootObject(),
                                                                ax));
        }
    }


    public OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        return new OWLDataPropertyEditor(getOWLEditorKit());
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, OWLDataProperty>> getRowComparator() {
        return null;
    }
}
