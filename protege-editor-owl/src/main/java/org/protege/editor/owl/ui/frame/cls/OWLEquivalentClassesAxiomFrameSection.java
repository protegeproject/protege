package org.protege.editor.owl.ui.frame.cls;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLEquivalentClassesAxiomFrameSection extends AbstractOWLClassAxiomFrameSection<OWLEquivalentClassesAxiom, OWLClassExpression> {

    private static final String LABEL = "Equivalent To";

    private Set<OWLClassExpression> added = new HashSet<OWLClassExpression>();

    private boolean inferredEquivalentClasses = true;


    public OWLEquivalentClassesAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, "Equivalent class", frame);
    }


    protected void clear() {
        added.clear();
    }


    protected void addAxiom(OWLEquivalentClassesAxiom ax, OWLOntology ontology) {
        addRow(new OWLEquivalentClassesAxiomFrameSectionRow(getOWLEditorKit(),
                                                            this,
                                                            ontology,
                                                            getRootObject(),
                                                            ax));
        for (OWLClassExpression desc : ax.getClassExpressions()) {
            added.add(desc);
        }
    }


    protected Set<OWLEquivalentClassesAxiom> getClassAxioms(OWLClassExpression descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getEquivalentClassesAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLEquivalentClassesAxiom> axioms = new HashSet<OWLEquivalentClassesAxiom>();
            for (OWLAxiom ax : ont.getGeneralClassAxioms()){
                if (ax instanceof OWLEquivalentClassesAxiom &&
                    ((OWLEquivalentClassesAxiom)ax).getClassExpressions().contains(descr)){
                    axioms.add((OWLEquivalentClassesAxiom)ax);
                }
            }
            return axioms;
        }
    }


    protected void refillInferred() {
        if (!inferredEquivalentClasses) {
            return;
        }
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_CLASSES, 
                                                                  new Runnable() {

            public void run() {
                final OWLReasoner reasoner = getOWLModelManager().getReasoner();
                if (!reasoner.isConsistent()) {
            		return;
            	}
                if (!reasoner.isSatisfiable(getRootObject())) {
                    if (!getRootObject().isOWLNothing()) {
                        OWLClass nothing = getOWLModelManager().getOWLDataFactory().getOWLNothing();
                        addRow(new OWLEquivalentClassesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                            OWLEquivalentClassesAxiomFrameSection.this,
                                                                            null,
                                                                            getRootObject(),
                                                                            getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                                                                         nothing))));
                    }
                }
                else{
                    for (OWLClassExpression cls : reasoner.getEquivalentClasses(getRootObject())) {
                        if (!added.contains(cls) && !cls.equals(getRootObject())) {
                            addRow(new OWLEquivalentClassesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                OWLEquivalentClassesAxiomFrameSection.this,
                                                                                null,
                                                                                getRootObject(),
                                                                                getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                                                                             cls))));
                        }
                    }
                }
            }
        });

    }


    protected OWLEquivalentClassesAxiom createAxiom(OWLClassExpression object) {
        return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRootObject(), object));
    }


    public OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, AxiomType.EQUIVALENT_CLASSES);
    }

    public boolean checkEditorResults(OWLObjectEditor<OWLClassExpression> editor) {
    	Set<OWLClassExpression> equivalents = editor.getEditedObjects();
    	return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
    
    @Override
    public void handleEditingFinished(Set<OWLClassExpression> editedObjects) {
    	editedObjects = new HashSet<OWLClassExpression>(editedObjects);
    	editedObjects.remove(getRootObject());
    	super.handleEditingFinished(editedObjects);
    }

    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLClassExpression)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLClassExpression) {
                OWLClassExpression desc = (OWLClassExpression) obj;
                OWLAxiom ax = getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                           desc));
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
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
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLEquivalentClassesAxiom &&
    			((OWLEquivalentClassesAxiom) change.getAxiom()).getClassExpressions().contains(getRootObject());
    }



	/**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLClassExpression, OWLEquivalentClassesAxiom, OWLClassExpression>> getRowComparator() {
        return null;
    }
}
