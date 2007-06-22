package org.protege.editor.core.editorkit;

import org.protege.editor.core.ModelManager;
import org.protege.editor.core.ui.workspace.Workspace;

import java.net.URI;
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
 * Date: Mar 15, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * <p/>
 * <p/>
 * The EditorKit provides a connection between
 * some kind of "ontology model" (or knowledgebase) and
 * a UI for that model/knowledgebase.  The model, whatever
 * it might be is managed by a <code>ModelManager</code>.  The
 * UI comes in the form of a <code>Workspace</code>.
 * <p/>
 * Each <code>Workspace</code> has a reference to its
 * <code>EditorKit</code>, and each <code>ModelManager</code>
 * has a reference to its <code>EditorKit</code>.
 * <p/>
 * Each <code>EditorKit</code> has an ID, which determines its
 * type.  This is used by views, and other plugins such as menu
 * items that are only applicable to certain types of <code>EditorKit</code>s.
 * For example, an <code>OWLReasoner</code> can only work with an
 * <code>OWLEditorKit</code>.
 */
public interface EditorKit {

    /**
     * Gets the <code>EditorKit</code> Id.  This can be used to identify
     * the type of <code>EditorKit</code>.
     * @return A <code>String</code> that represents the <code>EditorKit</code>
     *         Id.
     */
    public String getId();


    /**
     * Gets the factory that created the clsdescriptioneditor kit.
     */
    public EditorKitFactory getEditorKitFactory();


    /**
     * Gets the <code>Workspace</code> that is used in the UI to
     * display the contents of the clsdescriptioneditor kit "model".
     */
    public Workspace getWorkspace();


    /**
     * Gets the "model" that the clsdescriptioneditor kit edits.  This will
     * probably contain one or more ontologies.
     */
    public ModelManager getModelManager();


    /**
     * Called by the system to initialise the editorkit to an
     * "new" or "empty" state.  This is typically called in response
     * to a File->New request for example.
     * @return <code>true</code> in an new/empty editorkit and
     *         model was successfully created. <code>false</code> if the
     *         intialisation sequence was cancelled or didn't complete.
     * @throws Exception This exception is thrown if there were
     *                   errors.
     */
    public boolean handleNewRequest() throws Exception;


    public boolean handleLoadRequest() throws Exception;


    public boolean handleLoadFrom(URI uri) throws Exception;


    public boolean handleLoadRecentRequest(EditorKitDescriptor descriptor) throws Exception;


    public void handleSave() throws Exception;


    public void handleSaveAs() throws Exception;


    public void close();
}
