package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.action.ActionTarget;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.event.ChangeListener;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * Components that support the copying of <code>OWLObjects</code> should
 * implement this interface, so that they will work with the copy action
 * on the edit menu.
 */
public interface Copyable extends ActionTarget {

    /**
     * Determines whether or not at least one <code>OWLObject</code>
     * can be copied.
     * @return <code>true</code> if at least one object can be copied, or
     *         <code>false</code> if no objects can't be copied.
     */
    public boolean canCopy();


    /**
     * Gets the objects that will be copied.
     * @return A <code>List</code> of <code>OWLObjects</code> that will be
     *         copied to the clip board
     */
    public List<OWLObject> getObjectsToCopy();


    /**
     * Adds a change listener.  If the ability to copy OWL objects changes, the
     * copyable component should notify listeners through a change of state event.
     * @param changeListener The listener to be added.
     * @see ChangeListenerMediator Components may want to use the <code>ChangeListenerMediator</code>
     *      class to manage event notifcation and addition/removal of listeners
     */
    void addChangeListener(ChangeListener changeListener);


    /**
     * Removes a previously added change listener.
     * @param changeListener The ChangeListener to be removed.
     */
    void removeChangeListener(ChangeListener changeListener);
}
