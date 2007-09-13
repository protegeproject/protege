package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collections;
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
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LoadedOntologyPage extends AbstractImportSourcePage {

    private static final Logger logger = Logger.getLogger(LoadedOntologyPage.class);


    public static final String ID = LoadedOntologyPage.class.getName();

    private JList ontologyList;


    public LoadedOntologyPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import pre-loaded ontology", owlEditorKit);
    }


    private Set<OWLOntology> getOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        ontologies.addAll(getOWLModelManager().getOntologies());
        ontologies.removeAll(getOWLModelManager().getActiveOntologies());
        return ontologies;
    }


    public ImportVerifier getImportVerifier() {
        return new LoadedOntologyImportVerifier(Collections.singleton((OWLOntology) ontologyList.getSelectedValue()));
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select an existing (pre-loaded) ontology that you want to import.");
        ontologyList = new OWLObjectList(getOWLEditorKit());
        ontologyList.setCellRenderer(new OWLOntologyCellRenderer(getOWLEditorKit()));
        ontologyList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateState();
                }
            }
        });
        parent.setLayout(new BorderLayout());
        parent.add(ComponentFactory.createScrollPane(ontologyList));
    }


    private void fillList() {
        ontologyList.setListData(getOntologies().toArray());
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public void displayingPanel() {
        fillList();
        updateState();
        ontologyList.requestFocus();
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(ontologyList.getSelectedValue() != null);
    }
}

