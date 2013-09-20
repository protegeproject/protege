package org.protege.editor.core.ui.action;

import javax.swing.Icon;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePlugin;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A plugin that descirbes a <code>ProtegeAction</code>.
 * The plugin describes essential information such as the
 * name of the action, the tooltip and the icon.  The plugin
 * can also create instances of the action that it describes.
 */
public interface ProtegeActionPlugin extends ProtegePlugin<ProtegeAction> {

    /**
     * Gets the plugin id.
     */
    public String getId();


    /**
     * Gets the name of the action.  This is the name
     * that will be used for the menu item text etc.
     */
    public String getName();


    /**
     * Gets the tooltip text for the action.
     * @return A <code>String</code> representing the tooltip
     *         text, or <code>null</code> if the action created by this
     *         plugin shouldn't have any tooltip text.
     */
    public String getToolTipText();


    /**
     * Gets the icon for this action.
     * @return The <code>Icon</code> for actions created by
     *         this plugin, or <code>null</code> if the actions created
     *         by this plugin shouldn't have any icon.
     */
    public Icon getIcon();


    /**
     * Gets the <code>EditorKit</code> that instances of
     * this plugin will act on.
     */
    public EditorKit getEditorKit();
}
