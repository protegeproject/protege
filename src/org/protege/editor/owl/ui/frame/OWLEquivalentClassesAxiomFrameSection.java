package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.cls.AbstractOWLClassAxiomFrameSection;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.UnsupportedReasonerOperationException;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.CollectionFactory;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLEquivalentClassesAxiomFrameSection extends AbstractOWLClassAxiomFrameSection<OWLEquivalentClassesAxiom, OWLDescription> {

    private static final String LABEL = "Equivalent classes";

    private Set<OWLDescription> added = new HashSet<OWLDescription>();

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
        for (OWLDescription desc : ax.getDescriptions()) {
            added.add(desc);
        }
    }


    protected Set<OWLEquivalentClassesAxiom> getClassAxioms(OWLDescription descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getEquivalentClassesAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLEquivalentClassesAxiom> axioms = new HashSet<OWLEquivalentClassesAxiom>();
            for (OWLAxiom ax : ont.getGeneralClassAxioms()){
                if (ax instanceof OWLEquivalentClassesAxiom &&
                    ((OWLEquivalentClassesAxiom)ax).getDescriptions().contains(descr)){
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
        try {
            if (!getOWLModelManager().getReasoner().isSatisfiable(getRootObject())) {
                addRow(new OWLEquivalentClassesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                    this,
                                                                    null,
                                                                    getRootObject(),
                                                                    getOWLDataFactory().getOWLEquivalentClassesAxiom(
                                                                            CollectionFactory.createSet(getRootObject(),
                                                                                                        getOWLModelManager().getOWLDataFactory().getOWLNothing()))));
            }
            else{
                for (OWLDescription cls : getOWLModelManager().getReasoner().getEquivalentClasses(getRootObject())) {
                    if (!added.contains(cls) && !cls.equals(getRootObject())) {
                        addRow(new OWLEquivalentClassesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                            this,
                                                                            null,
                                                                            getRootObject(),
                                                                            getOWLDataFactory().getOWLEquivalentClassesAxiom(
                                                                                    CollectionFactory.createSet(
                                                                                            getRootObject(),
                                                                                            cls))));
                    }
                }
            }
        }
        catch (UnsupportedReasonerOperationException e) {
            inferredEquivalentClasses = false;
        }
        catch (OWLReasonerException e) {
            e.printStackTrace();
        }
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        if (axiom.getDescriptions().contains(getRootObject())) {
            reset();
        }
    }


    protected OWLEquivalentClassesAxiom createAxiom(OWLDescription object) {
        return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRootObject(), object));
    }


    public OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null);
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLDescription)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLDescription) {
                OWLDescription desc = (OWLDescription) obj;
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


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLDescription, OWLEquivalentClassesAxiom, OWLDescription>> getRowComparator() {
        return null;
    }
}
