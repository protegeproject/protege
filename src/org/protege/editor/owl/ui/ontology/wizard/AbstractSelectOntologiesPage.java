package org.protege.editor.owl.ui.ontology.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.merge.MergeTypePage;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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
                if (defOnts.contains((OWLOntology)list.getModel().getElementAt(i))){
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
