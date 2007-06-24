package org.protege.editor.owl.ui.ontology.wizard.merge;

import java.net.URI;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyURIPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLRuntimeException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergeOntologiesWizard extends Wizard {

    private static final Logger logger = Logger.getLogger(MergeOntologiesWizard.class);

    private SelectOntologiesPage selectOntologiesPage;

    private OntologyURIPanel uriPanel;

    private PhysicalLocationPanel physicalLocationPanel;

    private OWLModelManager owlModelManager;


    public MergeOntologiesWizard(OWLEditorKit editorKit) {
        setTitle("Create ontology wizard");
        this.owlModelManager = editorKit.getOWLModelManager();
        registerWizardPanel(SelectOntologiesPage.ID, selectOntologiesPage = new SelectOntologiesPage(editorKit));
        registerWizardPanel(MergeTypePage.ID, new MergeTypePage(editorKit));
        registerWizardPanel(OntologyURIPanel.ID, uriPanel = new OntologyURIPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, physicalLocationPanel = new PhysicalLocationPanel(editorKit));
        setCurrentPanel(SelectOntologiesPage.ID);
    }


    public Set<OWLOntology> getOntologiesToMerge() {
        return selectOntologiesPage.getOntologies();
    }


    public OWLOntology getOntologyToMergeInto() {
        URI uri = uriPanel.getURI();
        try {
            return owlModelManager.createNewOntology(uri, physicalLocationPanel.getLocationURI());
        }
        catch (OWLOntologyCreationException e) {
            throw new OWLRuntimeException(e);
        }
    }
}
