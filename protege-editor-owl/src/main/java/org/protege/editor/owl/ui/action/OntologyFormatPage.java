package org.protege.editor.owl.ui.action;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.ui.OntologyFormatPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

import javax.swing.*;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 6, 2009<br><br>
 */
public class OntologyFormatPage extends AbstractWizardPanel {
	private static final long serialVersionUID = -3310342304172021246L;

	public static final String ID = "ONTOLOGY_FORMAT_PANEL";

    private OntologyFormatPanel panel;


    public OntologyFormatPage(EditorKit editorKit) {
        super(ID, "Ontology Format", editorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select the format in which the ontology will be saved (by default).\n\n" +
        		        "Note that the Manchester OWL Syntax does not support all OWL constructs (e.g. GCI's and\n" +
        		        "annotations of undeclared entities) and the Latex format cannot be reloaded");
         
        panel = new OntologyFormatPanel();
        
        parent.add(panel);
    }


    public OWLDocumentFormat getFormat() {
        return panel.getSelectedFormat();
    }


    public Object getBackPanelDescriptor() {
        return PhysicalLocationPanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }
}
