package org.protege.editor.core.ui.view;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.coode.mdock.ComponentFactory;
import org.protege.editor.core.ui.workspace.Workspace;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewComponentFactory implements ComponentFactory {

    private Workspace workspace;


    public ViewComponentFactory(Workspace workspace) {
        this.workspace = workspace;
    }


    public JComponent createComponent(Map<String, String> properties) {
        String pluginId = properties.get("pluginId");
        ViewComponentPluginLoader loader = new ViewComponentPluginLoader(workspace);
        for (ViewComponentPlugin plugin : loader.getPlugins()) {
            if (plugin.getId().equals(pluginId)) {
                return new View(plugin, workspace);
            }
        }
        return new JButton("Couldn't load");
    }
}
