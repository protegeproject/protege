package org.protege.editor.owl.ui.ontology.wizard.move;

/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */
import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;


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
