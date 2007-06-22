package org.protege.editor.owl.ui.ontology.imports.missing;

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
public class CopyOptionPanel extends AbstractOWLWizardPanel {

    public static final String ID = "CopyOptionPanel";

    private JCheckBox copyCheckBox;


    public CopyOptionPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Copy file to root ontology folder?", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Would you like to copy the file to to root ontology folder? " + "Ontologies are only editable if they are loaded from the same folder that the root importing " + "ontology was loaded from.");

        copyCheckBox = new JCheckBox("Copy to imports root folder", true);
        parent.setLayout(new BorderLayout());
        parent.add(copyCheckBox, BorderLayout.NORTH);
    }


    public Object getBackPanelDescriptor() {
        return SpecifyFilePathPanel.ID;
    }
}
