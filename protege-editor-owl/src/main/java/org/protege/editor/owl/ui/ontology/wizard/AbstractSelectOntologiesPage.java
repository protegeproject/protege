package org.protege.editor.owl.ui.ontology.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.wizard.merge.MergeTypePage;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Sep-2008<br><br>
 */
public class AbstractSelectOntologiesPage extends AbstractOWLWizardPanel {

    private JList list;

    public AbstractSelectOntologiesPage(Object ID, OWLEditorKit owlEditorKit, String title) {
        super(ID, title, owlEditorKit);
    }

    final protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        list = new JList();
        list.setVisibleRowCount(8);
        list.setCellRenderer(getOWLEditorKit().getWorkspace().createOWLCellRenderer());
        final java.util.List<OWLOntology> orderedOntologies = new ArrayList<OWLOntology>(getOWLModelManager().getOntologies());
        Collections.sort(orderedOntologies, getOWLModelManager().getOWLObjectComparator());
        list.setListData(orderedOntologies.toArray());
        parent.add(new JScrollPane(list), BorderLayout.NORTH);
        updateSelectionMode();

        list.addListSelectionListener(event -> {
            handleSelectionChanged();
        });
    }


    private void handleSelectionChanged() {
        getWizard().setNextFinishButtonEnabled(!getOntologies().isEmpty());
    }


    private void updateSelectionMode() {
        if (isMultiSelect()) {
            list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        else {
            list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }


    protected boolean isMultiSelect() {
        return true;
    }


    public void aboutToDisplayPanel() {
        super.aboutToDisplayPanel();
        updateSelectionMode();
    }


    public Object getNextPanelDescriptor() {
        return MergeTypePage.ID;
    }


    public void displayingPanel() {
        super.displayingPanel();
        if (list.getSelectedValue() == null){
            Set<OWLOntology> defOnts = getDefaultOntologies();
            for (int i=0; i<list.getModel().getSize(); i++){
                if (defOnts.contains(list.getModel().getElementAt(i))){
                    list.addSelectionInterval(i, i);
                }
            }
        }
        list.requestFocus();
    }

    /**
     * Override to set the ontologies that are first shown
     * @return the set of ontologies that should be selected the first
     * time this page is shown
     */
    protected Set<OWLOntology> getDefaultOntologies() {
        return Collections.singleton(getOWLModelManager().getActiveOntology());
    }


    public Set<OWLOntology> getOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        for (Object o : list.getSelectedValues()) {
            ontologies.add((OWLOntology) o);
        }
        return ontologies;
    }

}
