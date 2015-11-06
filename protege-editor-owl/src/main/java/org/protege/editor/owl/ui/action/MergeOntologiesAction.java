package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.refactor.ontology.OntologyMerger;
import org.protege.editor.owl.ui.ontology.wizard.merge.MergeOntologiesWizard;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.event.ActionEvent;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergeOntologiesAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        MergeOntologiesWizard wizard = new MergeOntologiesWizard(getOWLEditorKit());
        if (wizard.showModalDialog() == Wizard.FINISH_RETURN_CODE) {
            Set<OWLOntology> ontologies = wizard.getOntologiesToMerge();
            OWLOntology ont = wizard.getOntologyToMergeInto();
            OntologyMerger merger = new OntologyMerger(getOWLModelManager().getOWLOntologyManager(), ontologies, ont);
            merger.mergeOntologies();
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
