package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.inference.OWLReasonerAdapter;
import org.semanticweb.owlapi.inference.OWLReasonerException;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Feb-2007<br><br>
 */
public class InheritedAnonymousClassesFrameSection extends AbstractOWLFrameSection<OWLClass, OWLClassAxiom, OWLClassExpression> {

    private static final String LABEL = "Inherited anonymous classes";

    private Set<OWLClass> processedClasses = new HashSet<OWLClass>();


    public InheritedAnonymousClassesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
        super(editorKit, LABEL, "Inherited anonymous class", frame);
    }


    protected OWLSubClassOfAxiom createAxiom(OWLClassExpression object) {
        return null; // canAdd() = false
    }


    public OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return null; // canAdd() = false
    }


    protected void refill(OWLOntology ontology) {
        Set<OWLClass> clses = getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getAncestors(getRootObject());
        clses.remove(getRootObject());
        for (OWLClass cls : clses) {
            for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(cls)) {
                if (ax.getSuperClass().isAnonymous()) {
                    addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(), this, ontology, cls, ax));
                }
            }
            for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)) {
                    addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(), this, ontology, cls, ax));
            }
            processedClasses.add(cls);
        }
    }


    protected void refillInferred() {
        try {
            if (!getOWLModelManager().getReasoner().isSatisfiable(getRootObject())) {
                return;
            }
            Set<OWLClass> clses = OWLReasonerAdapter.flattenSetOfSets(getOWLModelManager().getReasoner().getAncestorClasses(
                    getRootObject()));
            clses.remove(getRootObject());
            for (OWLClass cls : clses) {
                if (!processedClasses.contains(cls)) {
                    for (OWLOntology ontology : getOWLModelManager().getActiveOntologies()) {
                        for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(cls)) {
                            if (ax.getSuperClass().isAnonymous()) {
                                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                                    this,
                                                                                    null,
                                                                                    cls,
                                                                                    ax));
                            }
                        }
                        for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)) {
                            Set<OWLClassExpression> descs = new HashSet<OWLClassExpression>(ax.getClassExpressions());
                            descs.remove(getRootObject());
                            OWLClassExpression superCls = descs.iterator().next();
                            if (superCls.isAnonymous()) {
                                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                                    this,
                                                                                    null,
                                                                                    cls,
                                                                                    getOWLDataFactory().getOWLSubClassOfAxiom(
                                                                                            getRootObject(),
                                                                                            superCls)));
                            }
                        }
                    }
                }
            }
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public boolean canAdd() {
        return false;
    }


    public void visit(OWLSubClassOfAxiom axiom) {
        reset();
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        reset();
    }


    protected void clear() {
        processedClasses.clear();
    }


    public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLClassExpression>> getRowComparator() {
        return null;
    }
}
