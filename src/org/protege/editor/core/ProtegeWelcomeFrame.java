package org.protege.editor.core;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.EditorKitDescriptor;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.ui.OpenFromURIPanel;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.LinkLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Map;
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
 * Date: Mar 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeWelcomeFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(ProtegeWelcomeFrame.class);


    public ProtegeWelcomeFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new ProtegeWelcomePanel());
        pack();
        centre();
    }


    private void centre() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = getSize();
        setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
    }


    private class ProtegeWelcomePanel extends JPanel {

        private Icon background;

        private Map<String, Action> linkMap;


        public ProtegeWelcomePanel() {
            setBackground(Color.WHITE);
            background = Icons.getIcon("logo.wizard.png");

            final ProtegeManager manager = ProtegeManager.getInstance();

            Color color = PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.PROPERTY_COLOR_KEY),
                                                Color.GRAY);
            Color classColor = PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.CLASS_COLOR_KEY),
                                                     Color.GRAY);

            int strutHeight = 10;

            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            add(panel);

            JLabel label = new JLabel("Welcome to Prot\u00E9g\u00E9");
            label.setFont(getFont().deriveFont(Font.BOLD, 35.0f));
            label.setBorder(BorderFactory.createEmptyBorder(30, 0, 50, 0));
            label.setForeground(Color.LIGHT_GRAY);
            panel.add(label, BorderLayout.NORTH);

            Box box = new Box(BoxLayout.Y_AXIS);
            box.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
            panel.add(box);

            for (final EditorKitFactoryPlugin plugin : manager.getEditorKitFactoryPlugins()) {

                box.add(new LinkLabel("Create new " + plugin.getLabel(), new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (ProtegeManager.getInstance().createAndSetupNewEditorKit(plugin)) {
                                dispose();
                            }
                        }
                        catch (Exception e1) {
                            logger.error(e1);
                        }
                    }
                }));

                box.add(Box.createVerticalStrut(strutHeight));

                box.add(new LinkLabel("Open " + plugin.getLabel(), new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (ProtegeManager.getInstance().openAndSetupEditorKit(plugin)) {
                                dispose();
                            }
                        }
                        catch (Exception e1) {
                            logger.error(e1);
                        }
                    }
                }));

                box.add(Box.createVerticalStrut(strutHeight));

                box.add(new LinkLabel("Open " + plugin.getLabel() + " from URI", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            URI uri = OpenFromURIPanel.showDialog();
                            if (uri != null) {
                                ProtegeManager.getInstance().loadAndSetupEditorKitFromURI(plugin, uri);
                            }
                        }
                        catch (Exception e1) {
                            logger.error(e1);
                        }
                    }
                }));
            }

            box.add(Box.createVerticalStrut(strutHeight + 20));

            if (RecentEditorKitManager.getInstance().getDescriptors().size() > 0) {
                Box recentLinkBox = new Box(BoxLayout.Y_AXIS);

                recentLinkBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, classColor),
                        " Open recent ",
                        0,
                        0,
                        getFont().deriveFont(Font.BOLD),
                        color), BorderFactory.createEmptyBorder(20, 20, 20, 20)));


                for (final EditorKitDescriptor desc : RecentEditorKitManager.getInstance().getDescriptors()) {
                    recentLinkBox.add(new LinkLabel(desc.getLabel(), new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                manager.openAndSetupRecentEditorKit(desc);
                                dispose();
                            }
                            catch (Exception e1) {
                                logger.error(e1);
                            }
                        }
                    }));
                }
                box.add(recentLinkBox);
            }
        }


        public Dimension getPreferredSize() {
            Dimension prefSize = super.getPreferredSize();
            prefSize.width += 30;
            prefSize.height += 30;
            return prefSize;
        }


        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            background.paintIcon(this, g2, 0, 0);
            Color oldColor = g2.getColor();
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fillRect(0, 0, background.getIconWidth(), background.getIconHeight());

            g2.setColor(oldColor);
        }
    }
}
