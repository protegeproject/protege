package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
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
 * Date: 23-Feb-2007<br><br>
 */
public class InheritedAnonymousClassesFrameSection extends AbstractOWLFrameSection<OWLClass, OWLClassAxiom, OWLDescription> {

    private static final String LABEL = "Inherited anonymous classes";

    private Set<OWLClass> processedClasses = new HashSet<OWLClass>();


    public InheritedAnonymousClassesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
        super(editorKit, LABEL, "Inherited anonymous class", frame);
    }


    protected OWLSubClassAxiom createAxiom(OWLDescription object) {
        return getOWLDataFactory().getOWLSubClassAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return new OWLClassDescriptionEditor(getOWLEditorKit(), null);
    }


    protected void refill(OWLOntology ontology) {
        Set<OWLClass> clses = getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getAncestors(getRootObject());
        clses.remove(getRootObject());
        for (OWLClass cls : clses) {
            for (OWLSubClassAxiom ax : ontology.getSubClassAxiomsForLHS(cls)) {
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
                        for (OWLSubClassAxiom ax : ontology.getSubClassAxiomsForLHS(cls)) {
                            if (ax.getSuperClass().isAnonymous()) {
                                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                                    this,
                                                                                    null,
                                                                                    cls,
                                                                                    ax));
                            }
                        }
                        for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)) {
                            Set<OWLDescription> descs = new HashSet<OWLDescription>(ax.getDescriptions());
                            descs.remove(getRootObject());
                            OWLDescription superCls = descs.iterator().next();
                            if (superCls.isAnonymous()) {
                                addRow(new InheritedAnonymousClassesFrameSectionRow(getOWLEditorKit(),
                                                                                    this,
                                                                                    null,
                                                                                    cls,
                                                                                    getOWLDataFactory().getOWLSubClassAxiom(
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


    public void visit(OWLSubClassAxiom axiom) {
        reset();
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        reset();
    }


    protected void clear() {
        processedClasses.clear();
    }


    public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLDescription>> getRowComparator() {
        return null;
    }
}
