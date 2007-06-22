package org.protege.editor.owl.ui.ontology.imports.missing;

import org.protege.editor.core.ui.util.FilePathPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashSet;
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
public class SpecifyFilePathPanel extends AbstractOWLWizardPanel {

    public static final String ID = "SpecifyFilePathPanel";

    private FilePathPanel filePathPanel;


    public SpecifyFilePathPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Specify file", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please specify the path to the file containing the ontology");
        filePathPanel = new FilePathPanel("Specify ontology file", new HashSet<String>());
        parent.add(filePathPanel, BorderLayout.NORTH);
        filePathPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateState();
            }
        });
    }


    public void displayingPanel() {
        super.displayingPanel();
        filePathPanel.requestFocus();
        updateState();
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(filePathPanel.getFile() != null && filePathPanel.getFile().exists());
    }


    public Object getBackPanelDescriptor() {
        return ResolutionTypePanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return CopyOptionPanel.ID;
    }
}
