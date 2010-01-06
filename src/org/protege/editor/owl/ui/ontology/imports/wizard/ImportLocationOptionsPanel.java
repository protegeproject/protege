package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class ImportLocationOptionsPanel extends JPanel {
	
	private ImportInfo info;
	private JRadioButton ontologyIDButton;
	private JRadioButton versionIDButton;
	private JRadioButton physicalIDButton;
	private JRadioButton userInputButton;
	private JTextField uriField;
	private int optionsCount;
	
	
	public ImportLocationOptionsPanel(ImportInfo info) {
		this.info = info;
		OWLOntologyID id = info.getOntologyID();
		URI physicalLocation = info.getPhysicalLocation();
		ButtonGroup bg = new ButtonGroup();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		optionsCount = 0;
		
		if (id != null && !id.isAnonymous()) {
		    ontologyIDButton = new JRadioButton("Import using the ontology name: " + id.getOntologyIRI());
		    ontologyIDButton.setAlignmentX(LEFT_ALIGNMENT);
		    add(ontologyIDButton);
		    bg.add(ontologyIDButton);
		    optionsCount++;
		}

    	boolean useVersionButton = (id.getVersionIRI() != null && !id.getVersionIRI().equals(id.getOntologyIRI()));
    	if (useVersionButton) {
    		versionIDButton = new JRadioButton("Import using the ontology version (Recommended): " + id.getVersionIRI());
    		versionIDButton.setAlignmentX(LEFT_ALIGNMENT);
    		add(versionIDButton);
    		bg.add(versionIDButton);
    		optionsCount++;
    	}
    	if (!physicalLocation.equals(id.getOntologyIRI().toURI()) &&
    			(id.getVersionIRI() == null || !physicalLocation.equals(id.getVersionIRI().toURI())) &&
    			!physicalLocation.getScheme().equals("file")) {
    		physicalIDButton = new JRadioButton("Import using the supplied physical URI (Not Recommended): " + physicalLocation);
    		physicalIDButton.setAlignmentX(LEFT_ALIGNMENT);
    		add(physicalIDButton);
    		bg.add(physicalIDButton);
    		optionsCount++;
    	}
    	if (optionsCount <= 1) {
    	    userInputButton = new JRadioButton("Import using the usr supplied URI (Discouraged)");
    	    userInputButton.setAlignmentX(LEFT_ALIGNMENT);
    	    add(userInputButton);
    	    bg.add(userInputButton);
    	    uriField = new JTextField();
    	    uriField.setAlignmentX(LEFT_ALIGNMENT);
    	    uriField.setEnabled(false);
    	    userInputButton.addActionListener(new ActionListener() {
    	       
    	        public void actionPerformed(ActionEvent e) {
    	            uriField.setEnabled(userInputButton.isSelected());
    	        }
    	    });
    	    add(uriField);
    	}

    	if (versionIDButton != null) {
    	    versionIDButton.setSelected(true);
    	}
    	else if (ontologyIDButton != null) {
    	    ontologyIDButton.setSelected(true);
    	}
    	else if (physicalIDButton != null) {
    	    physicalIDButton.setSelected(true);
    	}
    	else {
    	    userInputButton.setSelected(true);
    	    uriField.setEnabled(true);
    	}
	}
	
	public void setImportLocation() {    	
		OWLOntologyID id = info.getOntologyID();
		URI physicalLocation = info.getPhysicalLocation();
    	if (ontologyIDButton != null && ontologyIDButton.isSelected()) {
    		info.setImportLocation(id.getOntologyIRI());
    	}
    	else if (versionIDButton != null && versionIDButton.isSelected()) {
    		info.setImportLocation(id.getVersionIRI());
    	}
    	else {
    		info.setImportLocation(IRI.create(physicalLocation));
    	}
	}
	
	public boolean isPanelNeeded() {
		return optionsCount > 1;
	}
}
