package org.protege.editor.core.ui.about;


import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.*;


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
        BundleContext applicationContext = PluginUtilities.getInstance().getApplicationContext();
        Bundle application = applicationContext.getBundle();
        Version v = PluginUtilities.getBundleVersion(application);

        String versionString = "Version " + v.getMajor() + "." + v.getMinor() + "." + v.getMicro() +  " (Build " + v.getQualifier() + ")";
        JLabel label = new JLabel(versionString, JLabel.CENTER);
        logoPanel.add(label, BorderLayout.SOUTH);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        add(logoPanel, BorderLayout.NORTH);
		JLabel copy = new JLabel(
				"<html><body>Prot\u00E9g\u00E9 is developed by the Stanford Center for Biomedical Informatics Research. Prot\u00E9g\u00E9 is a national "
						+ "resource for biomedical ontologies and knowledge bases supported by the National Institute of General Medical Sciences.<br><br>"
						+ "Previous versions of the Prot\u00E9g\u00E9 4 series were developed in collaboration with The University of Manchester.");
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
