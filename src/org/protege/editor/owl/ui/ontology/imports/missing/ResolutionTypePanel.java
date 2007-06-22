package org.protege.editor.owl.ui.ontology.imports.missing;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

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
 * Date: 17-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ResolutionTypePanel extends AbstractOWLWizardPanel {

    private JRadioButton specifyFileRadioButton;

    private JRadioButton specifyLibraryButton;

    public static final String ID = "ResolutionTypePanel";


    public ResolutionTypePanel(OWLEditorKit owlEditorKit) {
        super(ID, "Missing import resolution type", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please choose what you want to do in order to resolve the missing import");
        ButtonGroup bg = new ButtonGroup();
        Box box = new Box(BoxLayout.Y_AXIS);
        specifyFileRadioButton = new JRadioButton("Specify a file containing the ontology");
        box.add(specifyFileRadioButton);
        bg.add(specifyFileRadioButton);
        box.add(Box.createVerticalStrut(3));
        specifyLibraryButton = new JRadioButton("Add a new ontology library that contains the ontology");
        box.add(specifyLibraryButton);
        bg.add(specifyLibraryButton);
        specifyFileRadioButton.setSelected(true);
        parent.add(box, BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        if (specifyFileRadioButton.isSelected()) {
            return SpecifyFilePathPanel.ID;
        }
        else {
            return WizardPanel.FINISH;
        }
    }
}
