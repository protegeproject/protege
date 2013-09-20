package org.protege.editor.core.ui.menu;

import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.action.ProtegeActionPluginJPFImpl;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of the <code>MenuActionPlugin</code> which uses
 * the Java Plugin Framework to provide the details of the plugin.
 */
public class MenuActionPluginJPFImpl extends ProtegeActionPluginJPFImpl implements MenuActionPlugin {
    
    private static Logger logger = Logger.getLogger(MenuActionPluginJPFImpl.class);

    public static final String EXTENSION_POINT_ID = "EditorKitMenuAction";

    private static final String ACCELERATOR_PARAM = "accelerator";

    private static final String URL_PARAM = "url";

    private static final String PATH_PARAM = "path";

    private static final String SEPARATOR = "/";

    private static final String DYNAMIC_PARAM = "dynamic";
    
    private static final String CHECKBOX_PARAM = "checkbox";
    
    private static final String RADIOBUTTON_PARAM = "radiobutton";

    private String parentId;

    private String group;

    private String groupIndex;


    public MenuActionPluginJPFImpl(EditorKit editorKit, IExtension extension) {
        super(editorKit, extension);
        parse();
    }


    private String getPath() {
        // The path corresponds to the path parameter value.
        return getPluginProperty(PATH_PARAM, SEPARATOR);
    }


    public String getParentId() {
        return parentId;
    }


    public String getGroup() {
        return group;
    }


    public String getGroupIndex() {
        return groupIndex;
    }


    public KeyStroke getAccelerator() {
        String acceleratorString = getPluginProperty(ACCELERATOR_PARAM);
        if (acceleratorString != null) {
            KeyStroke ks = KeyStroke.getKeyStroke(acceleratorString);
            if (ks != null) {
                return KeyStroke.getKeyStroke(ks.getKeyCode(),
                                              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ks.getModifiers());
            }
        }
        return null;
    }


    public URL getURL() {
        String urlStr = getPluginProperty(URL_PARAM, null);
        if (urlStr != null){
            try {
                return new URL(urlStr);
            }
            catch (MalformedURLException e) {
                Logger.getLogger(MenuActionPluginJPFImpl.class).warn("Invalid URL for action " + getId(), e);
            }
        }
        return null;
    }


    private boolean isClassSpecified(){
        return getPluginProperty(PluginProperties.CLASS_PARAM_NAME) != null;
    }


    /**
     * Determines if the menu is dynamically constructed
     * after the plugin has been loaded.
     */
    public boolean isDynamic() {
        return PropertyUtil.getBoolean(getPluginProperty(DYNAMIC_PARAM), false);
    }

    @Deprecated
    public boolean isJCheckBox() {
        return isCheckBox();
    }

    public boolean isCheckBox() {
        return PropertyUtil.getBoolean(getPluginProperty(CHECKBOX_PARAM), false);
    }
    
    public boolean isRadioButton() {
        return PropertyUtil.getBoolean(getPluginProperty(RADIOBUTTON_PARAM), false);
    }


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ProtegeAction newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ProtegeAction menuAction = null;
        if (isClassSpecified()){
            menuAction = super.newInstance();
        }
        if (menuAction == null) {
            URL url = getURL();
            if (url != null){
                menuAction = new OpenURLMenuAction(url);
            }
            else{
                menuAction = new NullMenuAction();
            }
        }
        menuAction.putValue(AbstractAction.NAME, getName());
        String toolTip = getToolTipText();
        if (toolTip != null) {
            toolTip = toolTip.replace("\n", "");
            toolTip = toolTip.replace("\t", "");
            menuAction.putValue(AbstractAction.SHORT_DESCRIPTION, toolTip);
        }
        menuAction.putValue(AbstractAction.ACCELERATOR_KEY, getAccelerator());
        menuAction.setEditorKit(getEditorKit());
        try {
            menuAction.initialise();
        }
        catch (Exception e) {
            Logger.getLogger(MenuActionPluginJPFImpl.class).error(e);
        }
        return menuAction;
    }




    /**
     * Parses the path to extract the parent id, the
     * group and the group index.  If the group and group
     * index aren't specified then these default to the
     * empty string.
     */
    private void parse() {
        group = "";
        groupIndex = "";
        String path = getPath();
        int separatorIndex = path.indexOf(SEPARATOR);
        if (separatorIndex > -1) {
            parentId = path.substring(0, separatorIndex).trim();
            String groupPart = path.substring(separatorIndex + 1, path.length()).trim();
            int groupPartIndex = groupPart.indexOf("-");
            if (groupPartIndex > -1) {
                group = groupPart.substring(0, groupPartIndex).trim();
                groupIndex = groupPart.substring(groupPartIndex + 1, groupPart.length());
            }
            else {
                group = groupPart;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Parsed: " + this + " parentId = " + parentId);
        }
    }
    
    @Override
    public String toString() {
        return "[Menu: " + getName() + " -- <" + getGroup() + ", " + getGroupIndex() + ">]";
    }
}
