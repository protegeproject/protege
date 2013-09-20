package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertySetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDisjointDataPropertiesFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>> {

    public static final String LABEL = "Disjoint With";


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
        for (OWLDisjointDataPropertiesAxiom ax : ontology.getDisjointDataPropertiesAxioms(getRootObject())) {
            addRow(new OWLDisjointDataPropertiesFrameSectionRow(getOWLEditorKit(),
                                                                this,
                                                                ontology,
                                                                getRootObject(),
                                                                ax));
        }
    }


    public OWLObjectEditor<Set<OWLDataProperty>> getObjectEditor() {
        return new OWLDataPropertySetEditor(getOWLEditorKit());
    }
    
    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLDataProperty>> editor) {
    	Set<OWLDataProperty> equivalents = editor.getEditedObject();
    	return !equivalents.contains(getRootObject());
    }
    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLDisjointDataPropertiesAxiom &&
    			((OWLDisjointDataPropertiesAxiom) change.getAxiom()).getProperties().contains(getRootObject());
    }



    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>>> getRowComparator() {
        return null;
    }
}
