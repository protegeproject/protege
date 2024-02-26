package org.protege.editor.core.update;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.protege.editor.core.ui.about.PluginInfoTable;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginPreferencesPanel extends PreferencesPanel {

    private JCheckBox checkForUpdatesAtStartupCheckBox;

    private JTextField pluginRegistryEditor;

    public void applyChanges() {
        PluginManager.getInstance().setAutoUpdateEnabled(checkForUpdatesAtStartupCheckBox.isSelected());
        final URL url;
        try {
            url = new URL(pluginRegistryEditor.getText());
            PluginManager.getInstance().setPluginRegistryLocation(url);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public void dispose() throws Exception {
    }


    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        add(panel, BorderLayout.NORTH);

        panel.addGroup("Auto update");
        checkForUpdatesAtStartupCheckBox = new JCheckBox("Automatically check for plugin updates at start up",
                PluginManager.getInstance().isAutoUpdateEnabled());
        panel.addGroupComponent(checkForUpdatesAtStartupCheckBox);

        panel.addSeparator();

        panel.addGroup("Plugin registry");

        pluginRegistryEditor = new JTextField(PluginManager.getInstance().getPluginRegistryLocation().toString(), 50);
        panel.addGroupComponent(pluginRegistryEditor);
        panel.addHelpText("This is the location that Protégé will use to check which plugins are available");
        JButton resetToDefaultRegistry = new JButton("Reset to default registry location");
        resetToDefaultRegistry.addActionListener(e -> pluginRegistryEditor.setText(PluginManager.DEFAULT_REGISTRY));
        panel.addIndentedGroupComponent(resetToDefaultRegistry);

        panel.addSeparator();
        panel.addGroup("Installed plugins");
        JScrollPane pluginInfoScrollPane = new JScrollPane(new PluginInfoTable());
        pluginInfoScrollPane.setPreferredSize(new Dimension(500, 300));
        panel.addGroupComponent(pluginInfoScrollPane);
    }
}
