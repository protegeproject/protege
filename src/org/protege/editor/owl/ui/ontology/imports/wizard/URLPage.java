package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.core.ui.OpenFromURIPanel;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.OWLEditorKit;

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
public class URLPage extends AbstractImportSourcePage {

    public static final String ID = "URLPage";

    public OpenFromURIPanel urlPanel;


    public URLPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from URL", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the URL that points to the file that contains the " + "ontology.  (Please note that this should be the physical URL, rather than the " + "ontology URI)");
        parent.setLayout(new BorderLayout());
        parent.add(urlPanel = new OpenFromURIPanel(){
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
        });
        urlPanel.addStatusChangedListener(new InputVerificationStatusChangedListener(){
            public void verifiedStatusChanged(boolean newState) {
                getWizard().setNextFinishButtonEnabled(newState);
            }
        });
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        urlPanel.requestFocus();
    }


    public ImportVerifier getImportVerifier() {
        try {
            return new URLImportFileVerifier(urlPanel.getURI().toURL());
        }
        catch (MalformedURLException e) {
            return null;
        }
    }
}
