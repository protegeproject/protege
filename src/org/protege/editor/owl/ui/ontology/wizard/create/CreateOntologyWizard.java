package org.protege.editor.owl.ui.ontology.wizard.create;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.action.OntologyFormatPage;
import org.semanticweb.owl.model.OWLOntologyFormat;

import java.awt.*;
import java.net.URI;

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

    private OntologyFormatPage formatPanel;


    public CreateOntologyWizard(Frame owner, OWLEditorKit editorKit) {
        super(owner);
        setTitle("Create ontology wizard");
        registerWizardPanel(OntologyURIPanel.ID, ontologyURIPanel = new OntologyURIPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, physicalLocationPanel = new PhysicalLocationPanel(editorKit));
        registerWizardPanel(OntologyFormatPage.ID, formatPanel = new OntologyFormatPage(editorKit));
        setCurrentPanel(OntologyURIPanel.ID);
    }

    public URI getOntologyURI() {
        return ontologyURIPanel.getURI();
    }

    public URI getLocationURI() {
        return physicalLocationPanel.getLocationURI();
    }

    public OWLOntologyFormat getFormat() {
        return formatPanel.getFormat();
    }

    public int showModalDialog() {
        int ret = super.showModalDialog();
        if(ret == Wizard.FINISH_RETURN_CODE) {
            physicalLocationPanel.storeRecentLocations();
        }
        return ret;
    }

}
