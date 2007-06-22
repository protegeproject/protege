package org.protege.editor.owl.ui.view;

import org.semanticweb.owl.model.OWLObject;

import javax.swing.event.ChangeListener;
import java.util.List;
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
public interface Copyable {

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
