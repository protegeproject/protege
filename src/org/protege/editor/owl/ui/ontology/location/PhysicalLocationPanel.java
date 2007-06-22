package org.protege.editor.owl.ui.ontology.location;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.ViewBanner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.ShowFileAction;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
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
 * Date: 02-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PhysicalLocationPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(PhysicalLocationPanel.class);

    private OWLEditorKit owlEditorKit;


    public PhysicalLocationPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout(3, 3));
        add(new ViewBanner("Loaded ontology sources", OWLSystemColors.getOWLOntologyColor()), BorderLayout.NORTH);
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Set<OWLOntology> ts = new TreeSet<OWLOntology>(new Comparator<OWLOntology>() {
            public int compare(OWLOntology o1, OWLOntology o2) {
                return o1.getURI().compareTo(o2.getURI());
            }
        });
        ts.addAll(owlEditorKit.getOWLModelManager().getOntologies());
        for (OWLOntology ont : ts) {
            OntologySourcePanel panel = new OntologySourcePanel(ont);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            box.add(panel);
        }
        box.setBackground(Color.WHITE);
        JPanel boxHolder = new JPanel(new BorderLayout());
        boxHolder.setOpaque(false);
        boxHolder.add(box, BorderLayout.NORTH);
        add(ComponentFactory.createScrollPane(boxHolder), BorderLayout.CENTER);
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }


    public static final void showDialog(OWLEditorKit owlEditorKit) {
        PhysicalLocationPanel panel = new PhysicalLocationPanel(owlEditorKit);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
        JDialog dlg = pane.createDialog(owlEditorKit.getWorkspace(), "Ontology source locations");
        dlg.setResizable(true);
        dlg.setVisible(true);
    }


    private class OntologySourcePanel extends JPanel {

        public OntologySourcePanel(OWLOntology ont) {
            setOpaque(false);
            setLayout(new BorderLayout(3, 3));
            JLabel ontURILabel = new JLabel(ont.getURI().toString());
            ontURILabel.setFont(ontURILabel.getFont().deriveFont(Font.BOLD));
            ontURILabel.setIcon(OWLIcons.getIcon("ontology.png"));
            add(ontURILabel, BorderLayout.NORTH);
            final URI physicalURI = owlEditorKit.getOWLModelManager().getOntologyPhysicalURI(ont);
            JLabel locURILabel = new JLabel();
            if (physicalURI.getScheme().equals("file")) {
                locURILabel.setText(new File(physicalURI).toString());
                final JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.add(new ShowFileAction(physicalURI));
                addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            popupMenu.show(OntologySourcePanel.this, e.getX(), e.getY());
                        }
                    }


                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            popupMenu.show(OntologySourcePanel.this, e.getX(), e.getY());
                        }
                    }
                });
            }
            else {
                locURILabel.setText(physicalURI.toString());
            }
            locURILabel.setFont(locURILabel.getFont().deriveFont(12.0f));
            locURILabel.setForeground(Color.DARK_GRAY);
            // Indent the physical URI slightly
            locURILabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            add(locURILabel, BorderLayout.SOUTH);
        }
    }
}
