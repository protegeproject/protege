package org.protege.editor.owl.ui.ontology.wizard.create;

import java.awt.Frame;
import java.net.URI;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CreateOntologyWizard extends Wizard {

    private OntologyURIPanel ontologyURIPanel;

    private PhysicalLocationPanel physicalLocationPanel;

    public CreateOntologyWizard(Frame owner, OWLEditorKit editorKit) {
        super(owner);
        setTitle("Create ontology wizard");
        registerWizardPanel(OntologyURIPanel.ID, ontologyURIPanel = new OntologyURIPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, physicalLocationPanel = new PhysicalLocationPanel(editorKit));
        setCurrentPanel(OntologyURIPanel.ID);
    }

    public URI getOntologyURI() {
        return ontologyURIPanel.getURI();
    }

    public URI getLocationURI() {
        return physicalLocationPanel.getLocationURI();
    }

    public int showModalDialog() {
        int ret = super.showModalDialog();
        if(ret == Wizard.FINISH_RETURN_CODE) {
            physicalLocationPanel.storeRecentLocations();
        }
        return ret;
    }

}
