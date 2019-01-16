package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.imports.wizard.page.*;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyImportWizard extends Wizard {

    private Set<ImportInfo> imports = new HashSet<>();
    private boolean customizeImports = false;
    private boolean importsAreFinal = false;
    
    public OntologyImportWizard(Frame owner, OWLEditorKit owlEditorKit) {
        super(owner);
        setTitle("Import ontology wizard");
        registerWizardPanel(ImportTypePage.ID, new ImportTypePage(owlEditorKit));
        registerWizardPanel(LocalFilePage.ID, new LocalFilePage(owlEditorKit));
        registerWizardPanel(URLPage.ID, new URLPage(owlEditorKit));
        registerWizardPanel(LoadedOntologyPage.ID, new LoadedOntologyPage(owlEditorKit));
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
    
    public boolean isImportsAreFinal() {
		return importsAreFinal;
	}
    
    public void setImportsAreFinal(boolean importsAreFinal) {
		this.importsAreFinal = importsAreFinal;
	}

}
