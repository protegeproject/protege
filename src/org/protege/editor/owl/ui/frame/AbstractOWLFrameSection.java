package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitor;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public abstract class AbstractOWLFrameSection<R extends OWLObject, A extends OWLAxiom, E> implements OWLFrameSection<R, A, E>, OWLFrameSectionRowObjectEditorHandler<E>, OWLAxiomVisitor {

    private OWLEditorKit owlEditorKit;

    private OWLOntologyChangeListener listener;

    private OWLFrame<? extends R> frame;

    private List<OWLFrameSectionRow<R, A, E>> rows;

    private String label;

    private OWLFrameSectionRowObjectEditor<E> editor;


    protected AbstractOWLFrameSection(OWLEditorKit editorKit, String label, OWLFrame<? extends R> frame) {
        this.owlEditorKit = editorKit;
        this.label = label;
        this.frame = frame;
        this.rows = new ArrayList<OWLFrameSectionRow<R, A, E>>();
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                if (getRootObject() == null) {
                    return;
                }
                for (OWLOntologyChange change : changes) {
                    if (change.isAxiomChange()) {
                        change.getAxiom().accept(AbstractOWLFrameSection.this);
                    }
                }
            }
        };
        editorKit.getOWLModelManager().addOntologyChangeListener(listener);
        Comparator<OWLFrameSectionRow<R, A, E>> comparator = null;
        comparator = getRowComparator();
    }


    final public void dispose() {
        getOWLModelManager().removeOntologyChangeListener(listener);
        disposeOfSection();
        if (editor != null) {
            editor.dispose();
        }
    }


    protected void setLabel(String label) {
        this.label = label;
    }


    protected OWLReasoner getReasoner() {
        return getOWLModelManager().getReasoner();
    }


    final protected void disposeOfSection() {

    }


    public String getLabel() {
        return label;
    }


    final public OWLFrameSectionRowObjectEditor<E> getEditor() {
        if (editor == null) {
            OWLFrameSectionRowObjectEditor<E> ed = getObjectEditor();
            if (ed != null) {
                ed.setHandler(this);
            }
            if (ed instanceof Wizard) {
                // Do not cache wizards
                return ed;
            }
            editor = ed;
        }
        if (editor != null) {
            editor.clear();
        }
        return editor;
    }


    public void handleEditingFinished(Set<E> editedObjects) {
        if (editedObjects == null) {
            return;
        }
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (E editedObject : editedObjects) {
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), createAxiom(editedObject)));
        }
        getOWLModelManager().applyChanges(changes);
    }


    protected abstract A createAxiom(E object);


    public abstract OWLFrameSectionRowObjectEditor<E> getObjectEditor();


    /**
     * Gets the index of the specified section row.
     * @param row The row whose index is to be obtained.
     * @return The index of the row, or -1 if the row is
     *         not contained within this section.
     */
    public int getRowIndex(OWLFrameSectionRow row) {
        return rows.indexOf(row);
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public OWLModelManager getOWLModelManager() {
        return owlEditorKit.getOWLModelManager();
    }


    public OWLOntologyManager getOWLOntologyManager() {
        return getOWLModelManager().getOWLOntologyManager();
    }


    public OWLDataFactory getOWLDataFactory() {
        return getOWLModelManager().getOWLDataFactory();
    }


    public OWLFrame<? extends R> getFrame() {
        return frame;
    }


    public R getRootObject() {
        return frame.getRootObject();
    }


    public void setRootObject(R rootObject) {
        rows.clear();
        clear();
        if (rootObject != null) {
            for (OWLOntology ontology : getOWLModelManager().getActiveOntologies()) {
                refill(ontology);
            }
            for (OWLOntology commentOntology : getOWLModelManager().getCommentOntologies()) {
                refill(commentOntology);
            }
            try {
                refillInferred();
            }
            catch (OWLReasonerException e) {
                throw new OWLRuntimeException(e);
            }
        }

        Comparator<OWLFrameSectionRow<R, A, E>> comparator = getRowComparator();
        if (comparator != null) {
//            Collections.sort(rows, comparator);
            TreeSet ts = new TreeSet(comparator);
            ts.addAll(rows);
            rows.clear();
            rows.addAll(ts);
        }
        fireContentChanged();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.  The method will
     * never be called if the root object is <code>null</code>
     */
    protected abstract void refill(OWLOntology ontology);


    protected abstract void clear();


    protected void refillInferred() throws OWLReasonerException {
        // Do nothing by default
    }


    protected void addRow(OWLFrameSectionRow<R, A, E> row) {
        rows.add(row);
    }


    protected void reset() {
        setRootObject(getRootObject());
        fireContentChanged();
    }


    private void fireContentChanged() {
        getFrame().fireContentChanged();
    }


    /**
     * Gets the rows that this section contains.
     */
    public List<OWLFrameSectionRow<R, A, E>> getRows() {
        return Collections.unmodifiableList(rows);
    }


    /**
     * Gets the rendering for this section, which will be used
     * to visually indicate the section.
     * @return A <code>String</code> representation of the section.  This
     *         is typically the section label.
     */
    public String getRendering() {
        return label;
    }


    /**
     * Determines if rows can be added to this section.
     * @return <code>true</code> if rows can be added to this section,
     *         or <code>false</code> if rows cannot be added to this section.
     */
    public boolean canAddRows() {
        return getOWLModelManager().isActiveOntologyMutable();
    }


    public boolean canAdd() {
        return canAddRows();
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        return false;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        return false;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRendering());
        sb.append(":\n");
        for (OWLFrameSectionRow row : getRows()) {
            sb.append("\t");
            sb.append(row);
            sb.append("\n");
        }
        return sb.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of <code>OWLAxiomVisitor</code>.  These methods can be overriden to be notified when
    // an axiom is added or removed


    public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
    }


    public void visit(OWLAxiomAnnotationAxiom axiom) {
    }


    public void visit(OWLClassAssertionAxiom axiom) {
    }


    public void visit(OWLDataPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
    }


    public void visit(OWLDataPropertyRangeAxiom axiom) {
    }


    public void visit(OWLDataSubPropertyAxiom axiom) {
    }


    public void visit(OWLDeclarationAxiom axiom) {
    }


    public void visit(OWLDifferentIndividualsAxiom axiom) {
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
    }


    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
    }


    public void visit(OWLDisjointUnionAxiom axiom) {
    }


    public void visit(OWLEntityAnnotationAxiom axiom) {
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
    }


    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
    }


    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
    }


    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
    }


    public void visit(OWLImportsDeclaration axiom) {
    }


    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
    }


    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
    }


    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
    }


    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
    }


    public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
    }


    public void visit(OWLObjectPropertyRangeAxiom axiom) {
    }


    public void visit(OWLObjectSubPropertyAxiom axiom) {
    }


    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
    }


    public void visit(OWLSameIndividualsAxiom axiom) {
    }


    public void visit(OWLSubClassAxiom axiom) {
    }


    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
    }


    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
    }


    public void visit(SWRLRule rule) {
    }


    public void visit(OWLOntologyAnnotationAxiom axiom) {
    }


    public String getName() {
        return getLabel();
    }
}
