package org.protege.editor.owl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.ui.util.ProgressDialog;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21/01/16
 */
public class OntologyReloader {

    private static final Logger logger = LoggerFactory.getLogger(OntologyReloader.class);

    private final OWLOntology ontologyToReload;

    private final ProgressDialog dlg = new ProgressDialog();

    private final OWLModelManager modelManager;

    private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadExecutor()
    );

    public OntologyReloader(OWLOntology ontologyToReload, OWLModelManager modelManager) {
        this.ontologyToReload = ontologyToReload;
        this.modelManager = modelManager;
    }

    /**
     * Reloads the specified ontology.  Either the ontology is successfully reloaded or it is left intact.
     * @throws OWLOntologyCreationException
     */
    public void reload() throws OWLOntologyCreationException {
        logger.info(LogBanner.start("Reloading ontology"));
        logger.info("Reloading ontology: {}", ontologyToReload.getOntologyID());
        try {
            // Load the ontology as a fresh ontology
            List<OWLOntologyChange> changes = reloadOntologyAndGetPatch();
            logger.info("Applying {} change(s) to patch ontology to reloaded ontology", changes.size());
            if (!changes.isEmpty()) {
                ontologyToReload.getOWLOntologyManager().applyChanges(changes);
                if(modelManager instanceof IOListenerManager) {
                    ((IOListenerManager) modelManager).fireAfterLoadEvent(ontologyToReload.getOntologyID(), ontologyToReload.getOWLOntologyManager().getOntologyDocumentIRI(ontologyToReload).toURI());
                }
            }
        } catch (Throwable t) {
            if (t instanceof OWLOntologyCreationException) {
                throw (OWLOntologyCreationException) t;
            }
            else {
                throw new OWLOntologyCreationException(t);
            }
        }
    }

    private List<OWLOntologyChange> reloadOntologyAndGetPatch() throws OWLOntologyCreationException {
		ListenableFuture<List<OWLOntologyChange>> future = executorService
				.submit(() -> {					
					try {
						return performReloadAndGetPatch();
					} finally {
						dlg.setVisible(false);
					}
				});
		dlg.setVisible(true);
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new OWLOntologyCreationException(e);
        } catch (ExecutionException e) {
            if(e.getCause() instanceof OWLOntologyCreationException) {
                throw (OWLOntologyCreationException) e.getCause();
            }
            else {
                logger.error("An error occurred whilst reloading the ontology: {}", e.getMessage(), e);
                throw new OWLOntologyCreationException(e);
            }
        }
    }

    private List<OWLOntologyChange> performReloadAndGetPatch() throws OWLOntologyCreationException {
        dlg.setMessage("Reloading ontology");
        OWLOntologyManager reloadingManager = OWLManager.createOWLOntologyManager();
        PriorityCollection<OWLOntologyIRIMapper> iriMappers = reloadingManager.getIRIMappers();
        iriMappers.clear();
        // Should be able to share these
        iriMappers.add(ontologyToReload.getOWLOntologyManager().getIRIMappers());
        Stopwatch sw = Stopwatch.createStarted();
        long freeMemory0 = Runtime.getRuntime().freeMemory();
        // We might need declarations and imports for parsing the reloaded ontology.  Copy as much as we really need.
        OWLOntologyManager manager = ontologyToReload.getOWLOntologyManager();
        for(OWLOntology o : manager.getOntologies()) {
            // We don't need the ontology that we want to reload
            if (!o.equals(ontologyToReload)) {
                OWLOntology odecl = reloadingManager.createOntology(o.getOntologyID());
                reloadingManager.setOntologyDocumentIRI(odecl, manager.getOntologyDocumentIRI(o));
                Set<OWLDeclarationAxiom> axioms = o.getAxioms(AxiomType.DECLARATION);
                logger.info("Copying {} declaration axioms from {} for reloading", axioms.size(), o.getOntologyID());
                reloadingManager.addAxioms(odecl, axioms);
            }
        }
        sw.stop();
        long freeMemory1 = Runtime.getRuntime().freeMemory();
        long usedMemMb = (long)((freeMemory0 - freeMemory1) / (1024 * 1024.0));
        logger.info("Copied ontologies in {} ms.  Used: {} MB", sw.elapsed(TimeUnit.MILLISECONDS), usedMemMb);

        IRI ontologyDocumentIRI = manager.getOntologyDocumentIRI(ontologyToReload);
        logger.info("Reloading {} from {}", ontologyToReload.getOntologyID(), ontologyDocumentIRI);
        OWLOntology reloadedOntology = reloadingManager.loadOntologyFromOntologyDocument(ontologyDocumentIRI);
        List<OWLOntologyChange> changes = new ArrayList<>();
        // Compute a diff between the original and the reloaded ontology
        generateChangesToTransferContent(reloadedOntology, ontologyToReload, changes);
        return changes;
    }

    /**
     * Generates
     * @param from The ontology that should essentially be the final ontology
     * @param to The ontology to which changes should be applied to make it the same (in terms of id, annotations,
     *           imports and axioms) as the from ontology
     * @param changeList A list that will be filled with changes
     */
    private static void generateChangesToTransferContent(OWLOntology from, OWLOntology to, List<OWLOntologyChange> changeList) {
        for(OWLImportsDeclaration decl : from.getImportsDeclarations()) {
            if (!to.getImportsDeclarations().contains(decl)) {
                changeList.add(new AddImport(to, decl));
            }
        }
        for(OWLImportsDeclaration decl : to.getImportsDeclarations()) {
            if (!from.getImportsDeclarations().contains(decl)) {
                changeList.add(new RemoveImport(to, decl));
            }
        }
        for(OWLAnnotation annotation : from.getAnnotations()) {
            if (!to.getAnnotations().contains(annotation)) {
                changeList.add(new AddOntologyAnnotation(to, annotation));
            }
        }
        for(OWLAnnotation annotation : to.getAnnotations()) {
            if (!from.getAnnotations().contains(annotation)) {
                changeList.add(new RemoveOntologyAnnotation(to, annotation));
            }
        }
        for(OWLAxiom ax : from.getAxioms()) {
            if (!to.containsAxiom(ax)) {
                changeList.add(new AddAxiom(to, ax));
            }
        }
        for(OWLAxiom ax : to.getAxioms()) {
            if (!from.containsAxiom(ax)) {
                changeList.add(new RemoveAxiom(to, ax));
            }
        }
        if(!from.getOntologyID().equals(to.getOntologyID())) {
            changeList.add(new SetOntologyID(to, from.getOntologyID()));
        }
    }
}
