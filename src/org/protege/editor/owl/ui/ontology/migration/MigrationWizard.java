package org.protege.editor.owl.ui.ontology.migration;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyURIPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;

import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @deprecated
 */
public class MigrationWizard extends Wizard {

    private OWLEditorKit owlEditorKit;


    public MigrationWizard(Frame owner, OWLEditorKit owlEditorKit) {
        super(owner);
        this.owlEditorKit = owlEditorKit;
        registerWizardPanel(MigrationStartPanel.ID, new MigrationStartPanel(owlEditorKit));
        registerWizardPanel(MigrationStartPanel.ID, new OntologyURIPanel(owlEditorKit));
        registerWizardPanel(MigrationStartPanel.ID, new PhysicalLocationPanel(owlEditorKit));
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }
}
