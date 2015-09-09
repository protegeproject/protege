package org.protege.editor.owl.ui.frame;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public interface OWLFrameListener {

    /**
     * Gets called when the frame content has
     * been changed.  Usually because a row has
     * been added or removed.
     * @throws Exception
     */
    void frameContentChanged() throws Exception;
}
