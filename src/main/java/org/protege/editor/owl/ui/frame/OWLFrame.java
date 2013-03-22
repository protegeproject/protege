package org.protege.editor.owl.ui.frame;

import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public interface OWLFrame<R extends Object> {

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
