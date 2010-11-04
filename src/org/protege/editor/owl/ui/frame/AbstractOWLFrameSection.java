package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 *
 * The visitor methods can be overriden to be notified when an axiom is added or removed
 */
public abstract class AbstractOWLFrameSection<R extends Object, A extends OWLAxiom, E> extends OWLAxiomVisitorAdapter 
        implements OWLFrameSection<R, A, E>, OWLObjectEditorHandler<E> {
	public static final Logger LOGGER = Logger.getLogger(AbstractOWLFrameSection.class);

    private OWLEditorKit owlEditorKit;

    private OWLFrame<? extends R> frame;

    private List<OWLFrameSectionRow<R, A, E>> rows;

    private String label;

    private String rowLabel;

    private OWLObjectEditor<E> editor;

    private boolean cacheEditor = true;

    private OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            handleChanges(changes);
        }
    };


    protected AbstractOWLFrameSection(OWLEditorKit editorKit, String label, String rowLabel, OWLFrame<? extends R> frame) {
        this.owlEditorKit = editorKit;
        this.label = label;
        this.rowLabel = rowLabel;
        this.frame = frame;
        this.rows = new ArrayList<OWLFrameSectionRow<R, A, E>>();

        getOWLModelManager().addOntologyChangeListener(listener);
    }

    
    protected AbstractOWLFrameSection(OWLEditorKit editorKit, String label, OWLFrame<? extends R> frame) {
        this(editorKit, label, null, frame);
    }


    // not the perfect solution, but prevents us from breaking the API
    protected void setCacheEditor(boolean cacheEditor){
        this.cacheEditor = cacheEditor;
    }


    public List<MListButton> getAdditionalButtons() {
        return Collections.emptyList();
    }


    protected void handleChanges(List<? extends OWLOntologyChange> changes) {
        if (getRootObject() == null) {
            return;
        }
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                change.getAxiom().accept(AbstractOWLFrameSection.this);
            }
        }
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


    protected void disposeOfSection() {

    }


    public String getLabel() {
        return label;
    }


    public String getRowLabel(OWLFrameSectionRow row) {
        return rowLabel;
    }


    final public OWLObjectEditor<E> getEditor() {
        if (!cacheEditor && editor != null){
            editor.dispose();
            editor = null;
        }
        if (editor == null) {
            OWLObjectEditor<E> ed = getObjectEditor();
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
        Set<A> axioms = new HashSet<A>();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (E editedObject : editedObjects) {
            final A ax = createAxiom(editedObject);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            axioms.add(ax);
        }
        getOWLModelManager().applyChanges(changes);
        for (A axiom : axioms){
            if (!getOWLModelManager().getActiveOntology().containsAxiom(axiom)){
                LOGGER.warn("axiom not added = " + axiom);
            }
        }
    }


    protected abstract A createAxiom(E object);


    public abstract OWLObjectEditor<E> getObjectEditor();


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
        return owlEditorKit.getModelManager();
    }


    public OWLOntologyManager getOWLOntologyManager() {
        return getOWLModelManager().getOWLOntologyManager();
    }


    public OWLDataFactory getOWLDataFactory() {
        return getOWLModelManager().getOWLDataFactory();
    }
    
    public OWLReasoner getCurrentReasoner() {
    	return getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
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
            for (OWLOntology ontology : getOntologies()) {
                refill(ontology);
            }
            refillInferred();
        }

        Comparator<OWLFrameSectionRow<R, A, E>> comparator = getRowComparator();
        if (comparator != null) {
//            Collections.sort(rows, comparator);
            TreeSet<OWLFrameSectionRow<R,A,E>> ts = new TreeSet<OWLFrameSectionRow<R,A,E>>(comparator);
            ts.addAll(rows);
            rows.clear();
            rows.addAll(ts);
        }
        fireContentChanged();
    }


    protected Set<OWLOntology> getOntologies() {
        return getOWLModelManager().getActiveOntologies();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.  The method will
     * never be called if the root object is <code>null</code>
     */
    protected abstract void refill(OWLOntology ontology);


    protected abstract void clear();


    protected void refillInferred() {
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
    
    public List<A> getAxioms() {
    	List<A> axioms = new ArrayList<A>();
    	for (OWLFrameSectionRow<R,A,E> row : rows) {
    		axioms.add(row.getAxiom());
    	}
    	return axioms;
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


    public boolean canAdd() {
        return getOWLModelManager().isActiveOntologyMutable();
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

    public String getName() {
        return getLabel();
    }
}
