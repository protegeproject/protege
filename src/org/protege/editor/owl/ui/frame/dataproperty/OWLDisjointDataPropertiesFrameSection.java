package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.editor.OWLDataPropertySetEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDisjointDataPropertiesFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>> {

    public static final String LABEL = "Disjoint properties";


    public OWLDisjointDataPropertiesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, "Disjoint properties", frame);
    }


    protected OWLDisjointDataPropertiesAxiom createAxiom(Set<OWLDataProperty> object) {
        Set<OWLDataProperty> disjoints = new HashSet<OWLDataProperty>(object);
        disjoints.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(disjoints);
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


    public OWLFrameSectionRowObjectEditor<Set<OWLDataProperty>> getObjectEditor() {
        return new OWLDataPropertySetEditor(getOWLEditorKit());
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>>> getRowComparator() {
        return null;
    }
}
