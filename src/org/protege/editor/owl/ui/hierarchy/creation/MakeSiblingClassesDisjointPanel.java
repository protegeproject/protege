package org.protege.editor.owl.ui.hierarchy.creation;

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
 * Date: 18-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MakeSiblingClassesDisjointPanel extends AbstractOWLWizardPanel {

    public static final String ID = "MakeSiblingClassesDisjointPanel";

    private JCheckBox checkBox;


    public MakeSiblingClassesDisjointPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Make sibling classes disjoint?", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Do you want to make sibling classes disjoint? (Recommended)");
        checkBox = new JCheckBox("Make sibling classes disjoint", true);
        parent.setLayout(new BorderLayout());
        parent.add(checkBox, BorderLayout.NORTH);
    }


    public boolean isMakeSiblingClassesDisjoint() {
        return checkBox.isSelected();
    }


    public void displayingPanel() {
        checkBox.requestFocus();
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    /**
     * Override this class to provide the Object-based identifier of the panel that the
     * user should traverse to when the Back button is pressed. Note that this method
     * is only called when the button is actually pressed, so that the panel can change
     * the previous panel's identifier dynamically at runtime if necessary. Return null if
     * the button should be disabled.
     * @return Object-based identifier
     */
    public Object getBackPanelDescriptor() {
        return TabIndentedHierarchyPanel.ID;
    }
}
