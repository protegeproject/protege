package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportLocationOptionsPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class SelectImportLocationPage extends AbstractWizardPanel {
	public static final String ID = "SelectImportLocationPage";

	private Box mainBox;
    private Collection<ImportLocationOptionsPanel> optionsPanels = new ArrayList<ImportLocationOptionsPanel>();
    private Object backPanelDescriptor;
	
    public SelectImportLocationPage(OWLEditorKit owlEditorKit) {
        super(ID, "Select URI In Import Statement", owlEditorKit);
    }

    @Override
    protected void createUI(JComponent parent) {
    	setInstructions("Please choose a value for the imported location:");

    	parent.setLayout(new BorderLayout());
    	mainBox = new Box(BoxLayout.Y_AXIS);
    	parent.add(mainBox, BorderLayout.CENTER);
    }

    @Override
    public void aboutToDisplayPanel() {
    	mainBox.removeAll();
    	optionsPanels.clear();
    	Set<ImportInfo> parameters = ((OntologyImportWizard) getWizard()).getImports();
    	for (ImportInfo parameter : parameters) {
    		OWLOntologyID id = parameter.getOntologyID();
    		if (id == null) {
    			continue;
    		}
    		ImportLocationOptionsPanel optionsPanel = new ImportLocationOptionsPanel(parameter);
    		if (optionsPanel.isPanelNeeded()) {
    			Border lineBorder = BorderFactory.createLineBorder(Color.GRAY);
    			Border titledBorder = BorderFactory.createTitledBorder(lineBorder, "Physical Location: " + parameter.getPhysicalLocation().toString());
    			optionsPanel.setBorder(titledBorder);
    			mainBox.add(optionsPanel);
    			optionsPanels.add(optionsPanel);
    		}
    		else {
    			optionsPanel.setImportLocation();
    		}
    	}
    }
    
    @Override
    public void displayingPanel() {
    	if (optionsPanels.size() == 0) {
    		getWizard().setCurrentPanel(getNextPanelDescriptor());
    	}
    }
    
    @Override
    public void aboutToHidePanel() {
    	for (ImportLocationOptionsPanel optionsPanel : optionsPanels) {
    		optionsPanel.setImportLocation();
    	}
    }
    
    public void setBackPanelDescriptor(Object backPanelDescriptor) {
		this.backPanelDescriptor = backPanelDescriptor;
	}
    
    @Override
    public Object getBackPanelDescriptor() {
    	return backPanelDescriptor;
    }
    
    public Object getNextPanelDescriptor() {
        return ImportConfirmationPage.ID;
    }
}
