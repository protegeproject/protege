package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.inference.OWLReasonerAdapter;
import org.semanticweb.owlapi.inference.OWLReasonerException;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
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
public class OWLInverseObjectPropertiesAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty> {

    public static final String LABEL = "Inverse properties";

    private Set<OWLObjectPropertyExpression> added = new HashSet<OWLObjectPropertyExpression>();


    public OWLInverseObjectPropertiesAxiomFrameSection(OWLEditorKit editorKit,
                                                       OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Inverse property", frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLInverseObjectPropertiesAxiom ax : ontology.getInverseObjectPropertyAxioms(getRootObject())) {
            addRow(new OWLInverseObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                      this,
                                                                      ontology,
                                                                      getRootObject(),
                                                                      ax));
            added.addAll(ax.getProperties());
        }
    }


    protected void refillInferred() throws OWLReasonerException {
        final Set<OWLObjectProperty> infInverses = new HashSet<OWLObjectProperty>(OWLReasonerAdapter.flattenSetOfSets(getReasoner().getInverseProperties(getRootObject())));
        infInverses.removeAll(added);
        for (OWLObjectProperty invProp : infInverses) {
            final OWLInverseObjectPropertiesAxiom ax = getOWLDataFactory().getOWLInverseObjectPropertiesAxiom(
                    getRootObject(),
                    invProp);
                addRow(new OWLInverseObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                          this,
                                                                          null,
                                                                          getRootObject(),
                                                                          ax));
        }
    }


    protected OWLInverseObjectPropertiesAxiom createAxiom(OWLObjectProperty object) {
        return getOWLDataFactory().getOWLInverseObjectPropertiesAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLObjectProperty> getObjectEditor() {
        return new OWLObjectPropertyEditor(getOWLEditorKit());
    }


    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        if (axiom.getProperties().contains(getRootObject())) {
            reset();
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty>> getRowComparator() {
        return null;
    }
}
