package org.protege.editor.owl.model.io;

import com.google.common.util.concurrent.*;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.ui.util.ProgressDialog;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 11 May 16
 */
public class OntologyLoader {

    private final Logger logger = LoggerFactory.getLogger(OntologyLoader.class);

    private final OWLModelManager modelManager;

    private final UserResolvedIRIMapper userResolvedIRIMapper;

    private final ProgressDialog dlg = new ProgressDialog();

    private final ListeningExecutorService ontologyLoadingService = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadExecutor()
    );

    public OntologyLoader(OWLModelManager modelManager, UserResolvedIRIMapper userResolvedIRIMapper) {
        this.modelManager = modelManager;
        this.userResolvedIRIMapper = userResolvedIRIMapper;
    }

    public Optional<OWLOntology> loadOntology(URI documentUri) throws OWLOntologyCreationException {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("The ontology loader must be called from the Event Dispatch Thread");
        }
        return loadOntologyInOtherThread(documentUri);
    }

    private Optional<OWLOntology> loadOntologyInOtherThread(URI uri) throws OWLOntologyCreationException {
        ListenableFuture<Optional<OWLOntology>> result = ontologyLoadingService.submit(() -> loadOntologyInternal(uri));
        Futures.addCallback(result, new FutureCallback<Optional<OWLOntology>>() {
            @Override
            public void onSuccess(Optional<OWLOntology> result) {
                showProgressDialog(false);
            }

            @Override
            public void onFailure(Throwable t) {
                showProgressDialog(false);
            }
        });
        showProgressDialog(true);

        try {
            return result.get();
        } catch (InterruptedException e) {
            return Optional.empty();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof OWLOntologyCreationException) {
                throw (OWLOntologyCreationException) e.getCause();
            }
            else {
                logger.error("An error occurred whilst loading the ontology at {}. Cause: {}", e.getCause().getMessage());
            }
            return Optional.empty();
        }
    }


    private void showProgressDialog(boolean b) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> dlg.setVisible(b));
        }
        else {
            dlg.setVisible(b);
        }
    }

    private OWLOntologyManager getOntologyManager() {
        return modelManager.getOWLOntologyManager();
    }

    private Optional<OWLOntology> loadOntologyInternal(URI uri) throws OWLOntologyCreationException {

        // I think the loading manager needs to be a concurrent manager because we
        // copy over the ontologies and the ontologies have to be concurrent ontology implementations
        OWLOntologyManager loadingManager = OWLManager.createConcurrentOWLOntologyManager();

        PriorityCollection<OWLOntologyIRIMapper> iriMappers = loadingManager.getIRIMappers();
        iriMappers.clear();
        iriMappers.add(userResolvedIRIMapper);
        iriMappers.add(new WebConnectionIRIMapper());
        iriMappers.add(new AutoMappedRepositoryIRIMapper(modelManager.getOntologyCatalogManager()));

        loadingManager.addOntologyLoaderListener(new ProgressDialogOntologyLoaderListener(dlg, logger));
        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        IRIDocumentSource documentSource = new IRIDocumentSource(IRI.create(uri));
        OWLOntology ontology = loadingManager.loadOntologyFromOntologyDocument(documentSource, configuration);
        for (OWLOntology loadedOntology : loadingManager.getOntologies()) {
            getOntologyManager().copyOntology(loadedOntology, OntologyCopy.MOVE);
        }
        modelManager.setActiveOntology(ontology);
        modelManager.fireEvent(EventType.ONTOLOGY_LOADED);
        OWLOntologyID id = ontology.getOntologyID();
        if (!id.isAnonymous()) {
            getOntologyManager().getIRIMappers().add(new SimpleIRIMapper(id.getDefaultDocumentIRI().get(), IRI.create(uri)));
        }
        return Optional.of(ontology);
    }

}
