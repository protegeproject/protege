package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportConfirmationPage extends AbstractOWLWizardPanel {

    private static final Logger logger = Logger.getLogger(ImportConfirmationPage.class);


    public static final String ID = "ImportConfirmationPage";

    private JComponent importedOntologiesComponent;
    private TitledBorder titledBorder;

    private Object backPanelDescriptor;

    public ImportConfirmationPage(OWLEditorKit owlEditorKit) {
        super(ID, "Confirm imports", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("The system will import the following ontologies.  Press Finish " + "to import these ontologies, or Cancel to exit the wizard without importing " + "any ontologies.");
        importedOntologiesComponent = new JPanel(new BorderLayout());
        parent.setLayout(new BorderLayout());
        parent.add(importedOntologiesComponent, BorderLayout.NORTH);
    }


    public void displayingPanel() {
        super.displayingPanel();
        fillImportList();
    }


    private void fillImportList() {
        importedOntologiesComponent.removeAll();
        Box box = new Box(BoxLayout.Y_AXIS);
        boolean advanced = ((OntologyImportWizard) getWizard()).isCustomizeImports();
        Set<ImportInfo> parameters = ((OntologyImportWizard) getWizard()).getImports();
        for (ImportInfo parameter : parameters) {
        	if (parameter.isReady()) {
        	    if (advanced) {
        	        ImportEntryPanel importPanel = new ImportEntryPanel(parameter);
        	        box.add(importPanel);
        	    }
        	    else {
        	        box.add(new JLabel(parameter.getPhysicalLocation().toString()));
        	    }
        	}
        }
        importedOntologiesComponent.add(box, BorderLayout.NORTH);
    }

    public void setBackPanelDescriptor(Object backPanelDescriptor) {
        this.backPanelDescriptor = backPanelDescriptor;
    }
    
    @Override
    public Object getBackPanelDescriptor() {
        if (((OntologyImportWizard) getWizard()).isCustomizeImports()) {
            return SelectImportLocationPage.ID;
        }
        else {
            return backPanelDescriptor;
        }
    }
    
    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    private class ImportEntryPanel extends JPanel {

        public ImportEntryPanel(ImportInfo parameter) {
            setBorder(BorderFactory.createEmptyBorder(1, 0, 4, 0));
            setLayout(new BorderLayout(1, 1));
            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            
            JLabel physicalLocationLabel = new JLabel("Load import from " + parameter.getPhysicalLocation().toString());
            physicalLocationLabel.setFont(physicalLocationLabel.getFont().deriveFont(10.0f));
            physicalLocationLabel.setAlignmentX(LEFT_ALIGNMENT);
            center.add(physicalLocationLabel);
            
            JLabel ontologyNameLabel = new JLabel("Imported Ontology Name " + parameter.getOntologyID().getOntologyIRI().toString());
            ontologyNameLabel.setAlignmentX(LEFT_ALIGNMENT);
            center.add(ontologyNameLabel);
            
            if (parameter.getOntologyID().getVersionIRI() != null) {
            	JLabel ontologyVersionLabel = new JLabel("Imported Ontology Version " + parameter.getOntologyID().getVersionIRI());
            	ontologyVersionLabel.setAlignmentX(LEFT_ALIGNMENT);
            	center.add(ontologyVersionLabel);
            }
            
            center.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
            Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
            titledBorder = BorderFactory.createTitledBorder(lineBorder, "Import Declaration: " + parameter.getImportLocation().toString());
            setBorder(titledBorder);
            add(center, BorderLayout.CENTER);
        }
    }
}
