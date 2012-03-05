package org.protege.editor.owl.ui.frame.cls;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Feb-2007<br><br>
 */
public class InheritedAnonymousClassesFrameSection extends AbstractOWLFrameSection<OWLClass, OWLClassAxiom, OWLClassExpression> {

    private static final String LABEL = "SubClass Of (Anonymous Ancestor)";

    private Set<OWLClass> processedClasses = new HashSet<OWLClass>();


    public InheritedAnonymousClassesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
        super(editorKit, LABEL, "Anonymous Ancestor Class", frame);
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
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES,
                                                                  new Runnable() {
            public void run() {
                refillInferredDoIt();
            }
        });
    }
    
    private void refillInferredDoIt() {
        if (!getOWLModelManager().getReasoner().isConsistent()) {
            return;
        }
        Set<OWLClass> clses = getReasoner().getSuperClasses(getRootObject(), true).getFlattened();
        clses.remove(getRootObject());
        for (OWLClass cls : clses) {
            if (!processedClasses.contains(cls)) {
                for (OWLOntology ontology : getOWLModelManager().getActiveOntology().getImportsClosure()) {
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
                        for (OWLClassExpression superCls : descs) {
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
    }


    public boolean canAdd() {
        return false;
    }
    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() && (change.getAxiom() instanceof OWLSubClassOfAxiom || change.getAxiom() instanceof OWLEquivalentClassesAxiom);
    }


    protected void clear() {
        processedClasses.clear();
    }


    public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAxiom, OWLClassExpression>> getRowComparator() {
        return null;
    }
}
