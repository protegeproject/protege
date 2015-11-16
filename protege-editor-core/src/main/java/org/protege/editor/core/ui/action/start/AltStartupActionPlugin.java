package org.protege.editor.core.ui.action.start;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.action.ProtegeActionPluginJPFImpl;

import javax.swing.*;


public class AltStartupActionPlugin extends AbstractProtegePlugin<AltStartupAction> implements Comparable<AltStartupActionPlugin>{
	public static final String ID = "OtherStartupActions";
	
	public static final String SORT_ORDER_PARAM = "sortIndex";

    private JFrame parent;

    private IExtension extension;


    public AltStartupActionPlugin(JFrame parent, IExtension extension) {
        super(extension);
        this.parent = parent;
        this.extension = extension;
    }



    /**
     * Gets the name of the action.  This is the name
     * that will be used for the menu item text etc.
     */
    public String getName() {
        return PluginUtilities.getAttribute(extension, ProtegeActionPluginJPFImpl.NAME_PARAM);
    }
    
    public String getSortIndex() {
    	return PluginUtilities.getAttribute(extension, SORT_ORDER_PARAM);
    }
    
    public int compareTo(AltStartupActionPlugin o) {
    	int comp1 = getSortIndex().compareTo(o.getSortIndex());
    	if (comp1 != 0) {
    		return comp1;
    	}
    	return getId().compareTo(o.getId());
    }


    /**
     * Gets the tooltip text for the action.
     * @return A <code>String</code> representing the tooltip
     *         text, or <code>null</code> if the action created by this
     *         plugin shouldn't have any tooltip text.
     */
    public String getToolTipText() {
        return PluginUtilities.getAttribute(extension, ProtegeActionPluginJPFImpl.TOOL_TIP_PARAM);
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
    
    @Override
    public AltStartupAction newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    	AltStartupAction a = super.newInstance();
    	a.setParent(parent);
    	a.putValue(AbstractAction.NAME, getName());
        String toolTip = getToolTipText();
        if (toolTip != null) {
            toolTip = toolTip.replace("\n", "");
            toolTip = toolTip.replace("\t", "");
            a.putValue(AbstractAction.SHORT_DESCRIPTION, toolTip);
        }
    	return a;
    }

}
