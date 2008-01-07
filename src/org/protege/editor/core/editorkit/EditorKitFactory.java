package org.protege.editor.core.editorkit;

import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 16, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * <p/>
 * <code>EditorKit</code> factories are responsible
 * for creating clsdescriptioneditor kits.  They are a type of plugin
 * to the application.
 */
public interface EditorKitFactory {

    /**
     * The <code>EditorKitFactory</code> is a type of plugin,
     * and therefore type of extension point to the protege
     * application.  This member contains the ID of the extension point.
     */
    public static final String EXTENSION_POINT_ID = "EditorKitFactory";


    /**
     * Gets the identifier for this <code>EditorKitFactory</code>.
     * @return A <code>String</code> representation of the
     *         clsdescriptioneditor kit factory.
     */
    public String getId();


    public boolean canLoad(URI uri);


    public EditorKit createEditorKit() throws Exception;


    /**
     * Determines if the editor kit descriptor is valid.  A descriptor
     * might not be valid because the original set of files or file that
     * the descriptor describes may have been moved or deleted for example.
     * @param descriptor The descriptor, which will have the same id as this
     * editor kit factory
     * @return <code>true</code> if the descriptor is valid, otherwise false.
     */
    public boolean isValidDescriptor(EditorKitDescriptor descriptor);
}
