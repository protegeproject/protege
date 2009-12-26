package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owlapi.model.OWLOntologyID;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnticipateOntologyIdPage extends AbstractOWLWizardPanel {

    public static final String ID = "AnticipateOntologyIdPage";

    private JLabel label;

    private JProgressBar progressBar;

    private Runnable checker;
    
    private Set<ImportInfo> failedImports = new HashSet<ImportInfo>();


    public AnticipateOntologyIdPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import verification", owlEditorKit);
        checker = new Runnable() {
            public void run() {
                checkImport();
            }
        };
    }


    public Object getNextPanelDescriptor() {
        return needsImportPage() ? SelectImportLocationPage.ID : ImportConfirmationPage.ID;
    }

    private boolean needsImportPage() {
    	OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
    	Set<ImportInfo> imports = wizard.getImports();
    	if (imports == null || imports.size() != 1) {
    		return false;
    	}
    	ImportInfo parameters = imports.iterator().next();
    	OWLOntologyID id = parameters.getOntologyID();
    	if (id == null) {
    		return true;
    	}
    	else if (parameters.getImportLocation() != null) {
    		return false;
    	}
    	else if (id.getVersionIRI() != null &&	 !id.getVersionIRI().equals(id.getOntologyIRI())) {
    		return true;
    	}
    	else if (parameters.getPhysicalLocation().equals(id.getOntologyIRI().toURI()) 
    				|| parameters.getPhysicalLocation().getScheme().equals("file")){
    		parameters.setImportLocation(id.getOntologyIRI());
    		return false;
    	}
    	else {
    		return true;
    	}
    }

    protected void createUI(final JComponent parent) {
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        panel.add(label = new JLabel("Please wait.  Verifying import..."), BorderLayout.NORTH);
        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);
        parent.setLayout(new BorderLayout());
        parent.add(panel, BorderLayout.NORTH);
    }


    protected void checkImport() {
    	for (ImportInfo parameters : ((OntologyImportWizard) getWizard()).getImports()) {
    		if (parameters.getOntologyID() != null) {
    			continue;
    		}
    		boolean failed = false;
    		try {
    			MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor(parameters.getPhysicalLocation());
    			OWLOntologyID id = extractor.getOntologyId();
    			if (id != null) {
    				parameters.setOntologyID(id);
    			}
    			else {
    				failed = true;
    			}
    		}
    		catch (Throwable t) {
    			failed = true;
    			ProtegeApplication.getErrorLog().logError(t);
    		}
    		if (failed) {
    			failedImports.add(parameters);
    		}
    	}
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getWizard().setCurrentPanel(getNextPanelDescriptor());
            }
        });
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        Thread t = new Thread(checker);
        t.start();
    }
}
