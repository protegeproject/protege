package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportVerificationPage extends AbstractOWLWizardPanel {

    public static final String ID = "ImportVerificationPage";

    private JLabel label;

    private JProgressBar progressBar;

    private Runnable checker;


    public ImportVerificationPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import verification", owlEditorKit);
        checker = new Runnable() {
            public void run() {
                checkImport();
            }
        };
    }


    public Object getNextPanelDescriptor() {
        return ImportConfirmationPage.ID;
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
        ImportVerifier verifier = ((OntologyImportWizard) getWizard()).getImportVerifier();
        final ImportParameters param = verifier.checkImports();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getWizard().setCurrentPanel(ImportConfirmationPage.ID);
            }
        });
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        Thread t = new Thread(checker);
        t.start();
    }
}
