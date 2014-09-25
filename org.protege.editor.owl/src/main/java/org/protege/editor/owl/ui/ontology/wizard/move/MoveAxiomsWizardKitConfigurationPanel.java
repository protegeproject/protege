package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 19-Sep-2008<br><br>
 */
public class MoveAxiomsWizardKitConfigurationPanel extends AbstractMoveAxiomsWizardPanel {

    private MoveAxiomsKitConfigurationPanel content;

    private Object prevId;

    private Object nextId;

    private JPanel holder;

    
    public MoveAxiomsWizardKitConfigurationPanel(Object prevId, Object nextId, MoveAxiomsKitConfigurationPanel content, OWLEditorKit owlEditorKit) {
        super(content.getID(), content.getTitle(), owlEditorKit);
        this.content = content;
        this.prevId = prevId;
        this.nextId = nextId;
        holder.add(content);
        setInstructions(content.getInstructions());
    }

    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        parent.add(holder = new JPanel(new BorderLayout()));
    }


    public void aboutToDisplayPanel() {
        super.aboutToDisplayPanel();
        content.update();
        setComponentTransparency(content);

    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        content.commit();
    }


    public Object getBackPanelDescriptor() {
        return prevId;
    }


    public Object getNextPanelDescriptor() {
        return nextId;
    }
}
