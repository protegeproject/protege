package org.protege.editor.owl.ui.action.export.inferred;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;
import java.awt.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Aug-2007<br><br>
 */
public class ExportInferredOntologyIncludeAssertedAxiomsPanel extends AbstractOWLWizardPanel {

    private JCheckBox includeAnnotationsCheckBox;

    private JCheckBox includeAssertedLogicalAxiomsCheckBox;

    public static final String ID = "ID";


    public ExportInferredOntologyIncludeAssertedAxiomsPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Include asserted axioms", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please decide which asserted information you wish to include in the export.  If you choose " + "to include asserted logical axioms then all logical axioms in the ontology will be exported as well " + "as the inferred logical axioms that were selected on the previous page.");
        includeAnnotationsCheckBox = new JCheckBox("Include annotations");
        includeAssertedLogicalAxiomsCheckBox = new JCheckBox("Include asserted logical axioms");
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(includeAnnotationsCheckBox);
        box.add(Box.createVerticalStrut(4));
        box.add(includeAssertedLogicalAxiomsCheckBox);
        parent.add(box, BorderLayout.NORTH);
    }


    public boolean isIncludeAnnotationAxioms() {
        return includeAnnotationsCheckBox.isSelected();
    }


    public boolean isIncludeAssertedLogicalAxioms() {
        return includeAssertedLogicalAxiomsCheckBox.isSelected();
    }


    public Object getNextPanelDescriptor() {
        return ExportInferredOntologyIDPanel.ID;
    }


    public Object getBackPanelDescriptor() {
        return ExportInferredOntologyWizardSelectAxiomsPanel.ID;
    }
}
