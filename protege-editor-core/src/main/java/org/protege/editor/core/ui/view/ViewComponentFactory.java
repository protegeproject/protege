package org.protege.editor.core.ui.view;

import org.coode.mdock.ComponentFactory;
import org.protege.editor.core.ui.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


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

    private ViewComponentPlugin emptyPlugin;


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
        // we need to return a fully functioning view so that the close button works
        String msg = String.format(
                "<html><body>" +
                        "<div style='font-weight: bold; padding-bottom: 20px;'>This view could not be loaded because its content is " +
                        "provided by a view " +
                        "plugin that could not be found.</div>" +
                        "<div style='padding-bottom: 20px;'>" +
                        "This problem may have been caused because the plugin is not installed or it may have been " +
                        "caused by incompatible changes to the plugin in question.</div>" +
                        "<div>Please check that the relevant plugin is installed.  You can also try resetting " +
                        "the tab to its default state to see if " +
                        "this solves the issue.</div>" +
                "</body></html>"
        );
        return new View(getEmptyPlugin(pluginId, msg), workspace);
    }

    public ViewComponentPlugin getEmptyPlugin(final String pluginId, final String message) {
        if (emptyPlugin == null){
            emptyPlugin = new ViewComponentPluginAdapter(){

                public String getLabel() {
                    return pluginId;
                }

                public Workspace getWorkspace() {
                    return workspace;
                }

                public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
                    return new ViewComponent(){

                        public void initialise() throws Exception {
                            setLayout(new BorderLayout());
                            JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
                            msgLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                            add(msgLabel, BorderLayout.CENTER);
                        }

                        public void dispose() throws Exception {
                            // do nothing
                        }
                    };
                }
            };
        }
        return emptyPlugin;
    }
}
