package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralClassAxiomsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLClassAxiom, OWLClassAxiom> {

    public OWLGeneralClassAxiomsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, "General class axioms", "General class axiom", frame);
    }


    protected OWLClassAxiom createAxiom(OWLClassAxiom object) {
        return object;
    }


    public OWLObjectEditor<OWLClassAxiom> getObjectEditor() {
        return new OWLGeneralAxiomEditor(getOWLEditorKit());
    }


    protected void refill(OWLOntology ontology) {
        for (OWLClassAxiom ax : ontology.getGeneralClassAxioms()) {
            addRow(new OWLGeneralClassAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }


    protected void clear() {
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLClassAxiom, OWLClassAxiom>> getRowComparator() {
        return null;
    }


    public void visit(OWLSubClassOfAxiom axiom) {
        if (axiom.getSubClass().isAnonymous()) {
            reset();
        }
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            if (!desc.isAnonymous()) {
                return;
            }
        }
        reset();
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            if (!desc.isAnonymous()) {
                return;
            }
        }
        reset();
    }
}
