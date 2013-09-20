package org.protege.editor.owl.ui.ontology.wizard.move.byreference;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.common.SignatureSelection;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;

import java.awt.*;
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
 * 23-Sep-2008<br><br>
 */
public class SelectSignaturePanel extends MoveAxiomsKitConfigurationPanel {

    private OWLEntitySelectorPanel selector;

    private SignatureSelection kit;


    public SelectSignaturePanel(SignatureSelection kit) {
        this.kit = kit;
    }


    public void initialise() {
        setLayout(new BorderLayout());
        selector = new OWLEntitySelectorPanel(getEditorKit());
        add(selector, BorderLayout.CENTER);
    }


    public void dispose() {
        selector.dispose();
    }


    public String getID() {
        return "Signature panel";
    }


    public String getTitle() {
        return "Select a signature";
    }


    public String getInstructions() {
        return "Select entities in that should be in the signature for the copy/move/delete operation.";
    }


    public void update() {
    }


    public void commit() {
        kit.setSignature(selector.getSelectedObjects());
    }
}
