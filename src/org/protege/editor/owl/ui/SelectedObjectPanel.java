package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.frame.OWLEntityFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

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
 * Date: 29-Oct-2007<br><br>
 *
 * A panel that displays information about selected objects (specifically
 * entities and ontologies) - just an experimental hack really.
 */
public class SelectedObjectPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -1836316447338285493L;

    private OWLEditorKit owlEditorKit;

    private OWLEntityFrame entityFrame;

    private OWLFrameList frameList;

    private CardLayout layout;

    private JPanel cardPanel;

    private OWLOntologyDisplayPanel ontologyPanel;

    private JLabel objectDisplayLabel;

    private JCheckBox showUsageCheckBox;

    private JSplitPane usageSplitPane;

    private UsagePanel usagePanel;


    public SelectedObjectPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        setLayout(new BorderLayout(3, 3));
        cardPanel = new JPanel(layout = new CardLayout());
        add(cardPanel);
        objectDisplayLabel = new JLabel();
        JPanel headerPanel = new JPanel(new BorderLayout(7, 7));
        headerPanel.add(objectDisplayLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        showUsageCheckBox = new JCheckBox(new AbstractAction("Show usage") {

            /**
             * 
             */
            private static final long serialVersionUID = -8105770102874054033L;

            public void actionPerformed(ActionEvent e) {
                showUsagePanel(showUsageCheckBox.isSelected());
            }
        });
        headerPanel.add(showUsageCheckBox, BorderLayout.EAST);

        entityFrame = new OWLEntityFrame(getOWLEditorKit());
        frameList = new OWLFrameList(getOWLEditorKit(), entityFrame);
        cardPanel.add("ENTITIES", new JScrollPane(frameList));
        owlEditorKit.getWorkspace().getOWLSelectionModel().addListener(new OWLSelectionModelListener() {

            public void selectionChanged() throws Exception {
                OWLObject selObj = getOWLWorkspace().getOWLSelectionModel().getSelectedObject();
                frameList.setRootObject(selObj);
                if (selObj instanceof OWLEntity) {
                    layout.show(cardPanel, "ENTITIES");
                }
                else if(selObj instanceof OWLOntology) {
                    ontologyPanel.setOntology((OWLOntology) selObj);
                    layout.show(cardPanel, "ONTOLOGY");
                }
                updateLabel(selObj);
            }
        });
        frameList.setRootObject(null);
        getOWLModelManager().addListener(new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
                if(event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    OWLOntology ontology = SelectedObjectPanel.this.getOWLModelManager().getActiveOntology();
                    ontologyPanel.setOntology(ontology);
                    layout.show(cardPanel, "ONTOLOGY");
                    updateLabel(ontology);
                }
            }
        });
        cardPanel.add("ONTOLOGY", ontologyPanel = new OWLOntologyDisplayPanel(owlEditorKit));
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        usagePanel = new UsagePanel(owlEditorKit);
        usageSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        usageSplitPane.setResizeWeight(0.9);
        usageSplitPane.setRightComponent(usagePanel);
        usageSplitPane.setBorder(null);
    }

    private void showUsagePanel(boolean b) {
        if (b) {
            remove(cardPanel);
            usageSplitPane.setLeftComponent(cardPanel);
            add(usageSplitPane);
            revalidate();
        }
        else {
            remove(usageSplitPane);
            add(cardPanel);
            revalidate();
        }
    }

    private void updateLabel(final OWLObject object) {
        object.accept(new OWLObjectVisitorAdapter() {

            public void visit(OWLClass owlClass) {
                objectDisplayLabel.setText("<html><body><b>Class: </b>" + owlEditorKit.getModelManager().getRendering(object) + "</body></html>");
            }


            public void visit(OWLDataProperty owlDataProperty) {
                objectDisplayLabel.setText("<html><body><b>Data property: </b>" + owlEditorKit.getModelManager().getRendering(object) + "</body></html>");
            }


            public void visit(OWLIndividual owlIndividual) {
                objectDisplayLabel.setText("<html><body><b>Individual: </b>" + owlEditorKit.getModelManager().getRendering(object) + "</body></html>");
            }


            public void visit(OWLObjectProperty owlObjectProperty) {
                objectDisplayLabel.setText("<html><body><b>Object property: </b>" + owlEditorKit.getModelManager().getRendering(object) + "</body></html>");
            }


            public void visit(OWLOntology owlOntology) {
                objectDisplayLabel.setText("<html><body><b>Ontology: </b>" + owlEditorKit.getModelManager().getRendering(owlOntology) + "</body></html>");
            }
        });
    }

    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }

    public OWLWorkspace getOWLWorkspace() {
        return getOWLEditorKit().getWorkspace();
    }

    public OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }
}
