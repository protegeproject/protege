package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyTabbedSetEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLDisjointObjectPropertiesFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectPropertyExpression>> {

    public static final String LABEL = "Disjoint With";

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


    protected OWLDisjointObjectPropertiesAxiom createAxiom(Set<OWLObjectPropertyExpression> object) {
        Set<OWLObjectPropertyExpression> disjoints = new HashSet<>(object);
        disjoints.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(disjoints);
    }
    
    public OWLObjectEditor<Set<OWLObjectPropertyExpression>> getObjectEditor() {
        return new OWLObjectPropertyTabbedSetEditor(getOWLEditorKit());
    }


    @Override
	public boolean checkEditorResults(OWLObjectEditor<Set<OWLObjectPropertyExpression>> editor) {
		Set<OWLObjectPropertyExpression> equivalents = editor.getEditedObject();
		return !equivalents.contains(getRootObject());
	}


	/**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectPropertyExpression>>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLDisjointObjectPropertiesAxiom &&
    			((OWLDisjointObjectPropertiesAxiom) change.getAxiom()).getProperties().contains(getRootObject());
    }


}
