package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.list.RemovableObjectList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
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

    private OWLDataTypeSelectorPanel datatypeSelectorPanel;

    private OWLAnnotationPropertySelectorPanel annotationPropertySelectorPanel;

    private JTabbedPane tabbedPane;

    private RemovableObjectList entityList;

    private JButton button;

    private JScrollPane sp;

    private OWLEntityVisitor selectionSetter = new OWLEntityVisitor(){

        public void visit(OWLClass owlClass) {
            tabbedPane.setSelectedComponent(classSelectorPanel);
            classSelectorPanel.setSelection(owlClass);
        }


        public void visit(OWLObjectProperty owlObjectProperty) {
            tabbedPane.setSelectedComponent(objectPropertySelectorPanel);
            objectPropertySelectorPanel.setSelection(owlObjectProperty);
        }


        public void visit(OWLDataProperty owlDataProperty) {
            tabbedPane.setSelectedComponent(dataPropertySelectorPanel);
            dataPropertySelectorPanel.setSelection(owlDataProperty);
        }


        public void visit(OWLNamedIndividual owlNamedIndividual) {
            tabbedPane.setSelectedComponent(individualSelectorPanel);
            individualSelectorPanel.setSelection(owlNamedIndividual);
        }


        public void visit(OWLDatatype owlDatatype) {
            tabbedPane.setSelectedComponent(datatypeSelectorPanel);
            datatypeSelectorPanel.setSelection(owlDatatype);
        }


        public void visit(OWLAnnotationProperty owlAnnotationProperty) {
            tabbedPane.setSelectedComponent(annotationPropertySelectorPanel);
            annotationPropertySelectorPanel.setSelection(owlAnnotationProperty);
        }
    };


    public OWLEntitySelectorPanel(OWLEditorKit owlEditorKit) {
        this(owlEditorKit, true);
    }

    public OWLEntitySelectorPanel(OWLEditorKit owlEditorKit, boolean multiselect) {

        classSelectorPanel = new OWLClassSelectorPanel(owlEditorKit, false);
        classSelectorPanel.setBorder(null);

        objectPropertySelectorPanel = new OWLObjectPropertySelectorPanel(owlEditorKit, false);
        objectPropertySelectorPanel.setBorder(null);

        dataPropertySelectorPanel = new OWLDataPropertySelectorPanel(owlEditorKit, false);
        dataPropertySelectorPanel.setBorder(null);

        individualSelectorPanel = new OWLIndividualSelectorPanel(owlEditorKit, false);
        individualSelectorPanel.setBorder(null);

        datatypeSelectorPanel = new OWLDataTypeSelectorPanel(owlEditorKit, false);
        datatypeSelectorPanel.setBorder(null);

        annotationPropertySelectorPanel = new OWLAnnotationPropertySelectorPanel(owlEditorKit, false);
        annotationPropertySelectorPanel.setBorder(null);


        tabbedPane = new JTabbedPane();
        tabbedPane.add("Classes", classSelectorPanel);
        tabbedPane.add("Object properties", objectPropertySelectorPanel);
        tabbedPane.add("Data properties", dataPropertySelectorPanel);
        tabbedPane.add("Individuals", individualSelectorPanel);
        tabbedPane.add("Datatypes", datatypeSelectorPanel);
        tabbedPane.add("Annotation Properties", annotationPropertySelectorPanel);

        if (!multiselect){
            setLayout(new BorderLayout());
            add(tabbedPane, BorderLayout.CENTER);
        }
        else{
            setLayout(new EntitySelectorPanelLayoutManager());
            add(tabbedPane);
            entityList = new RemovableObjectList();
            entityList.setCellRenderer(new OWLCellRenderer(owlEditorKit));

            entityList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if(!e.getValueIsAdjusting()) {
                        transmitSelectionFromList();
                    }
                }
            });

            add(button = new JButton(new AbstractAction(">>") {
                public void actionPerformed(ActionEvent e) {
                    addSelectedItems();
                }
            }));
            sp = new JScrollPane(entityList);
            add(sp);
        }
    }


    private boolean isMultiSelect(){
        return entityList != null;
    }


    public void transmitSelectionFromList() {
        if (isMultiSelect()){
            OWLEntity ent = (OWLEntity) entityList.getSelectedObject();
            setTreeSelection(ent);
        }
    }


    public OWLEntity getSelectedObject() {
        if (isMultiSelect()){
            return (OWLEntity) entityList.getSelectedObject();
        }
        else{
            final Set<? extends OWLEntity> sel = getCurrentSelection();
            if (!sel.isEmpty()){
                return sel.iterator().next();
            }
        }
        return null;
    }


    public Set<OWLEntity> getSelectedObjects() {
        if (isMultiSelect()){
            return new HashSet<OWLEntity>(entityList.getListItems());
        }
        else{
            return new HashSet<OWLEntity>(getCurrentSelection());
        }
    }

    public void setSelection(Set<? extends OWLEntity> entities) {
        if (isMultiSelect()){
            entityList.setListData(entities.toArray());
        }
        else if (entities.size() == 1) {
        	setSelection(entities.iterator().next());
        }
    }

    public void setSelection(OWLEntity entity){
        if (isMultiSelect()){
            entityList.setListData(entity != null ? new OWLEntity[]{entity} : new OWLEntity[]{});
        }
        else{
            setTreeSelection(entity);
        }
    }


    private void setTreeSelection(OWLEntity ent) {
        if (ent != null) {
            ent.accept(selectionSetter);
        }
    }


    public void dispose() {
        classSelectorPanel.dispose();
        objectPropertySelectorPanel.dispose();
        dataPropertySelectorPanel.dispose();
        individualSelectorPanel.dispose();
        datatypeSelectorPanel.dispose();
        annotationPropertySelectorPanel.dispose();
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

    public void addSelectionListener(ChangeListener l){
        classSelectorPanel.addSelectionListener(l);
        objectPropertySelectorPanel.addSelectionListener(l);
        dataPropertySelectorPanel.addSelectionListener(l);
        individualSelectorPanel.addSelectionListener(l);
        datatypeSelectorPanel.addSelectionListener(l);
        annotationPropertySelectorPanel.addSelectionListener(l);
        tabbedPane.addChangeListener(l);
    }


    public void removeSelectionListener(ChangeListener l){
        classSelectorPanel.removeSelectionListener(l);
        objectPropertySelectorPanel.removeSelectionListener(l);
        dataPropertySelectorPanel.removeSelectionListener(l);
        individualSelectorPanel.removeSelectionListener(l);
        datatypeSelectorPanel.removeSelectionListener(l);
        annotationPropertySelectorPanel.removeSelectionListener(l);
        tabbedPane.removeChangeListener(l);
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
