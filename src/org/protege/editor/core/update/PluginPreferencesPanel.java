package org.protege.editor.core.update;

import org.protege.editor.core.ui.preferences.PreferencesPanel;
import org.protege.editor.core.ui.util.ComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginPreferencesPanel extends PreferencesPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 3990983369055447871L;

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

        Box settingsHolder = new Box(BoxLayout.PAGE_AXIS);
        settingsHolder.add(createAutoUpdatePanel());
        settingsHolder.add(Box.createVerticalStrut(12));
        settingsHolder.add(createRegistryPanel());

        add(settingsHolder, BorderLayout.NORTH);
    }


    private Component createAutoUpdatePanel() {
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(0.0f);
        box.setBorder(ComponentFactory.createTitledBorder("Auto update"));

        checkForUpdatesAtStartupCheckBox = new JCheckBox("Automatically check for plugin updates at start up",
                                                         PluginManager.getInstance().isAutoUpdateEnabled());
        checkForUpdatesAtStartupCheckBox.setAlignmentX(0.0f);

        JButton checkForUpdatesNow = new JButton(new AbstractAction("Check for updates now") {
            /**
             * 
             */
            private static final long serialVersionUID = -1489049180196552810L;

            public void actionPerformed(ActionEvent e) {
                PluginManager.getInstance().checkForUpdates();
            }
        });
        checkForUpdatesNow.setAlignmentX(0.0f);

        box.add(checkForUpdatesAtStartupCheckBox);
        box.add(Box.createVerticalStrut(12));
        box.add(checkForUpdatesNow);

        return box;
    }


    private Component createRegistryPanel() {
        Box registryHolder = new Box(BoxLayout.PAGE_AXIS);
        registryHolder.setAlignmentX(0.0f);
        registryHolder.setBorder(ComponentFactory.createTitledBorder("Plugin registry"));

        pluginRegistryEditor = new JTextField(PluginManager.getInstance().getPluginRegistryLocation().toString());
        pluginRegistryEditor.setColumns(30);

        JButton resetToDefaultRegistry = new JButton(new AbstractAction("Reset to default"){
            /**
             * 
             */
            private static final long serialVersionUID = 2060172556249616568L;

            public void actionPerformed(ActionEvent event) {
                pluginRegistryEditor.setText(PluginManager.DEFAULT_REGISTRY);
            }
        });

        JButton checkForDownloadsNow = new JButton(new AbstractAction("Check for downloads now"){

            /**
             * 
             */
            private static final long serialVersionUID = 6345676228776716764L;

            public void actionPerformed(ActionEvent event) {
                PluginManager.getInstance().checkForDownloads();
            }
        });
        checkForDownloadsNow.setAlignmentX(0.0f);

        Box registryLocHolder = new Box(BoxLayout.LINE_AXIS);
        registryLocHolder.setAlignmentX(0.0f);
        registryLocHolder.add(pluginRegistryEditor);
        registryLocHolder.add(resetToDefaultRegistry);

        registryHolder.add(Box.createVerticalStrut(12));
        registryHolder.add(new JLabel("This is the location P4 will check to see which plugins are available."));
        registryHolder.add(Box.createVerticalStrut(12));
        registryHolder.add(registryLocHolder);
        registryHolder.add(Box.createVerticalStrut(12));
        registryHolder.add(checkForDownloadsNow);

        return registryHolder;
    }
}
