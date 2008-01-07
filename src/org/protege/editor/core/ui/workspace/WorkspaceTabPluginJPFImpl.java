package org.protege.editor.core.ui.workspace;




import java.net.URL;

import javax.swing.Icon;

import org.eclipse.core.runtime.IExtension;
import org.osgi.framework.Bundle;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.plugin.PluginUtilities;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of the <code>WorkspaceTabPlugin</code> that uses
 * the Java Plugin Framework to obtain the details of the plugins
 */
public class WorkspaceTabPluginJPFImpl implements WorkspaceTabPlugin {

    /**
     * The plugin extension point ID
     */
    public static final String ID = "WorkspaceTab";

    /**
     * The plugin extension paramter name that identifies the label for the tab
     */
    private static final String LABEL_PARAM = "label";

    /**
     * The plugin extension parameter name that identifies the icon for the tab.
     */
    private static final String ICON_PARAM = "icon";


    /**
     * The plugin extension parameter name that identifies the icon for the tab.
     */
    private static final String INDEX_PARAM = "index";


    private static final String DEFAULT_VIEW_CONFIG_FILE_NAME_PARAM = "defaultViewConfigFileName";

    private TabbedWorkspace workspace;

    private IExtension extension;


    public WorkspaceTabPluginJPFImpl(TabbedWorkspace workspace, IExtension extension) {
        this.extension = extension;
        this.workspace = workspace;
    }


    /**
     * Gets the workspace that this plugin creates
     * tabs for.
     * @return A <code>TabbedWorkspace</code> that the tabs instantiated
     *         by this plugin belong to.
     */
    public TabbedWorkspace getWorkspace() {
        return workspace;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    public String getLabel() {
        return PluginProperties.getParameterValue(extension, LABEL_PARAM, "<Error! No label defined>");
    }


    public String getIndex() {
        return PluginProperties.getParameterValue(extension, INDEX_PARAM, "Z");
    }


    public Icon getIcon() {
        return null;
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public WorkspaceTab newInstance() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        ExtensionInstantiator<WorkspaceTab> instantiator = new ExtensionInstantiator<WorkspaceTab>(extension);
        WorkspaceTab tab = instantiator.instantiate();
        tab.setup(this);
        return tab;
    }


    public URL getDefaultViewConfigFile() {
        PluginUtilities util = PluginUtilities.getInstance();
        Bundle contributor = util.getBundle(extension);
        String resource = PluginProperties.getParameterValue(extension, DEFAULT_VIEW_CONFIG_FILE_NAME_PARAM, null);
        if (resource != null) {
            return contributor.getResource(resource);
        }
        else {
            return null;
        }
    }
}
