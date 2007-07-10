package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
