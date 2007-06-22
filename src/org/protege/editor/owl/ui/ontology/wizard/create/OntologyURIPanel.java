package org.protege.editor.owl.ui.ontology.wizard.create;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.OntologyPreferences;
import org.protege.editor.owl.ui.ontology.OntologyPreferencesPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
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
 * Date: 12-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyURIPanel extends AbstractWizardPanel {

    public static final String ID = "ONTOLOGY_URI_PANEL";

    private JTextField textField;


    public OntologyURIPanel(OWLEditorKit editorKit) {
        super("Ontology URI", "Ontology URI", null);
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(isValidURI());
    }


    private boolean isValidURI() {
        try {
            URI uri = new URI(textField.getText());
            return uri.isAbsolute();
        }
        catch (URISyntaxException e) {
            return false;
        }
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the ontology URI.  \n\nThe ontology URI is used to identify" + " the ontology in the context of the world wide web. Additionally, ontologies that" + " import this ontology will use the URI for the import.  It is recommended that you " + " set the ontology URI to be the URL where the ontology will be published.");
        parent.setLayout(new BorderLayout());
        textField = new JTextField(OntologyPreferences.getInstance().generateURI().toString());
        textField.setSelectionStart(textField.getText().lastIndexOf("/") + 1);
        textField.setSelectionEnd(textField.getText().lastIndexOf(".owl"));
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }


            public void insertUpdate(DocumentEvent e) {
                updateState();
            }


            public void removeUpdate(DocumentEvent e) {
                updateState();
            }
        });
        JPanel holderPanel = new JPanel(new BorderLayout(3, 3));
        parent.add(holderPanel, BorderLayout.NORTH);
        holderPanel.add(textField, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton but;
        buttonPanel.add(but = new JButton("Default base..."), BorderLayout.EAST);
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OntologyPreferencesPanel.showDialog(OntologyURIPanel.this);
                textField.setText(OntologyPreferences.getInstance().generateURI().toString());
            }
        });
        holderPanel.add(buttonPanel, BorderLayout.SOUTH);
    }


    public URI getURI() {
        try {
            return new URI(textField.getText());
        }
        catch (URISyntaxException e) {
            return null;
        }
    }


    public Object getNextPanelDescriptor() {
        return PhysicalLocationPanel.ID;
    }


    public void displayingPanel() {
        textField.requestFocus();
        updateState();
    }
}
