package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLOntologyHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * Date: 28-Oct-2007<br><br>
 */
public class OntologyImportsAndNavigationPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 7247887353859536512L;

    private OWLEditorKit owlEditorKit;

    private OWLModelManagerTree<OWLOntology> tree;

    private JCheckBox showImportsCheckBox;


    public OntologyImportsAndNavigationPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        setLayout(new BorderLayout());
        tree = new OWLModelManagerTree<OWLOntology>(owlEditorKit, new OWLOntologyHierarchyProvider(owlEditorKit.getModelManager()));
        add(new JScrollPane(tree));
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                OWLOntology ont = tree.getSelectedOWLObject();
                if (ont != null) {
                    owlEditorKit.getModelManager().setActiveOntology(ont);
                    owlEditorKit.getWorkspace().getOWLSelectionModel().setSelectedObject(ont);
                }
            }
        });
        tree.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                OWLOntology ont = tree.getSelectedOWLObject();
                if (ont != null) {
                    owlEditorKit.getWorkspace().getOWLSelectionModel().setSelectedObject(null);
                    owlEditorKit.getWorkspace().getOWLSelectionModel().setSelectedObject(ont);
                }
            }
        });
        showImportsCheckBox = new JCheckBox(new AbstractAction("Show imported axioms") {

            /**
             * 
             */
            private static final long serialVersionUID = -1171161540845340564L;

            public void actionPerformed(ActionEvent e) {
                setShowImports();
            }
        });
// @@TODO v3 port - no longer supports isIncludesImports
//        showImportsCheckBox.setSelected(owlEditorKit.getModelManager().isIncludeImports());
        showImportsCheckBox.setFont(showImportsCheckBox.getFont().deriveFont(Font.PLAIN, 11.0f));
        add(showImportsCheckBox, BorderLayout.SOUTH);
        tree.setCellRenderer(new OntologyTreeCellRenderer());
    }


    private void setShowImports() {
// @@TODO v3 port - no longer supports isIncludesImports      
//        owlEditorKit.getModelManager().setIncludeImports(showImportsCheckBox.isSelected());
    }


    private class OntologyTreeCellRenderer extends DefaultTreeCellRenderer {

        /**
         * 
         */
        private static final long serialVersionUID = -5285478535989683861L;

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
                                                      int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,  row, hasFocus);
            OWLOntology ontology = (OWLOntology) (((OWLObjectTreeNode) value).getOWLObject());
            if(ontology == null) {
                return label;
            }
            String uriString = ontology.getOntologyID().toString();
                int lastSepIndex = uriString.lastIndexOf('/');
                StringBuilder sb = new StringBuilder();
                if (lastSepIndex != -1) {
                    sb.append("<html><body>");
                    sb.append(uriString.substring(lastSepIndex + 1, uriString.length()));
                    sb.append("    ");
                    sb.append("<font color=\"gray\">");
                    sb.append(uriString);
                    sb.append("</font>");
                    sb.append("</body></html>");
                }
                else {
                    sb.append(uriString);
                }
                label.setText(sb.toString());
                label.setIcon(owlEditorKit.getWorkspace().getOWLIconProvider().getIcon(ontology));
                return label;
        }
    }
}
