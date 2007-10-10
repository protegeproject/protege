package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.UnsupportedReasonerOperationException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLEquivalentClassesAxiomFrameSection extends AbstractOWLFrameSection<OWLClass, OWLEquivalentClassesAxiom, OWLDescription> {

    private static final String LABEL = "Equivalent classes";

    private Set<OWLClass> added;

    private boolean inferredEquivalentClasses = true;


    public OWLEquivalentClassesAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, frame);
        added = new HashSet<OWLClass>();
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(getRootObject())) {
            addRow(new OWLEquivalentClassesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                this,
                                                                ontology,
                                                                getRootObject(),
                                                                ax));
            for (OWLDescription desc : ax.getDescriptions()) {
                if (!desc.isAnonymous()) {
                    added.add(desc.asOWLClass());
                }
            }
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
                return;
            }
            for (OWLClass cls : getOWLModelManager().getReasoner().getEquivalentClasses(getRootObject())) {
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
        return new OWLClassDescriptionEditor(getOWLEditorKit(), null);
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
    public Comparator<OWLFrameSectionRow<OWLClass, OWLEquivalentClassesAxiom, OWLDescription>> getRowComparator() {
        return null;
    }
}
