package org.protege.editor.owl.model.io;

import com.google.common.util.concurrent.*;
import org.protege.editor.owl.model.IOListenerManager;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OntologyManagerFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.util.LowMemoryNotificationView;
import org.protege.editor.owl.model.util.MemoryMonitor;
import org.protege.editor.owl.ui.util.LowMemoryNotificationViewImpl;
import org.protege.editor.owl.ui.util.ProgressDialog;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyBuilder;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NonConcurrentOWLOntologyBuilder;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    	ListenableFuture<Optional<OWLOntology>> result = ontologyLoadingService
				.submit(() -> {					
					try {						
						return loadOntologyInternal(uri);
					} finally {
						dlg.setVisible(false);
					}					
				});
    	dlg.setVisible(true);
        try {
            return result.get();
        } catch (InterruptedException e) {
            return Optional.empty();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof OWLOntologyCreationException) {
                throw (OWLOntologyCreationException) e.getCause();
            }
            else {
                logger.error("An error occurred whilst loading the ontology at {}. Cause: {}", uri, e.getCause().getMessage());
            }
            return Optional.empty();
        }
    }

    private OWLOntologyManager getOntologyManager() {
        return modelManager.getOWLOntologyManager();
    }

    private Optional<OWLOntology> loadOntologyInternal(URI documentURI) throws OWLOntologyCreationException {

        MemoryMonitor memoryMonitor = new MemoryMonitor(new LowMemoryNotificationViewImpl());
        OWLOntology ontology;
        // I think the loading manager needs to be a concurrent manager because we
        // copy over the ontologies and the ontologies have to be concurrent ontology implementations
        OWLOntologyManager loadingManager = createInterceptingManager(memoryMonitor::checkMemory);

        PriorityCollection<OWLOntologyIRIMapper> iriMappers = loadingManager.getIRIMappers();
        iriMappers.clear();
        iriMappers.add(userResolvedIRIMapper);
        iriMappers.add(new WebConnectionIRIMapper());
        iriMappers.add(new AutoMappedRepositoryIRIMapper(
                modelManager.getOntologyCatalogManager(), documentURI));

        loadingManager.addOntologyLoaderListener(new ProgressDialogOntologyLoaderListener(dlg, logger));

        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        IRIDocumentSource documentSource = new IRIDocumentSource(IRI.create(documentURI));
        ontology = loadingManager.loadOntologyFromOntologyDocument(documentSource, configuration);
        Set<OWLOntology> alreadyLoadedOntologies = new HashSet<>();
        for (OWLOntology loadedOntology : loadingManager.getOntologies()) {
            if (!modelManager.getOntologies().contains(loadedOntology)) {
                OWLOntologyManager modelManager = getOntologyManager();
                fireBeforeLoad(loadedOntology, documentURI);
                modelManager.copyOntology(loadedOntology, OntologyCopy.MOVE);
                fireAfterLoad(loadedOntology, documentURI);
            }
            else {
                alreadyLoadedOntologies.add(loadedOntology);
            }
        }
        if(!alreadyLoadedOntologies.isEmpty()) {
            displayOntologiesAlreadyLoadedMessage(alreadyLoadedOntologies);
        }


        modelManager.setActiveOntology(ontology);
        modelManager.fireEvent(EventType.ONTOLOGY_LOADED);
        OWLOntologyID id = ontology.getOntologyID();
        if (!id.isAnonymous()) {
            getOntologyManager().getIRIMappers().add(new SimpleIRIMapper(id.getDefaultDocumentIRI().get(), IRI.create(documentURI)));
        }
        return Optional.of(ontology);
    }

    private void displayOntologiesAlreadyLoadedMessage(Set<OWLOntology> alreadyLoadedOntologies) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("The following ontologies are already loaded in this workspace<br><br>");
        for(OWLOntology alreadyLoadedOntology : alreadyLoadedOntologies) {
            String ren = modelManager.getRendering(alreadyLoadedOntology);
            sb.append("<b>");
            sb.append(ren);
            sb.append("</b><br>");
        }
        sb.append("<br>");
        sb.append("They have not been replaced/overwritten");
        sb.append("</body></html>");

        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, sb.toString(), "Workspace already contains loaded ontologies", JOptionPane.WARNING_MESSAGE));
    }


    private void fireBeforeLoad(OWLOntology loadedOntology, URI documentURI) {
        if(modelManager instanceof IOListenerManager) {
            ((IOListenerManager) modelManager).fireBeforeLoadEvent(
                    loadedOntology.getOntologyID(),
                    documentURI);
        }
    }

    private void fireAfterLoad(OWLOntology loadedOntology, URI documentURI) {
        if(modelManager instanceof IOListenerManager) {
            ((IOListenerManager) modelManager).fireAfterLoadEvent(
                    loadedOntology.getOntologyID(),
                    documentURI);
        }
    }

    /**
     * Creates an ontology manager that is suitable for loading ontology and that
     * intercepts the add axiom changes.
     * @param intercept A runnable that will be called when an axiom is added to an ontology
     *                  that is being loaded
     */
    public static OWLOntologyManager createInterceptingManager(Runnable intercept) {
        OWLOntologyManager m = OWLManager.createConcurrentOWLOntologyManager();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        OWLOntologyManager manager = new OWLOntologyManagerImpl(new OWLDataFactoryImpl(), lock) {
            @Override
            public ChangeApplied addAxiom(@Nonnull OWLOntology ont,
                                          @Nonnull OWLAxiom axiom) {
                intercept.run();
                return super.addAxiom(ont, axiom);
            }
        };
        OWLOntologyFactory factory = new OWLOntologyFactoryImpl(new ConcurrentOWLOntologyBuilder(new NonConcurrentOWLOntologyBuilder(), lock));
        manager.setOntologyFactories(Collections.singleton(factory));
        manager.getOntologyParsers().add(m.getOntologyParsers());
        manager.getOntologyFactories().set(m.getOntologyFactories());
        return manager;
    }
}
