package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
/*
 * Copyright (C) 2008, University of Manchester
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 19-Sep-2008<br><br>
 */
public abstract class MoveAxiomsKitConfigurationPanel extends JComponent {


    private OWLEditorKit editorKit;

    private MoveAxiomsWizard wizard;


    public MoveAxiomsKitConfigurationPanel() {
        
    }


    public void setup(OWLEditorKit editorKit, MoveAxiomsWizard wizard) {
        this.editorKit = editorKit;
        this.wizard = wizard;
    }

    public abstract void initialise();

    public abstract void dispose();

    public OWLEditorKit getEditorKit() {
        return editorKit;
    }

    public String getInstructions() {
        return "";
    }

    public MoveAxiomsWizard getWizard() {
        return wizard;
    }


    public MoveAxiomsModel getModel() {
        return wizard;
    }

    public abstract String getID();

    public abstract String getTitle();

    public abstract void update();

    public abstract void commit();
}
