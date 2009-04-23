package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.editor.OWLObjectPropertySetEditor;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;

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

    private Set<OWLObjectPropertyExpression> added = new HashSet<OWLObjectPropertyExpression>();


    public OWLDisjointObjectPropertiesFrameSection(OWLEditorKit editorKit,
                                                   OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Disjoint properties", frame);
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLDisjointObjectPropertiesAxiom ax : ontology.getDisjointObjectPropertiesAxiom(getRootObject())) {
            addRow(new OWLDisjointObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                       this,
                                                                       ontology,
                                                                       getRootObject(),
                                                                       ax));
            added.addAll(ax.getProperties());
        }
    }


    protected OWLDisjointObjectPropertiesAxiom createAxiom(Set<OWLObjectProperty> object) {
        Set<OWLObjectProperty> disjoints = new HashSet<OWLObjectProperty>(object);
        disjoints.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(disjoints);
    }


    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    public OWLFrameSectionRowObjectEditor<Set<OWLObjectProperty>> getObjectEditor() {
        return new OWLObjectPropertySetEditor(getOWLEditorKit());
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
}
