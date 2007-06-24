package org.protege.editor.core.update;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.protege.editor.core.ui.preferences.PreferencesPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class AutoUpdatePreferencesPanel extends PreferencesPanel {

    private JCheckBox checkForUpdatesAtStartupCheckBox;


    public void applyChanges() {
        UpdateManager.getInstance().setAutoUpdateEnabled(checkForUpdatesAtStartupCheckBox.isSelected());
    }


    public void dispose() throws Exception {
    }


    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        boolean checkOnStartUp = UpdateManager.getInstance().isAutoUpdateEnabled();
        Box box = new Box(BoxLayout.Y_AXIS);
        checkForUpdatesAtStartupCheckBox = new JCheckBox("Automatically check for plugin updates at start up",
                                                         checkOnStartUp);
        checkForUpdatesAtStartupCheckBox.setAlignmentX(0.0f);
        box.add(checkForUpdatesAtStartupCheckBox);
        add(box, BorderLayout.NORTH);
        JButton checkForUpdatesNow = new JButton(new AbstractAction("Check for updates now") {
            public void actionPerformed(ActionEvent e) {
                UpdateManager.getInstance().checkForUpdates(true);
            }
        });
        box.add(Box.createVerticalStrut(12));
        checkForUpdatesNow.setAlignmentX(0.0f);
        box.add(checkForUpdatesNow);
    }
}
