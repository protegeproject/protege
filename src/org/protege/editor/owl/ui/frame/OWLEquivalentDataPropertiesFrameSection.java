package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLEquivalentDataPropertiesFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty> {

    public static final String LABEL = "Equivalent properties";


    protected void clear() {
    }


    public OWLEquivalentDataPropertiesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, frame);
    }


    protected OWLEquivalentDataPropertiesAxiom createAxiom(OWLDataProperty object) {
        return getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                   object));
    }


    public OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        return new OWLDataPropertyEditor(getOWLEditorKit());
    }


    protected void refill(OWLOntology ontology) {
        for (OWLEquivalentDataPropertiesAxiom ax : ontology.getEquivalentDataPropertiesAxiom(getRootObject())) {
            addRow(new OWLEquivalentDataPropertiesFrameSectionRow(getOWLEditorKit(),
                                                                  this,
                                                                  ontology,
                                                                  getRootObject(),
                                                                  ax));
        }
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty>> getRowComparator() {
        return null;
    }
}
