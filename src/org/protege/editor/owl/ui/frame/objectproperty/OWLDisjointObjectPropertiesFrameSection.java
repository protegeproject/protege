package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertySetEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLDisjointObjectPropertiesFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectProperty>> {

    public static final String LABEL = "Disjoint properties";

//    private Set<OWLObjectPropertyExpression> added = new HashSet<OWLObjectPropertyExpression>();


    public OWLDisjointObjectPropertiesFrameSection(OWLEditorKit editorKit,
                                                   OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Disjoint properties", frame);
    }


    protected void clear() {
//        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLDisjointObjectPropertiesAxiom ax : ontology.getDisjointObjectPropertiesAxioms(getRootObject())) {
            addRow(new OWLDisjointObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                       this,
                                                                       ontology,
                                                                       getRootObject(),
                                                                       ax));
//            added.addAll(ax.getProperties());
        }
    }


    protected OWLDisjointObjectPropertiesAxiom createAxiom(Set<OWLObjectProperty> object) {
        Set<OWLObjectProperty> disjoints = new HashSet<OWLObjectProperty>(object);
        disjoints.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(disjoints);
    }
    
    public OWLObjectEditor<Set<OWLObjectProperty>> getObjectEditor() {
        return new OWLObjectPropertySetEditor(getOWLEditorKit());
    }


    @Override
	public boolean checkEditorResults(OWLObjectEditor<Set<OWLObjectProperty>> editor) {
		Set<OWLObjectProperty> equivalents = editor.getEditedObject();
		return !equivalents.contains(getRootObject());
	}


	/**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectProperty>>> getRowComparator() {
        return null;
    }


	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
	    if (axiom.getProperties().contains(getRootObject())) {
	        reset();
	    }
	}
}
