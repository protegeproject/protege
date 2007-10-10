package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.Frame;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
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
public class OntologyImportWizard extends Wizard {

    private static final Logger logger = Logger.getLogger(OntologyImportWizard.class);

    private ImportVerifier importVerifier;


    public OntologyImportWizard(Frame owner, OWLEditorKit owlEditorKit) {
        super(owner);
        setTitle("Import ontology wizard");
        registerWizardPanel(ImportTypePage.ID, new ImportTypePage(owlEditorKit));
        registerWizardPanel(LocalFilePage.ID, new LocalFilePage(owlEditorKit));
        registerWizardPanel(URLPage.ID, new URLPage(owlEditorKit));
        registerWizardPanel(LoadedOntologyPage.ID, new LoadedOntologyPage(owlEditorKit));
        registerWizardPanel(LibraryPage.ID, new LibraryPage(owlEditorKit));
        registerWizardPanel(ImportVerificationPage.ID, new ImportVerificationPage(owlEditorKit));
        registerWizardPanel(ImportConfirmationPage.ID, new ImportConfirmationPage(owlEditorKit));
        setCurrentPanel(ImportTypePage.ID);
    }


    protected void setImportVerifier(ImportVerifier importVerifier) {
        this.importVerifier = importVerifier;
    }


    public ImportVerifier getImportVerifier() {
        return importVerifier;
    }
}
