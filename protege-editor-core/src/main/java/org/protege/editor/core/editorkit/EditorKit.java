package org.protege.editor.core.editorkit;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ModelManager;
import org.protege.editor.core.ui.workspace.Workspace;

import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 15, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>



 * The EditorKit provides a connection between
 * some kind of "ontology model" (or knowledgebase) and
 * a UI for that model/knowledgebase.  The model, whatever
 * it might be is managed by a <code>ModelManager</code>.  The
 * UI comes in the form of a <code>Workspace</code>.

 * Each <code>Workspace</code> has a reference to its
 * <code>EditorKit</code>, and each <code>ModelManager</code>
 * has a reference to its <code>EditorKit</code>.

 * Each <code>EditorKit</code> has an ID, which determines its
 * type.  This is used by views, and other plugins such as menu
 * items that are only applicable to certain types of <code>EditorKit</code>s.
 * For example, an <code>OWLReasoner</code> can only work with an
 * <code>OWLEditorKit</code>.
 */
public interface EditorKit extends Disposable {

    /**
     * Gets the <code>EditorKit</code> Id.  This can be used to identify
     * the type of <code>EditorKit</code>.
     * @return A <code>String</code> that represents the <code>EditorKit</code>
     *         Id.
     */
    String getId();

    void put(Object key, Disposable value);
    
    Disposable get(Object key);
    

    /**
     * Gets the factory that created the editor kit.
     */
    EditorKitFactory getEditorKitFactory();


    /**
     * Gets the <code>Workspace</code> that is used in the UI to
     * display the contents of the editor kit "model".
     */
    Workspace getWorkspace();


    /**
     * Gets the "model" that the editor kit edits.  This will
     * probably contain one or more ontologies.
     */
    ModelManager getModelManager();


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
    boolean handleNewRequest() throws Exception;


    boolean handleLoadRequest() throws Exception;


    boolean handleLoadFrom(URI uri) throws Exception;


    boolean handleLoadRecentRequest(EditorKitDescriptor descriptor) throws Exception;


    void handleSave() throws Exception;


    void handleSaveAs() throws Exception;

    /**
     * Determines if this editor kit has modified the contents if its documents in any way.
     * @return <code>true</code> if this editor kit has modified the contents of its document, otherwise <code>false</code>.
     */
    boolean hasModifiedDocument();
}
