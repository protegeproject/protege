package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
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
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportConfirmationPage extends AbstractOWLWizardPanel {

    private static final Logger logger = Logger.getLogger(ImportConfirmationPage.class);


    public static final String ID = "ImportConfirmationPage";

    private JComponent importedOntologiesComponent;


    public ImportConfirmationPage(OWLEditorKit owlEditorKit) {
        super(ID, "Confirm imports", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("The system will import the following ontologies.  Press Finish " + "to import these ontologies, or Cancel to exit the wizard without importing " + "any ontologies.");
        importedOntologiesComponent = new JPanel(new BorderLayout());
        parent.setLayout(new BorderLayout());
        parent.add(importedOntologiesComponent, BorderLayout.NORTH);
    }


    public void displayingPanel() {
        super.displayingPanel();
        fillImportList();
    }


    private void fillImportList() {
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                                                         BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        ImportVerifier verifier = ((OntologyImportWizard) getWizard()).getImportVerifier();
        ImportParameters param = verifier.checkImports();
        for (URI uri : param.getOntologiesToBeImported()) {
            box.add(new ImportEntryPanel(uri, param.getOntologyLocationDescription(uri)));
        }

        importedOntologiesComponent.add(box, BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    private class ImportEntryPanel extends JPanel {

        public ImportEntryPanel(URI ontologyURI, String locationDescription) {
            setBorder(BorderFactory.createEmptyBorder(1, 0, 4, 0));
            setLayout(new BorderLayout(1, 1));
            setBackground(Color.WHITE);
            JLabel ontologyURILabel = new JLabel(ontologyURI.toString());
            add(ontologyURILabel, BorderLayout.NORTH);
            JLabel physicalLocationLabel = new JLabel(locationDescription);
            add(physicalLocationLabel, BorderLayout.SOUTH);
            physicalLocationLabel.setForeground(Color.GRAY);
            physicalLocationLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
            physicalLocationLabel.setFont(physicalLocationLabel.getFont().deriveFont(10.0f));
        }
    }
}
