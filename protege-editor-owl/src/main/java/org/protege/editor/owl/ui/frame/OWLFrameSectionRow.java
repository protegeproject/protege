package org.protege.editor.owl.ui.frame;

import java.util.List;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>

 * An <code>OWLFrameSectionRow</code> constitues a row in a frame section, which represents
 * an axiom.
 */
public interface OWLFrameSectionRow<R, A extends OWLAxiom, E> extends OWLFrameObject<R, A, E>, MListItem {

    /**
     * Gets the frame section which this row belongs to.
     */
    OWLFrameSection<R,A,E> getFrameSection();


    /**
     * Gets the root object of the frame that this row belongs to.
     */
    R getRoot();


    /**
     * Gets the axiom that the row holds.
     */
    A getAxiom();


    /**
     * Gets a user object. The user object is an arbitrary object which may be used for any purposes.
     * It may be <code>null</code>
     */
    Object getUserObject();


    void setUserObject(Object object);


    /**
     * This row represents an assertion in a particular ontology.
     * This gets the ontology that the assertion belongs to.
     */
    OWLOntology getOntology();


    OWLOntologyManager getOWLOntologyManager();


    /**
     * Determines if this row is editable.  If a row is editable then
     * it may be deleted/removed or edited.  A delete corresponds to
     * the axiom that the row contains being removed from an ontology
     * that contains it.
     * @return <code>true</code> if the row is editable, <code>false</code>
     *         if the row is not editable.
     */
    boolean isEditable();
    
    boolean isDeleteable();


    public boolean isInferred();


    /**
     * Gets the changes which are required to delete this row.  If the
     * row cannot be deleted this list will be empty.
     */
    List<? extends OWLOntologyChange> getDeletionChanges();


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    List<? extends OWLObject> getManipulatableObjects();
}
