package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.wizard.Wizard;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyAction extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
        try {
            ExportInferredOntologyWizard wizard = new ExportInferredOntologyWizard(getOWLEditorKit());
            int ret = wizard.showModalDialog();
            if (ret != Wizard.FINISH_RETURN_CODE) {
                return;
            }
            List<InferredAxiomGenerator<? extends OWLAxiom>> inferredAxiomGenerator = wizard.getInferredAxiomGenerators();

            OWLOntologyManager tempMan = OWLManager.createOWLOntologyManager();
            InferredOntologyGenerator gen = new InferredOntologyGenerator(getOWLModelManager().getReasoner(),
                                                                          inferredAxiomGenerator);
            OWLOntology ont = tempMan.createOntology(wizard.getOntologyID());

            gen.fillOntology(tempMan, ont);

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            if (wizard.isIncludeAnnotations()) {
                for (OWLOntology o : getOWLModelManager().getReasoner().getRootOntology().getImportsClosure()) {
                    for (OWLAnnotation annot : o.getAnnotations()) {
                        changes.add(new AddOntologyAnnotation(ont, annot));
                    }
                }
            }
            if (wizard.isIncludeAssertedLogicalAxioms()) {
                for (OWLOntology o : getOWLModelManager().getReasoner().getRootOntology().getImportsClosure()) {
                    for (OWLLogicalAxiom ax : o.getLogicalAxioms()) {
                        changes.add(new AddAxiom(ont, ax));
                    }
                }
            }
            tempMan.applyChanges(changes);

            tempMan.saveOntology(ont, wizard.getFormat(), IRI.create(wizard.getPhysicalURL()));

            JOptionPane.showMessageDialog(getWorkspace(),
                                          "The inferred axioms have been exported as an ontology to \n" + wizard.getPhysicalURL(),
                                          "Export finished",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
        catch (OWLOntologyCreationException e1) {
            JOptionPane.showMessageDialog(getWorkspace(),
                                          "Could not create ontology:\n" + e1.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
        catch (OWLOntologyStorageException e2) {
            ProtegeApplication.getErrorLog().logError(e2);
        }
        catch (OWLOntologyChangeException e1) {
            ProtegeApplication.getErrorLog().logError(e1);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
