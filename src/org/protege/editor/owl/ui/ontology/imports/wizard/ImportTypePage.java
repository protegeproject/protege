package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;
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
public class ImportTypePage extends AbstractWizardPanel {

    public static final String ID = "ImportTypePage";

    private JRadioButton webRadioButton;

    private JRadioButton localFileRadioButton;

    private JRadioButton libraryRadioButton;

    private JRadioButton loadedOntologyButton;


    public ImportTypePage(OWLEditorKit owlEditorKit) {
        super(ID, "Import type", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please choose an option:");
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(libraryRadioButton = new JRadioButton(
                "Import an ontology that is contained in one of the ontology libraries."));
        box.add(loadedOntologyButton = new JRadioButton("Import an ontology that has already been loaded/created."));
        box.add(webRadioButton = new JRadioButton("Import an ontology contained in a document located on the web."));
        box.add(localFileRadioButton = new JRadioButton("Import an ontology contained in a specific file."));
        parent.add(box, BorderLayout.NORTH);
        ButtonGroup bg = new ButtonGroup();
        bg.add(webRadioButton);
        bg.add(localFileRadioButton);
        bg.add(libraryRadioButton);
        bg.add(loadedOntologyButton);
        libraryRadioButton.setSelected(true);
    }


    public Object getNextPanelDescriptor() {
        if (webRadioButton.isSelected()) {
            return URLPage.ID;
        }
        else if (localFileRadioButton.isSelected()) {
            return LocalFilePage.ID;
        }
        else if (libraryRadioButton.isSelected()) {
            return LibraryPage.ID;
        }
        else {
            return LoadedOntologyPage.ID;
        }
    }


    public Object getBackPanelDescriptor() {
        return super.getBackPanelDescriptor();
    }


    public static void main(String[] args) {
        Wizard w = new Wizard();
    }
}
