package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.OWLObject;

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
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public interface OWLFrame<R extends OWLObject> {

    /**
     * Disposes of the frame by cleaning up any
     * resource and listeners used by the frame
     */
    void dispose();


    /**
     * Sets the root object.
     * @param rootObject The root object, may be <code>null</code>.
     */
    void setRootObject(R rootObject);


    /**
     * Gets the frame root object.
     * @return The frame root object.  The return value
     *         may be <code>null</code>.
     */
    R getRootObject();


    /**
     * Gets the sections within this frame.
     */
    List<OWLFrameSection> getFrameSections();


    /**
     * Adds a frame listener to this frame.
     * @param listener The listener to be added.
     */
    void addFrameListener(OWLFrameListener listener);


    /**
     * Removes a frame listener from this frame.
     * @param listener The listener to be removed.
     */
    void removeFrameListener(OWLFrameListener listener);


    /**
     * Called when the frame content (either the sections or
     * the rows within sections) have changed.
     */
    void fireContentChanged();
}
