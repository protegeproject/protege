package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.Frame;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.AnticipateOntologyIdPage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.ImportConfirmationPage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.ImportTypePage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.LibraryPage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.LoadedOntologyPage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.LocalFilePage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.SelectImportLocationPage;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.URLPage;


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
    private Set<ImportInfo> imports = new HashSet<ImportInfo>();
    private boolean customizeImports = false;
    
    public OntologyImportWizard(Frame owner, OWLEditorKit owlEditorKit) {
        super(owner);
        setTitle("Import ontology wizard");
        registerWizardPanel(ImportTypePage.ID, new ImportTypePage(owlEditorKit));
        registerWizardPanel(LocalFilePage.ID, new LocalFilePage(owlEditorKit));
        registerWizardPanel(URLPage.ID, new URLPage(owlEditorKit));
        registerWizardPanel(LoadedOntologyPage.ID, new LoadedOntologyPage(owlEditorKit));
        registerWizardPanel(LibraryPage.ID, new LibraryPage(owlEditorKit));
        registerWizardPanel(AnticipateOntologyIdPage.ID, new AnticipateOntologyIdPage(owlEditorKit));
        registerWizardPanel(SelectImportLocationPage.ID, new SelectImportLocationPage(owlEditorKit));
        registerWizardPanel(ImportConfirmationPage.ID, new ImportConfirmationPage(owlEditorKit));
        setCurrentPanel(ImportTypePage.ID);
    }
    
    public void clearImports() {
    	imports.clear();
    }
    
    public void addImport(ImportInfo parameters) {
    	imports.add(parameters);
	}
    
    public void removeImport(ImportInfo parameters) {
    	imports.remove(parameters);
    }
    
    public Set<ImportInfo> getImports() {
    	return Collections.unmodifiableSet(imports);
    }
    
    public boolean isCustomizeImports() {
        return customizeImports;
    }
    
    public void setCustomizeImports(boolean customizeImports) {
        this.customizeImports = customizeImports;
    }

}
