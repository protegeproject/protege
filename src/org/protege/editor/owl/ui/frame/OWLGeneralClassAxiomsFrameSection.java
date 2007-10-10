package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralClassAxiomsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLClassAxiom, OWLClassAxiom> {

    public OWLGeneralClassAxiomsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, "General class axioms", frame);
    }


    protected OWLClassAxiom createAxiom(OWLClassAxiom object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLClassAxiom> getObjectEditor() {
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


    public void visit(OWLSubClassAxiom axiom) {
        if (axiom.getSubClass().isAnonymous()) {
            reset();
        }
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
        for (OWLDescription desc : axiom.getDescriptions()) {
            if (!desc.isAnonymous()) {
                return;
            }
        }
        reset();
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        for (OWLDescription desc : axiom.getDescriptions()) {
            if (!desc.isAnonymous()) {
                return;
            }
        }
        reset();
    }
}
