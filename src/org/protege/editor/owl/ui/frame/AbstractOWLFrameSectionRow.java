package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public abstract class AbstractOWLFrameSectionRow<R extends Object, A extends OWLAxiom, E> implements OWLFrameSectionRow<R, A, E>, OWLObjectEditorHandler<E> {

    public static final String DEFAULT_DELIMETER = ", ";

    public static final String DEFAULT_PREFIX = "";

    public static final String DEFAULT_SUFFIX = "";

    private OWLEditorKit owlEditorKit;

    private OWLOntology ontology;

    private R rootObject;

    protected A axiom;

    private Object userObject;

    private OWLFrameSection section;

    protected AbstractOWLFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                         R rootObject, A axiom) {
        this.owlEditorKit = owlEditorKit;
        this.section = section;
        this.ontology = ontology;
        this.rootObject = rootObject;
        this.axiom = axiom;
    }


    public OWLFrameSection getFrameSection() {
        return section;
    }


    public R getRootObject() {
        return rootObject;
    }


    /**
     * Default implementation which returns <code>null</code>
     */
    public Object getUserObject() {
        return userObject;
    }


    public void setUserObject(Object object) {
        this.userObject = object;
    }


    public boolean isFixedHeight() {
        return false;
    }

    final public OWLObjectEditor<E> getEditor() {
        OWLObjectEditor<E> editor = getObjectEditor();
        if (editor != null) {
            editor.setHandler(this);
        }
        return editor;
    }


    protected abstract OWLObjectEditor<E> getObjectEditor();

    public boolean checkEditorResults(OWLObjectEditor<E> editor) {
    	return true;
    }

    public void handleEditingFinished(Set<E> editedObjects) {
        if (editedObjects.isEmpty()) {
            return;
        }
        OWLAxiom newAxiom = createAxiom(editedObjects.iterator().next());
        if (newAxiom != null){ // the editor should protect from this, but just in case
        	A oldAxiom = getAxiom();
        	Set<OWLAnnotation> axiomAnnotations = oldAxiom.getAnnotations();
        	if (axiomAnnotations != null && !axiomAnnotations.isEmpty()) {
        		newAxiom = newAxiom.getAnnotatedAxiom(axiomAnnotations);
        	}
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.add(new RemoveAxiom(getOntology(), oldAxiom));
            changes.add(new AddAxiom(getOntology(), newAxiom));
            getOWLModelManager().applyChanges(changes);
        }
    }


    protected abstract A createAxiom(E editedObject);


    /**
     * This row represents an assertion in a particular ontology.
     * This gets the ontology that the assertion belongs to.
     */
    public OWLOntology getOntology() {
        return ontology;
    }


    public OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
    }


    public OWLDataFactory getOWLDataFactory() {
        return getOWLModelManager().getOWLDataFactory();
    }


    public OWLOntologyManager getOWLOntologyManager() {
        return getOWLModelManager().getOWLOntologyManager();
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    /**
     * Gets the root object of the frame that this row belongs to.
     */
    public R getRoot() {
        return rootObject;
    }


    /**
     * Gets the object that the row holds.
     */
    public A getAxiom() {
        return axiom;
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        return false;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        return false;
    }


    public String getTooltip() {
        if (ontology != null) {
            UIHelper helper = new UIHelper(owlEditorKit);
            String content = helper.getHTMLOntologyList(Collections.singleton(ontology));
            return "<html><body>Asserted in: " + content + "</body></html>";
        }
        else {
            return "Inferred";
        }
    }


    public String toString() {
        return getRendering();
    }


    /**
     * Deletes this row.  This will alter the underlying model of which
     * this row is a representation.  This method should not be called
     * if the <code>isEditable</code> method returns <code>false</code>.
     */
    public List<? extends OWLOntologyChange> getDeletionChanges() {
        if (isDeleteable()) {
            return Arrays.asList(new RemoveAxiom(getOntology(), getAxiom()));
        }
        else {
            return Collections.emptyList();
        }
    }


    /**
     * Determines if this row is editable.  If a row is editable then
     * it may be deleted/removed and column values may be edited.
     * @return <code>true</code> if the row is editable, <code>false</code>
     *         if the row is not editable.
     */
    public boolean isEditable() {
        return getOntology() != null;
    }


    public String getPrefix() {
        return DEFAULT_PREFIX;
    }


    public String getDelimeter() {
        return DEFAULT_DELIMETER;
    }


    public String getSuffix() {
        return DEFAULT_SUFFIX;
    }


    protected Object getObjectRendering(OWLObject ob) {
        return getOWLModelManager().getRendering(ob);
    }


    public boolean isInferred() {
        return ontology == null;
    }


    /**
     * Gets the rendering of the value of a particular column.
     * @return The <code>String</code> representation of the column
     *         value.
     */
    public String getRendering() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPrefix());
        for (Iterator<? extends OWLObject> it = getManipulatableObjects().iterator(); it.hasNext();) {
            OWLObject obj = it.next();
            sb.append(getObjectRendering(obj));
            if (it.hasNext()) {
                sb.append(getDelimeter());
            }
        }
        sb.append(getSuffix());
        return sb.toString();
    }


    public boolean isDeleteable() {
        return isEditable();
    }


    public void handleEdit() {

    }


    public boolean handleDelete() {
        return false;
    }

    public List<MListButton> getAdditionalButtons() {
        return Collections.emptyList();
    }
}
