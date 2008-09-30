package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLSubDataPropertyAxiomSuperPropertyFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLDataSubPropertyAxiom, OWLDataProperty> {

    public static final String LABEL = "Super properties";

    private Set<OWLDataSubPropertyAxiom> added = new HashSet<OWLDataSubPropertyAxiom>();


    public OWLSubDataPropertyAxiomSuperPropertyFrameSection(OWLEditorKit editorKit,
                                                            OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, "Super property", frame);
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
        added.clear();
        for (OWLDataSubPropertyAxiom ax : ontology.getDataSubPropertyAxiomsForLHS(getRootObject())) {
            addRow(new OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                           this,
                                                                           ontology,
                                                                           getRootObject(),
                                                                           ax));
            added.add(ax);
        }
    }


    protected void refillInferred() {
        try {
            for (OWLDataPropertyExpression infSup : OWLReasonerAdapter.flattenSetOfSets(getOWLModelManager().getReasoner().getSuperProperties(getRootObject()))) {
                if (!added.contains(infSup)) {
                    final OWLDataSubPropertyAxiom ax = getOWLDataFactory().getOWLSubDataPropertyAxiom(
                            getRootObject(),
                            infSup);
                    addRow(new OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                                   this,
                                                                                   null,
                                                                                   getRootObject(),
                                                                                   ax));
                }
            }
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
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
