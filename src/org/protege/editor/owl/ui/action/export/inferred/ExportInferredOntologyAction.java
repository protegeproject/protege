package org.protege.editor.owl.ui.action.export.inferred;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.inference.ReasonerUtilities;
import org.protege.editor.owl.model.inference.VacuousAxiomVisitor;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyAction extends ProtegeOWLAction {
	private static final long serialVersionUID = 5000834279491773432L;

	public void actionPerformed(ActionEvent e) {
		try {
			OWLReasonerManager reasonerManager = getOWLModelManager().getOWLReasonerManager();
			ReasonerStatus status = reasonerManager.getReasonerStatus();
			if (status != ReasonerStatus.INITIALIZED && status != ReasonerStatus.OUT_OF_SYNC) {
				ReasonerUtilities.warnUserIfReasonerIsNotConfigured(getOWLWorkspace(), reasonerManager);
				return;
			}
			final ExportInferredOntologyWizard wizard = new ExportInferredOntologyWizard(getOWLEditorKit());
			int ret = wizard.showModalDialog();
			if (ret != Wizard.FINISH_RETURN_CODE) {
				return;
			}
			new Thread(new ExportTask(wizard), "Export Inferred Axioms").start();
		}
		catch (OWLOntologyCreationException e1) {
			JOptionPane.showMessageDialog(getWorkspace(),
					"Could not create ontology:\n" + e1.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
    
    private class ExportTask implements Runnable {
    	private ExportInferredOntologyWizard wizard;
    	private ProgressMonitor monitor;
    	private Set<InferenceType> precompute;
    	private OWLOntologyManager outputManager;
    	private List<InferredAxiomGenerator<? extends OWLAxiom>> inferredAxiomGenerators;
    	private InferredOntologyGenerator inferredOntologyGenerator;
    	private OWLOntology exportedOntology;
    	private int taskCount;
    	
    	public ExportTask(ExportInferredOntologyWizard wizard) throws OWLOntologyCreationException {
    		this.wizard = wizard;
			inferredAxiomGenerators = wizard.getInferredAxiomGenerators();
			outputManager = OWLManager.createOWLOntologyManager();
			inferredOntologyGenerator = new InferredOntologyGenerator(getOWLModelManager().getReasoner(), inferredAxiomGenerators);
			exportedOntology = outputManager.createOntology(wizard.getOntologyID());

			taskCount = inferredAxiomGenerators.size() + 1;
			if (wizard.isIncludeAnnotations()) {
				taskCount += 1;
			}
			if (wizard.isIncludeAssertedLogicalAxioms()) {
				taskCount += 1;
			}
			taskCount += 3; // classify, apply changes and save the ontology...
    	}
    	
		public void run() {
			try {
				setupMonitor();
				
				adjustProgress("Initializing Reasoner", 0);
				precompute();
				
				inferredOntologyGenerator.fillOntology(outputManager, exportedOntology);

				int currentTask = inferredAxiomGenerators.size();
				List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

				adjustProgress("Deleting trivial inferences", ++currentTask);
				deleteTrivialAxioms(changes);
				
				if (wizard.isIncludeAnnotations()) {
					adjustProgress("Adding annotations", ++currentTask);
					addAnnotations(changes);
				}
				if (wizard.isIncludeAssertedLogicalAxioms()) {
                    adjustProgress("Adding asserted axioms", ++currentTask);
                    addAsserted(changes);
                }

                adjustProgress("Applying extra changes", ++currentTask);
                outputManager.applyChanges(changes);

                adjustProgress("Saving...", ++currentTask);
                outputManager.saveOntology(exportedOntology, wizard.getFormat(), IRI.create(wizard.getPhysicalURL()));


				monitor.close();

				JOptionPane.showMessageDialog(getWorkspace(),
						"The inferred axioms have been exported as an ontology to \n" + wizard.getPhysicalURL(),
						"Export finished",
						JOptionPane.INFORMATION_MESSAGE);
			}
			catch (ExportCancelledException cancelled) {
				JOptionPane.showMessageDialog(getWorkspace(),
						"The export operation has been cancelled at the users request",
						"Export aborted",
						JOptionPane.INFORMATION_MESSAGE);
			}
			catch (Throwable t) {
				ProtegeApplication.getErrorLog().logError(t);
			}
		}
		
		private void setupMonitor() {
			monitor = new ProgressMonitor(getOWLWorkspace(), "Exporting Inferred Ontology", "Initializing", 0, taskCount);
			precompute = EnumSet.noneOf(InferenceType.class);
			int task = 1;
			for (InferredAxiomGenerator<? extends OWLAxiom> generator : inferredAxiomGenerators) {
				((MonitoredInferredAxiomGenerator<? extends OWLAxiom>) generator).setProgressMonitor(monitor, task++);
				precompute.addAll(ExportInferredOntologyPanel.getInferenceType(generator));
			}
		}
		
		private void adjustProgress(String taskDescription, int taskCount) {
			if (monitor.isCanceled()) {
				throw new ExportCancelledException();
			}
			monitor.setNote(taskDescription);
			monitor.setProgress(taskCount);
		}

	    private void precompute() {
	    	Set<InferenceType> precomputeNow = EnumSet.copyOf(precompute);
	    	OWLReasoner reasoner = getOWLModelManager().getReasoner();
	    	if (!reasoner.getPendingChanges().isEmpty()) {
	    		reasoner.flush();
	    	}
	    	precomputeNow.retainAll(reasoner.getPrecomputableInferenceTypes());
	    	for (InferenceType inference : precompute) {
	    		if (reasoner.isPrecomputed(inference)) {
	    			precomputeNow.remove(inference);
	    		}
	    	}
	    	if (!precomputeNow.isEmpty()) {
	    		reasoner.precomputeInferences(precomputeNow.toArray(new InferenceType[0]));
	    	}
	    }
	    
	    private void deleteTrivialAxioms(List<OWLOntologyChange> changes) {
	    	for (OWLAxiom axiom : exportedOntology.getAxioms()) {
	    		if (VacuousAxiomVisitor.isVacuousAxiom(axiom)) {
	    			changes.add(new RemoveAxiom(exportedOntology, axiom));
	    		}
	    	}
	    }
	    
	    private void addAnnotations(List<OWLOntologyChange> changes) {
	    	for (OWLOntology o : getOWLModelManager().getReasoner().getRootOntology().getImportsClosure()) {
	    		for (OWLAnnotation annot : o.getAnnotations()) {
	    			changes.add(new AddOntologyAnnotation(exportedOntology, annot));
	    		}
	    		for (OWLAnnotationAssertionAxiom axiom : o.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
	    			changes.add(new AddAxiom(exportedOntology, axiom));
	    		}
	    	}
	    }

	    private void addAsserted(List<OWLOntologyChange> changes) {
	    	for (OWLOntology o : getOWLModelManager().getReasoner().getRootOntology().getImportsClosure()) {
	    		for (OWLLogicalAxiom ax : o.getLogicalAxioms()) {
	    			if (ax.isAnnotated() && exportedOntology.containsAxiom(ax.getAxiomWithoutAnnotations())) {
	    				changes.add(new RemoveAxiom(exportedOntology, ax.getAxiomWithoutAnnotations()));
	    			}
	    			changes.add(new AddAxiom(exportedOntology, ax));
	    		}
	    	}
	    }

    }
    
    
}
