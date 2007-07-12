package org.protege.editor.core;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.*;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * ProtegeManager is a singleton that manages the Protege application.
 * This means that the <code>ProtegeManager</code> manages the creation
 * and deletion of <code>EditorKit</code>s.  A list of the installed
 * <code>EditorKitFactoryPlugin</code>s
 */
public class ProtegeManager {

    public static final Logger logger = Logger.getLogger(ProtegeManager.class);

    private static ProtegeManager instance;

    private EditorKitManager editorKitManager;

    private Map<EditorKitFactoryPlugin, EditorKitFactory> editorKitFactoriesMap;


    private ProtegeManager() {
        editorKitFactoriesMap = new HashMap<EditorKitFactoryPlugin, EditorKitFactory>();
        EditorKitFactoryPluginLoader loader = new EditorKitFactoryPluginLoader();
        for (EditorKitFactoryPlugin plugin : loader.getPlugins()) {
            editorKitFactoriesMap.put(plugin, null);
        }
    }


    /**
     * Gets hold of the one and only <code>ProtegeManager</code>
     */
    public static synchronized ProtegeManager getInstance() {
        if (instance == null) {
            instance = new ProtegeManager();
        }
        return instance;
    }


    /**
     * Gets the <code>EditorKitManager</code>, which controls
     * the selected <code>EditorKit</code> etc. for the
     * application.
     */
    public EditorKitManager getEditorKitManager() {
        if (editorKitManager == null) {
            editorKitManager = new EditorKitManager();
        }
        return editorKitManager;
    }


    /**
     * This is a convenience method that can be used to obtain the
     * top level frame for a <code>Workspace</code>. This method actually
     * delegates to the <code>WorkspaceManager</code>
     * @param workspace The <code>Workspace</code> for which the top level
     *                  frame is to be retrieved.
     * @return The top level frame for a <code>Workspace</code> or
     *         <code>null</code> if there is no such <code>Workspace</code> or
     *         no frame exists.
     */
    public WorkspaceFrame getFrame(Workspace workspace) {
        return getEditorKitManager().getWorkspaceManager().getFrame(workspace);
    }


    /**
     * Obtains a list of the installed <code>EditorKitFactory</code> plugins.
     * These plugins can be passed to the createAndSetupNewEditorKit and
     * openAndSetupNewEditorKit methods.
     */
    public List<EditorKitFactoryPlugin> getEditorKitFactoryPlugins() {
        return new ArrayList<EditorKitFactoryPlugin>(editorKitFactoriesMap.keySet());
    }


    /**
     * Creates and sets up a new (probably empty) .
     * The new <code>EditorKit</code> will be passed to the
     * <code>EditorKitManager</code> for installation.
     * @param plugin The <code>EditorKitFactoryPlugin</code> that describes
     *               the <code>EditorKitFactory</code> which will be used to create the
     *               <code>EditorKit</code>.
     */
    public boolean createAndSetupNewEditorKit(EditorKitFactoryPlugin plugin) throws Exception {
        EditorKitFactory editorKitFactory = getEditorKitFactory(plugin);
        if (editorKitFactory != null) {
            EditorKit editorKit = editorKitFactory.createEditorKit();
            if (editorKit.handleNewRequest()) {
                getEditorKitManager().addEditorKit(editorKit);
                return true;
            }
        }
        return false;
    }


    /**
     * Opens an <code>EditorKit</code> using the <code>EditorKitFactory</code>
     * specified by the given Id.
     * @param plugin The <code>EditorKitFactoryPlugin</code> that describes
     *               the <code>EditorKitFactory</code> which will be used to create the
     *               <code>EditorKit</code>.
     */
    public boolean openAndSetupEditorKit(EditorKitFactoryPlugin plugin) throws Exception {
        EditorKitFactory editorKitFactory = getEditorKitFactory(plugin);
        if (editorKitFactory != null) {
            EditorKit editorKit = editorKitFactory.createEditorKit();
            if (editorKit.handleLoadRequest()) {
                getEditorKitManager().addEditorKit(editorKit);
                return true;
            }
        }
        return false;
    }


    public boolean loadAndSetupEditorKitFromURI(EditorKitFactoryPlugin plugin, URI uri) throws Exception {
        EditorKitFactory editorKitFactory = getEditorKitFactory(plugin);
        if (editorKitFactory != null) {
            EditorKit editorKit = editorKitFactory.createEditorKit();
            if (editorKit.handleLoadFrom(uri)) {
                getEditorKitManager().addEditorKit(editorKit);
                return true;
            }
        }
        return false;
    }


    public boolean openAndSetupRecentEditorKit(EditorKitDescriptor editorKitDescriptor) throws Exception {
        for (EditorKitFactoryPlugin plugin : getEditorKitFactoryPlugins()) {
            if (plugin.getId().equals(editorKitDescriptor.getEditorKitFactoryID())) {
                EditorKitFactory editorKitFactory = getEditorKitFactory(plugin);
                EditorKit editorKit = editorKitFactory.createEditorKit();
                if (editorKit.handleLoadRecentRequest(editorKitDescriptor)) {
                    getEditorKitManager().addEditorKit(editorKit);
                    return true;
                }
            }
        }
        return false;
    }


    public void saveEditorKit(EditorKit editorKit) throws Exception {
        editorKit.handleSave();
    }


    public void saveEditorKitAs(EditorKit editorKit) throws Exception {
        editorKit.handleSaveAs();
        getFrame(editorKit.getWorkspace()).updateTitle();
    }


    /**
     * Closes an <code>EditorKit</code>.  This disposes of
     * the clsdescriptioneditor kit's <code>Workspace</code>, and closes
     * the clsdescriptioneditor kit's model manager.
     */
    public void disposeOfEditorKit(EditorKit editorKit) {
        editorKit.close();
        ProtegeManager.getInstance().getEditorKitManager().removeEditorKit(editorKit);
        // Dispose of the workspace
        editorKit.getWorkspace().dispose();
        // Dispose of the model
        editorKit.getModelManager().dispose();
        System.gc();
        if (editorKitManager.getEditorKitCount() == 0) {
            System.exit(0);
        }
    }


    /**
     * Gets an <code>EditorKitFactory</code> by its corresponding plugin.
     * @param plugin The plugin that describes the clsdescriptioneditor kit that will be
     *               returned.
     * @return An <code>EditorKitFactory</code> or <code>null</code>
     *         if there is no installed <code>EditorKitFactory</code> with the
     *         specified Id.
     */
    private EditorKitFactory getEditorKitFactory(EditorKitFactoryPlugin plugin) {
        EditorKitFactory editorKitFactory = editorKitFactoriesMap.get(plugin);
        if (editorKitFactory != null) {
            return editorKitFactory;
        }
        EditorKitFactory editorKitFactory1 = plugin.newInstance();
        editorKitFactoriesMap.put(plugin, editorKitFactory1);
        return editorKitFactory1;
    }
}
