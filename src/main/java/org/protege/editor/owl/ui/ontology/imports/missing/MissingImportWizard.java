package org.protege.editor.owl.ui.ontology.imports.missing;

import java.awt.Frame;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MissingImportWizard extends Wizard {


    public MissingImportWizard(Frame frame, OWLEditorKit owlEditorKit) {
        super(frame);
        setTitle("Resolve missing import wizard");
        registerWizardPanel(ResolutionTypePanel.ID, new ResolutionTypePanel(owlEditorKit));
        registerWizardPanel(SpecifyFilePathPanel.ID, new SpecifyFilePathPanel(owlEditorKit));
        registerWizardPanel(CopyOptionPanel.ID, new CopyOptionPanel(owlEditorKit));
        setCurrentPanel(ResolutionTypePanel.ID);
    }


    public static void main(String[] args) {
        MissingImportWizard w = new MissingImportWizard(null, null);
        w.showModalDialog();
    }
}
