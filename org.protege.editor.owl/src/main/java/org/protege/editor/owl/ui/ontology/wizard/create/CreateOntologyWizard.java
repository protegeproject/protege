package org.protege.editor.owl.ui.ontology.wizard.create;

import java.awt.Frame;
import java.net.URI;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.action.OntologyFormatPage;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;

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

    private OntologyIDPanel ontologyIDPanel;

    private PhysicalLocationPanel physicalLocationPanel;

    private OntologyFormatPage formatPanel;


    public CreateOntologyWizard(Frame owner, OWLEditorKit editorKit) {
        super(owner);
        setTitle("Create ontology wizard");
        registerWizardPanel(OntologyIDPanel.ID, ontologyIDPanel = new OntologyIDPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, physicalLocationPanel = new PhysicalLocationPanel(editorKit));
        registerWizardPanel(OntologyFormatPage.ID, formatPanel = new OntologyFormatPage(editorKit));
        setCurrentPanel(OntologyIDPanel.ID);
    }

    public OWLOntologyID getOntologyID() {
        return ontologyIDPanel.getOntologyID();
    }

    public URI getLocationURI() {
        return physicalLocationPanel.getLocationURL();
    }

    public OWLDocumentFormat getFormat() {
        return formatPanel.getFormat();
    }

    @Override
    public int showModalDialog() {
        int ret = super.showModalDialog();
        if(ret == Wizard.FINISH_RETURN_CODE) {
            physicalLocationPanel.storeRecentLocations();
        }
        return ret;
    }

}
