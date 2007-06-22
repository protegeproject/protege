package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;

import java.awt.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
