package org.protege.editor.core.update;

import org.protege.editor.core.ui.preferences.PreferencesPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
