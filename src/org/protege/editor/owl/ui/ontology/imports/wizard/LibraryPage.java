package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.library.OntologyLibraryPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
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
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LibraryPage extends AbstractImportSourcePage {

    private static final Logger logger = Logger.getLogger(LibraryPage.class);


    public static final String ID = "LibraryPage";

    private LibraryOntologiesList list;


    public LibraryPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from ontology library", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("The list below shows ontologies that are contained in the available ontology " + "libraries.  To add or remove an ontology library, click the \"Ontology libraries...\" button.");
        parent.setLayout(new BorderLayout());
        list = new LibraryOntologiesList(getOWLModelManager());
        JPanel listPanel = new JPanel(new BorderLayout(7, 7));
        listPanel.add(ComponentFactory.createScrollPane(list), BorderLayout.NORTH);
        listPanel.add(new JButton(new AbstractAction("Ontology libraries...") {
            public void actionPerformed(ActionEvent e) {
                OntologyLibraryPanel.showDialog(getOWLEditorKit());
                // Rebuild the list
                list.rebuildList();
            }
        }), BorderLayout.EAST);
        parent.add(listPanel, BorderLayout.NORTH);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    getWizard().setNextFinishButtonEnabled(list.getSelectedIndex() != -1);
                }
            }
        });
    }


    public ImportVerifier getImportVerifier() {
        Set<URI> selectedURIs = new HashSet<URI>();
        Object [] selObjs = list.getSelectedValues();
        for (Object o : selObjs) {
            selectedURIs.add((URI) o);
        }
        return new LibraryVerifier(getOWLModelManager(), selectedURIs);
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        list.rebuildList();
        list.requestFocus();
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
    }
}
