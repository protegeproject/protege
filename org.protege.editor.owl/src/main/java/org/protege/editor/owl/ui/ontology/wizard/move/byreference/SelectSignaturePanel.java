package org.protege.editor.owl.ui.ontology.wizard.move.byreference;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.ontology.wizard.move.common.SignatureSelection;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;

import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
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
