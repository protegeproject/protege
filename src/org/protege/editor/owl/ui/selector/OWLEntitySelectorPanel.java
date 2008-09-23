package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.list.RemovableObjectList;
import org.protege.editor.core.Disposable;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.HashSet;
/*
 * Copyright (C) 2008, University of Manchester
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 21-Sep-2008<br><br>
 */
public class OWLEntitySelectorPanel extends JPanel implements OWLObjectSelector<OWLEntity>, Disposable {

     private OWLClassSelectorPanel classSelectorPanel;

    private OWLObjectPropertySelectorPanel objectPropertySelectorPanel;

    private OWLDataPropertySelectorPanel dataPropertySelectorPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;

    private JTabbedPane tabbedPane;

    private RemovableObjectList entityList;

    private JButton button;

    private JScrollPane sp;


    public OWLEntitySelectorPanel(OWLEditorKit owlEditorKit) {
        setLayout(new EntitySelectorPanelLayoutManager());
        classSelectorPanel = new OWLClassSelectorPanel(owlEditorKit, false);
        classSelectorPanel.setBorder(null);
        objectPropertySelectorPanel = new OWLObjectPropertySelectorPanel(owlEditorKit, false);
        objectPropertySelectorPanel.setBorder(null);
        dataPropertySelectorPanel = new OWLDataPropertySelectorPanel(owlEditorKit, false);
        dataPropertySelectorPanel.setBorder(null);
        individualSelectorPanel = new OWLIndividualSelectorPanel(owlEditorKit, false);
        individualSelectorPanel.setBorder(null);
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Classes", classSelectorPanel);
        tabbedPane.add("Object properties", objectPropertySelectorPanel);
        tabbedPane.add("Data properties", dataPropertySelectorPanel);
        tabbedPane.add("Individuals", individualSelectorPanel);
        entityList = new RemovableObjectList();
        entityList.setCellRenderer(new OWLCellRenderer(owlEditorKit));

        entityList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    transmitSelectionFromList();
                }
            }
        });
        add(tabbedPane);

        add(button = new JButton(new AbstractAction(">>") {
            public void actionPerformed(ActionEvent e) {
                addSelectedItems();
            }
        }));
        sp = new JScrollPane(entityList);
        add(sp);
    }

    public void transmitSelectionFromList() {
        OWLEntity ent = (OWLEntity) entityList.getSelectedObject();
        if (ent != null) {
            if(ent.isOWLClass()) {
                tabbedPane.setSelectedComponent(classSelectorPanel);
                classSelectorPanel.setSelection(ent.asOWLClass());
            }
            else if(ent.isOWLObjectProperty()) {
                tabbedPane.setSelectedComponent(objectPropertySelectorPanel);
                objectPropertySelectorPanel.setSelection(ent.asOWLObjectProperty());
            }
            else if(ent.isOWLDataProperty()) {
                tabbedPane.setSelectedComponent(dataPropertySelectorPanel);
                dataPropertySelectorPanel.setSelection(ent.asOWLDataProperty());
            }
            else if(ent.isOWLIndividual()) {
                tabbedPane.setSelectedComponent(individualSelectorPanel);
                individualSelectorPanel.setSelection(ent.asOWLIndividual());
            }
        }
    }


    public OWLEntity getSelectedObject() {
        return (OWLEntity) entityList.getSelectedObject();
    }


    public Set<OWLEntity> getSelectedObjects() {
        return new HashSet<OWLEntity>(entityList.getListItems());
    }

    public void setSelection(Set<OWLEntity> entities) {
        entityList.setListData(entities.toArray());
    }


    public void dispose() {
        classSelectorPanel.dispose();
        objectPropertySelectorPanel.dispose();
        dataPropertySelectorPanel.dispose();
        individualSelectorPanel.dispose();
    }


    public String getID() {
        return "modularity.selectsignature";
    }


    public String getTitle() {
        return "Select signature";
    }

    public void addSelectedItems() {
            entityList.addObject(getCurrentSelection());
    }

    public Set<? extends OWLEntity> getCurrentSelection() {
        Component selComponent = tabbedPane.getSelectedComponent();
        return ((OWLObjectSelector<? extends OWLEntity>) selComponent).getSelectedObjects();
    }


    private class EntitySelectorPanelLayoutManager implements LayoutManager {

        public void addLayoutComponent(String name, Component comp) {
        }


        public void removeLayoutComponent(Component comp) {
        }


        public Dimension preferredLayoutSize(Container parent) {
            Insets insets = parent.getInsets();
            Dimension dim = tabbedPane.getPreferredSize();
            int totalWidth = insets.left + insets.right + 2 * dim.width + button.getPreferredSize().width;
            int totalHeight = insets.top + insets.bottom + dim.height;
            return new Dimension(totalWidth, totalHeight);
        }


        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0);
        }


        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            int xOffset = insets.left;
            int yOffset = insets.top;
            int availWidth = parent.getWidth() - insets.left - insets.right;
            int availHeight = parent.getHeight() - insets.top - insets.bottom;
            Dimension buttonPrefDim = button.getPreferredSize();
            int leftAndRightColWidth = (availWidth - buttonPrefDim.width) / 2;
            tabbedPane.setBounds(xOffset, yOffset, leftAndRightColWidth, availHeight);
            Component tabComponent = tabbedPane.getComponentAt(0);
            int listYOffset = tabComponent.getBounds().y;
            sp.setBounds(xOffset + leftAndRightColWidth + buttonPrefDim.width,
                                 yOffset + listYOffset,
                                 tabComponent.getWidth(),
                                 tabComponent.getHeight());
            button.setBounds(xOffset + leftAndRightColWidth, yOffset + availHeight/2, buttonPrefDim.width, buttonPrefDim.height);
        }
    }

}
