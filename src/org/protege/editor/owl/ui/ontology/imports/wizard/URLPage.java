package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.BorderLayout;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ui.util.URLPanel;
import org.protege.editor.owl.OWLEditorKit;


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

    public URLPanel urlPanel;


    public URLPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from URL", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the URL that points to the file that contains the " + "ontology.  (Please note that this should be the physical URL, rather than the " + "ontology URI)");
        parent.setLayout(new BorderLayout());
        parent.add(urlPanel = new URLPanel());
        urlPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                URL url = urlPanel.getURL();
                if (url == null) {
                    getWizard().setNextFinishButtonEnabled(false);
                    return;
                }
                try {
                    URI uri = url.toURI();

                    getWizard().setNextFinishButtonEnabled(uri.isAbsolute() && uri.getScheme() != null && uri.getPath() != null);
                }
                catch (URISyntaxException e1) {
                    getWizard().setNextFinishButtonEnabled(false);
                    return;
                }
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
        return new URLImportFileVerifier(urlPanel.getURL());
    }
}
