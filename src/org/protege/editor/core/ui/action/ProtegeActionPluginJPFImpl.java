package org.protege.editor.core.ui.action;


import javax.swing.Icon;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.PluginUtilities;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 29, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class ProtegeActionPluginJPFImpl implements ProtegeActionPlugin {

    private EditorKit editorKit;

    private IExtension extension;

    private static final String NAME_PARAM = "name";

    private static final String TOOL_TIP_PARAM = "toolTip";


    protected ProtegeActionPluginJPFImpl(EditorKit editorKit, IExtension extension) {
        this.editorKit = editorKit;
        this.extension = extension;
    }


    protected IExtension getExtension() {
        return extension;
    }


    /**
     * Gets the plugin id.
     */
    public String getId() {
        return extension.getUniqueIdentifier();
    }


    /**
     * Gets the name of the action.  This is the name
     * that will be used for the menu item text etc.
     */
    public String getName() {
        return PluginUtilities.getAttribute(extension, NAME_PARAM);
    }


    /**
     * Gets the tooltip text for the action.
     * @return A <code>String</code> representing the tooltip
     *         text, or <code>null</code> if the action created by this
     *         plugin shouldn't have any tooltip text.
     */
    public String getToolTipText() {
        return PluginUtilities.getAttribute(extension, TOOL_TIP_PARAM);
    }


    /**
     * Gets the icon for this action.
     * @return The <code>Icon</code> for actions created by
     *         this plugin, or <code>null</code> if the actions created
     *         by this plugin shouldn't have any icon.
     */
    public Icon getIcon() {
        return null;
    }


    /**
     * Gets the <code>EditorKit</code> that instances of
     * this plugin will act on.
     */
    public EditorKit getEditorKit() {
        return editorKit;
    }

}
