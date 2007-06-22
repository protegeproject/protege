package org.protege.editor.owl.ui.ontology.wizard.merge;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyURIPanel;

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
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergeTypePage extends AbstractOWLWizardPanel {

    private JRadioButton mergeIntoExisting;

    private JRadioButton mergeIntoNew;

    public static final String ID = "MergeTypePage";


    public MergeTypePage(OWLEditorKit owlEditorKit) {
        super(ID, "Select merge type", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select how you would like to merge the ontologies that you have selected. " + "The ontologies can be merged into a brand new ontology, or they can be merged into an existing " + "ontology.");
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        parent.add(box, BorderLayout.NORTH);
        mergeIntoNew = new JRadioButton("Merge into new ontology", true);
        box.add(mergeIntoNew);
        mergeIntoExisting = new JRadioButton("Merge into existing ontology");
        box.add(mergeIntoExisting);
        ButtonGroup bg = new ButtonGroup();
        bg.add(mergeIntoNew);
        bg.add(mergeIntoExisting);
    }


    public void displayingPanel() {
        super.displayingPanel();
        mergeIntoNew.requestFocus();
    }


    public Object getNextPanelDescriptor() {
        if (mergeIntoNew.isSelected()) {
            return OntologyURIPanel.ID;
        }
        else {
            return WizardPanel.FINISH;
        }
    }
}
