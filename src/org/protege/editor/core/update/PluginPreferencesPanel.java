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

        boolean checkOnStartUp = PluginManager.getInstance().isAutoUpdateEnabled();
        checkForUpdatesAtStartupCheckBox = new JCheckBox("Automatically check for plugin updates at start up",
                                                         checkOnStartUp);
        checkForUpdatesAtStartupCheckBox.setAlignmentX(0.0f);

        JButton checkForUpdatesNow = new JButton(new AbstractAction("Check for updates now") {
            public void actionPerformed(ActionEvent e) {
                PluginManager.getInstance().checkForUpdates(true);
            }
        });
        checkForUpdatesNow.setAlignmentX(0.0f);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setBorder(ComponentFactory.createTitledBorder("Auto update"));
        box.add(checkForUpdatesAtStartupCheckBox);
        box.add(Box.createVerticalStrut(12));
        box.add(checkForUpdatesNow);

        pluginRegistryEditor = new JTextField(PluginManager.getInstance().getPluginRegistryLocation().toString());
        Box registryHolder = new Box(BoxLayout.PAGE_AXIS);
        registryHolder.setBorder(ComponentFactory.createTitledBorder("Plugin registry"));
        registryHolder.add(Box.createVerticalStrut(12));
        registryHolder.add(new JLabel("This is the location P4 will check to see which plugins are available."));
        registryHolder.add(pluginRegistryEditor);

        Box settingsHolder = new Box(BoxLayout.PAGE_AXIS);
        settingsHolder.add(box);
        settingsHolder.add(Box.createVerticalStrut(12));
        settingsHolder.add(registryHolder);

        add(settingsHolder, BorderLayout.NORTH);
    }
}
