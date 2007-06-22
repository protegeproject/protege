package org.protege.editor.core.ui.about;

import org.java.plugin.registry.PluginDescriptor;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.*;
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
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout());
        JPanel pluginPanel = new JPanel(new BorderLayout());
        pluginPanel.add(new JScrollPane(new PluginInfoTable()));
        pluginPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Plugin information"),
                                                                 BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        add(pluginPanel);
        JPanel logoPanel = new JPanel(new BorderLayout(3, 3));
        JLabel logoLabel = new JLabel(Icons.getIcon("logo.banner.gif"));
        logoPanel.add(logoLabel, BorderLayout.NORTH);
        PluginDescriptor desc = PluginUtilities.getInstance().getPluginManager().getRegistry().getPluginDescriptor(
                ProtegeApplication.ID);
        String versionString = "Version " + desc.getVersion().getMajor() + "." + desc.getVersion().getMinor() + " (Build " + desc.getVersion().getBuild() + ")";
        JLabel label = new JLabel(versionString, JLabel.CENTER);
        logoPanel.add(label, BorderLayout.SOUTH);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        add(logoPanel, BorderLayout.NORTH);
        JLabel copy = new JLabel(
                "<html><body>Protege is a collaborative development effort between Stanford University and University of Manchester.<br><br>" + "Protege-OWL 4 and Protege-Core Framework Copyright (c) The University of Manchester 2006");
        copy.setFont(copy.getFont().deriveFont(10.0f));
        copy.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(copy, BorderLayout.SOUTH);
    }


    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }


    public static void showDialog() {
        JOptionPane.showMessageDialog(null, new AboutPanel(), "About", JOptionPane.PLAIN_MESSAGE);
    }
}
