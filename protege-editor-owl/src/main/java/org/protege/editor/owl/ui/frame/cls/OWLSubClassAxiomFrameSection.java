package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.*;

import javax.swing.JOptionPane;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLSubClassAxiomFrameSection extends AbstractOWLClassAxiomFrameSection<OWLSubClassOfAxiom, OWLClassExpression> {

    private static final String LABEL = "SubClass Of";

    private Set<OWLClassExpression> added = new HashSet<>();


    public OWLSubClassAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, "Superclass", frame);
    }


    protected void clear() {
        added.clear();
    }


    protected void addAxiom(OWLSubClassOfAxiom ax, OWLOntology ont) {
        addRow(new OWLSubClassAxiomFrameSectionRow(getOWLEditorKit(), this, ont, getRootObject(), ax));
        added.add(ax.getSuperClass());
    }


    protected Set<OWLSubClassOfAxiom> getClassAxioms(OWLClassExpression descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getSubClassAxiomsForSubClass(descr.asOWLClass());
        }
        else{
            Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
            for (OWLAxiom ax : ont.getGeneralClassAxioms()){
                if (ax instanceof OWLSubClassOfAxiom && ((OWLSubClassOfAxiom)ax).getSubClass().equals(descr)){
                    axioms.add((OWLSubClassOfAxiom)ax);
                }
            }
            return axioms;
        }
    }


    protected void refillInferred() {
        final OWLReasoner reasoner = getOWLModelManager().getReasoner();
        if(!reasoner.isConsistent()) {
            return;
        }
        if(!reasoner.isSatisfiable(getRootObject())) {
            return;
        }
            getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES, () -> {
                    for (Node<OWLClass> inferredSuperClasses : reasoner.getSuperClasses(getRootObject(), true)) {
                        for (OWLClassExpression inferredSuperClass : inferredSuperClasses) {
                            if (!added.contains(inferredSuperClass)) {
                                addInferredRowIfNontrivial(new OWLSubClassAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                               OWLSubClassAxiomFrameSection.this,
                                                                                               null,
                                                                                               getRootObject(),
                                                                                               getOWLModelManager().getOWLDataFactory().getOWLSubClassOfAxiom(getRootObject(),
                                                                                                                                                              inferredSuperClass)));
                                added.add(inferredSuperClass);
                            }
                        }
                    }
            });

    }


    protected OWLSubClassOfAxiom createAxiom(OWLClassExpression object) {
    	if (getRootObject().equals(object)) {    		
    		return null;
    	} else {
            return getOWLDataFactory().getOWLSubClassOfAxiom(getRootObject(), object);
    	}
    }


    public OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, AxiomType.SUBCLASS_OF);
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLClassExpression)) {
                return false;
            }
        }
        return true;
    }


    private OWLObjectProperty prop;


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLClassExpression) {
                OWLClassExpression desc;
                if (prop != null) {
                    desc = getOWLDataFactory().getOWLObjectSomeValuesFrom(prop, (OWLClassExpression) obj);
                }
                else {
                    desc = (OWLClassExpression) obj;
                }
                OWLAxiom ax = getOWLDataFactory().getOWLSubClassOfAxiom(getRootObject(), desc);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else if (obj instanceof OWLObjectProperty) {
                // Prime
                prop = (OWLObjectProperty) obj;
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLSubClassOfAxiom) {
    		return ((OWLSubClassOfAxiom) axiom).getSubClass().equals(getRootObject());
    	}
    	return false;
    }



    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClassExpression>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClassExpression>>() {


            public int compare(OWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClassExpression> o1,
                               OWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClassExpression> o2) {
                if (o1.isInferred()) {
                    if (!o2.isInferred()) {
                        return 1;
                    }
                }
                else {
                    if (o2.isInferred()) {
                        return -1;
                    }
                }
                int val = getOWLModelManager().getOWLObjectComparator().compare(o1.getAxiom(), o2.getAxiom());

                if(val == 0) {
                    return o1.getOntology().getOntologyID().compareTo(o2.getOntology().getOntologyID());
                }
                else {
                    return val;
                }

            }
        };
    }
}
