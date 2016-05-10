package org.protege.editor.core.ui.action;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ToolBarActionPluginJPFImpl extends ProtegeActionPluginJPFImpl implements ToolBarActionPlugin {

    public static final String EXTENSION_POINT_ID = "ToolBarAction";

    private static final String GROUP_PARAM = "group";

    public static final String GROUP_INDEX_PARAM = "groupIndex";

    private static final String DEFAULT_GROUP = "Z";

    private static final String DEFAULT_GROUP_INDEX = "Z";


    public ToolBarActionPluginJPFImpl(EditorKit editorKit, IExtension extension) {
        super(editorKit, extension);
    }


    public String getGroup() {
        return getPluginProperty(GROUP_PARAM, DEFAULT_GROUP);
    }


    public String getGroupIndex() {
        return getPluginProperty(GROUP_PARAM, DEFAULT_GROUP_INDEX);
    }


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ProtegeAction newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ProtegeAction menuAction = super.newInstance();
        menuAction.putValue(AbstractAction.NAME, getName());
        menuAction.putValue(AbstractAction.SHORT_DESCRIPTION, getToolTipText());
        menuAction.setEditorKit(getEditorKit());
        return menuAction;
    }
}
