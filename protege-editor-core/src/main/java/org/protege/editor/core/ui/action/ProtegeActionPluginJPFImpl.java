package org.protege.editor.core.ui.action;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.core.plugin.PluginUtilities;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 29, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class ProtegeActionPluginJPFImpl extends AbstractProtegePlugin<ProtegeAction> implements ProtegeActionPlugin {

    private EditorKit editorKit;

    private IExtension extension;

    public static final String NAME_PARAM = "name";

    public static final String TOOL_TIP_PARAM = "toolTip";


    protected ProtegeActionPluginJPFImpl(EditorKit editorKit, IExtension extension) {
        super(extension);
        this.editorKit = editorKit;
        this.extension = extension;
    }



    /**
     * Gets the name of the action.  This is the name
     * that will be used for the menu item text etc.
     */
    public String getName() {
    	String name = PluginUtilities.getAttribute(extension, NAME_PARAM);
    	if (name != null && name.contains("\\u")) {
    		name = decode(name);
    	}
        return name;
    }


    /**
     * Gets the tooltip text for the action.
     * @return A <code>String</code> representing the tooltip
     *         text, or <code>null</code> if the action created by this
     *         plugin shouldn't have any tooltip text.
     */
    public String getToolTipText() {
    	String tooltip = PluginUtilities.getAttribute(extension, TOOL_TIP_PARAM);
    	if (tooltip != null && tooltip.contains("\\u")) {
    		tooltip = decode(tooltip);
    	}
        return tooltip;
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
    
    static final String decode(final String in) {
        String str = in;
        int index = str.indexOf("\\u");
        while (index > -1) {
            if (index > (str.length() - 6)) {
            	break;
            }
            String substring = str.substring(index + 2, index + 6);
            int hexVal = Integer.parseInt(substring, 16);
            String start = str.substring(0, index);
            String end = str.substring(index + 6);
            str = start + ((char) hexVal) + end;
            index = str.indexOf("\\u");
        }
        return str;
    }
}
