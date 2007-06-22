package org.protege.editor.owl.ui.ontology.wizard.merge;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
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
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SelectOntologiesPage extends AbstractOWLWizardPanel {

    public static final String ID = "SelectOntologiesPage";

    private JList list;


    public SelectOntologiesPage(OWLEditorKit owlEditorKit) {
        super(ID, "Select ontologies to merge", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select the ontologies that you want to merge into another ontology.");
        parent.setLayout(new BorderLayout());
        list = new JList();
        list.setVisibleRowCount(8);
        list.setCellRenderer(getOWLEditorKit().getOWLWorkspace().createOWLCellRenderer());
        list.setListData(getOWLModelManager().getOntologies().toArray());
        parent.add(new JScrollPane(list), BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        return MergeTypePage.ID;
    }


    public void displayingPanel() {
        super.displayingPanel();
        list.requestFocus();
    }


    public Set<OWLOntology> getOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        for (Object o : list.getSelectedValues()) {
            ontologies.add((OWLOntology) o);
        }
        return ontologies;
    }
}
