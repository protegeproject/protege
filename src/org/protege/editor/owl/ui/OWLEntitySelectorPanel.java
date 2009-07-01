package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
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
public class OWLEntitySelectorPanel extends JPanel {

    private OWLEditorKit owlEditorKit;

    private JTabbedPane tabbedPane;

    private OWLModelManagerTree<OWLClass> classTree;

    private OWLModelManagerTree<OWLObjectProperty> objectPropertyTree;

    private OWLModelManagerTree<OWLDataProperty> dataPropertyTree;

    private OWLIndividualListComponent individualsList;

    private Map<JComponent, JComponent> tabbedPaneComponentMap;

    private AssertedInferredHierarchyProvider<OWLClass> classHierarchyProvider;

    private JSplitPane classHierarchySplitPane;


    public OWLEntitySelectorPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;

        tabbedPaneComponentMap = new IdentityHashMap<JComponent, JComponent>();
        createUI();
        owlEditorKit.getWorkspace().getOWLSelectionModel().addListener(new OWLSelectionModelListener() {

            public void selectionChanged() throws Exception {
                updateSelectionFromModel();
            }
        });
        owlEditorKit.getModelManager().addListener(new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
               if(event.isType(EventType.REASONER_CHANGED)) {
                   handleReasonerChanged();
               }
            }
        });
    }

    private void createUI() {
        setLayout(new BorderLayout(7, 7));
        classHierarchySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        classHierarchySplitPane.setBorder(null);
        classHierarchySplitPane.setResizeWeight(0.8);
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(tabbedPane.getFont().deriveFont(Font.PLAIN, 11.0f));
        add(classHierarchySplitPane, BorderLayout.CENTER);
        classHierarchySplitPane.setBottomComponent(tabbedPane);
//        createButtonPanel();
        createClassTree();
        createObjectPropertyTree();
        createDataPropertyTree();
        createIndividualsList();
        tabbedPane.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                handleTabSelectionChange();
            }
        });
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 7, 7));
        JButton but = new JButton("Add class");
        but.setFont(but.getFont().deriveFont(Font.PLAIN, 11.0f));
        buttonPanel.add(but);
        but.setFont(but.getFont().deriveFont(Font.PLAIN, 11.0f));
        buttonPanel.add(but = new JButton("Add property"));
        but.setFont(but.getFont().deriveFont(Font.PLAIN, 11.0f));
        buttonPanel.add(but = new JButton("Add individual"));
        but.setFont(but.getFont().deriveFont(Font.PLAIN, 11.0f));
        add(buttonPanel, BorderLayout.NORTH);
    }


    private void setModelSelection(OWLEntity entity) {
        owlEditorKit.getWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
    }

    private void createClassTree() {
        classHierarchyProvider = new AssertedInferredHierarchyProvider<OWLClass>(
                owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider(),
                owlEditorKit.getModelManager().getOWLHierarchyManager().getInferredOWLClassHierarchyProvider()
        );
        classTree = new OWLModelManagerTree<OWLClass>(owlEditorKit, classHierarchyProvider);
        classTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                setModelSelection(classTree.getSelectedOWLObject());
            }
        });
        classHierarchySplitPane.setTopComponent(new JScrollPane(classTree));
//        tabbedPane.add("Classes", createHierarchyHolder(classTree));
    }

    private void createObjectPropertyTree() {
        objectPropertyTree = new OWLModelManagerTree<OWLObjectProperty>(owlEditorKit, owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider());
        objectPropertyTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                setModelSelection(objectPropertyTree.getSelectedOWLObject());
            }
        });
        tabbedPane.add("Object properties", createHierarchyHolder(objectPropertyTree));
    }

    private void createDataPropertyTree() {
        dataPropertyTree = new OWLModelManagerTree<OWLDataProperty>(owlEditorKit, owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider());
        dataPropertyTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                setModelSelection(dataPropertyTree.getSelectedOWLObject());
            }
        });
        tabbedPane.add("Data properties", createHierarchyHolder(dataPropertyTree));
    }

    private void createIndividualsList() {
        individualsList = new OWLIndividualListComponent(owlEditorKit);
        individualsList.getList().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    setModelSelection(individualsList.getSelectedIndividual());
                }
            }
        });
        tabbedPane.add("Individuals", createHierarchyHolder(individualsList));
    }

    private JComponent createHierarchyHolder(JComponent hierarchy) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.add(new JScrollPane(hierarchy));
        tabbedPaneComponentMap.put(hierarchy, panel);
        return panel;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Selection
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateSelectionFromModel() {
        OWLEntity entity = owlEditorKit.getWorkspace().getOWLSelectionModel().getSelectedEntity();
        setSelection(entity);
    }

    private void handleTabSelectionChange() {
        JComponent selComp = null;
        for(JComponent key : tabbedPaneComponentMap.keySet()) {
            if(tabbedPaneComponentMap.get(key).equals(tabbedPane.getSelectedComponent())) {
                selComp = key;
                break;
            }
        }
        if(selComp == null) {
            return;
        }
        if(selComp.equals(classTree)) {
            setModelSelection(classTree.getSelectedOWLObject());
        }
        else if(selComp.equals(objectPropertyTree)) {
            setModelSelection(objectPropertyTree.getSelectedOWLObject());

        }
        else if(selComp.equals(dataPropertyTree)) {
            setModelSelection(dataPropertyTree.getSelectedOWLObject());

        }
        else if(selComp.equals(individualsList)) {
            setModelSelection(individualsList.getSelectedIndividual());

        }
    }

    public Object getSelection() {

        return null;
    }

    public void setSelection(Object sel) {
        if(sel instanceof OWLClass) {
            if (classTree.getSelectedOWLObject() != null &&
                    !classTree.getSelectedOWLObject().equals(sel)) {
                classTree.setSelectedOWLObject((OWLClass) sel);
                tabbedPane.setSelectedComponent(tabbedPaneComponentMap.get(classTree));
            }
        }
        else if(sel instanceof OWLObjectProperty) {
            objectPropertyTree.setSelectedOWLObject((OWLObjectProperty) sel);
            tabbedPane.setSelectedComponent(tabbedPaneComponentMap.get(objectPropertyTree));
        }
        else if(sel instanceof OWLDataProperty) {
            dataPropertyTree.setSelectedOWLObject((OWLDataProperty) sel);
            tabbedPane.setSelectedComponent(tabbedPaneComponentMap.get(dataPropertyTree));
        }
        else if(sel instanceof OWLIndividual) {
            individualsList.getList().setSelectedValue(sel, true);
            tabbedPane.setSelectedComponent(tabbedPaneComponentMap.get(individualsList));
        }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Reasoner Changed
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    private void handleReasonerChanged() {
//        String name = getOWLModelManager().getOWLReasonerManager().getCurrentReasonerName();
//        if(name.equals("None")) {
//            classHierarchyProvider.swapToAsserted();
//        }
//        else {
//            classHierarchyProvider.swapToInferred();
//        }
    }




    private class AssertedInferredHierarchyProvider<N extends OWLObject> implements OWLObjectHierarchyProvider<N> {

        private OWLObjectHierarchyProvider<N> asserted;

        private OWLObjectHierarchyProvider<N> inferred;

        private OWLObjectHierarchyProvider<N> current;

        private ArrayList<OWLObjectHierarchyProviderListener<N>> listeners;

        public AssertedInferredHierarchyProvider(OWLObjectHierarchyProvider<N> asserted,
                                                 OWLObjectHierarchyProvider<N> inferred) {
            this.asserted = asserted;
            this.inferred = inferred;
            this.current = asserted;
            listeners = new ArrayList<OWLObjectHierarchyProviderListener<N>>();
        }

        public void swapToInferred() {
            if(current != inferred) {
                current = inferred;
                swapListeners(asserted, inferred);
                fireChange();
            }
        }

        public void swapToAsserted() {
            if(current != asserted) {
                current = asserted;
                swapListeners(inferred, asserted);
                fireChange();
            }
        }

        private void swapListeners(OWLObjectHierarchyProvider<N> from, OWLObjectHierarchyProvider<N> to) {
            for(OWLObjectHierarchyProviderListener<N> listener : listeners) {
                from.removeListener(listener);
                to.addListener(listener);
            }
        }



        public void addListener(OWLObjectHierarchyProviderListener<N> listener) {
            current.addListener(listener);
            listeners.add(listener);
        }


        public boolean containsReference(N object) {
            return current.containsReference(object);
        }


        public void dispose() {
        }


        public Set<N> getAncestors(N object) {
            return current.getAncestors(object);
        }


        public Set<N> getChildren(N object) {
            return current.getChildren(object);
        }


        public Set<N> getDescendants(N object) {
            return current.getDescendants(object);
        }


        public Set<N> getEquivalents(N object) {
            return current.getEquivalents(object);
        }


        public Set<N> getParents(N object) {
            return current.getParents(object);
        }


        public Set getPathsToRoot(N object) {
            return current.getPathsToRoot(object);
        }


        public Set<N> getRoots() {
            return current.getRoots();
        }


        public void removeListener(OWLObjectHierarchyProviderListener<N> listener) {
            current.removeListener(listener);
            listeners.remove(listener);
        }


        public void setOntologies(Set<OWLOntology> set) {
            asserted.setOntologies(set);
            inferred.setOntologies(set);
        }

        private void fireChange() {
            for(OWLObjectHierarchyProviderListener<N> listener : listeners) {
                listener.hierarchyChanged();
            }
        }
    }

}
