package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import org.protege.editor.core.ui.OpenFromURLPanel;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class URLPage extends OntologyImportPage {

    public static final String ID = "URLPage";

    public OpenFromURLPanel urlPanel;

    private boolean displayed = false;


    public URLPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from URL", owlEditorKit);
    }

    @Override
    protected void createUI(JComponent parent) {
        setInstructions("Please specify the URL that points to the file that contains the " + "ontology.  (Please note that this should be the physical URL, rather than the " + "ontology URI)");
        parent.setLayout(new BorderLayout());
        parent.add(urlPanel = new OpenFromURLPanel(){
            protected boolean isValidURI() {
                try {
                    if (super.isValidURI()){
                        getURI().toURL();
                        return true;
                    }
                }
                catch (MalformedURLException e) {
                    // drop through to return false
                }
                return false;
            }
        }, BorderLayout.CENTER);
        parent.add(createCustomizedImportsComponent(), BorderLayout.SOUTH);
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return AnticipateOntologyIdPage.ID;
    }


    public void displayingPanel() {
        urlPanel.requestFocus();
        getWizard().setNextFinishButtonEnabled(urlPanel.isValid());
        if (!displayed){
        	urlPanel.addStatusChangedListener(new InputVerificationStatusChangedListener(){
        		public void verifiedStatusChanged(boolean newState) {
        			getWizard().setNextFinishButtonEnabled(newState);
        		}
        	});
        }
        displayed = true;
    }

    @Override
    public void aboutToHidePanel() {
    	OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
        wizard.setImportsAreFinal(false);
    	wizard.clearImports();
    	ImportInfo parameters = new ImportInfo();
    	parameters.setPhysicalLocation(urlPanel.getURI());
    	wizard.addImport(parameters);
    	((SelectImportLocationPage) getWizardModel().getPanel(SelectImportLocationPage.ID)).setBackPanelDescriptor(ID);
        ((ImportConfirmationPage) getWizardModel().getPanel(ImportConfirmationPage.ID)).setBackPanelDescriptor(ID);
    	super.aboutToHidePanel();
    }

}
