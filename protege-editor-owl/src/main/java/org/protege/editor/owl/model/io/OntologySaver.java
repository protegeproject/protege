package org.protege.editor.owl.model.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.protege.editor.owl.ui.util.ProgressDialog;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;


/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 13 May 16
 *
 * Saves ontologies in a background thread and displays a progress dialog whilst the ontologies are being saved.
 */
public class OntologySaver {

    private final Logger logger = LoggerFactory.getLogger(OntologySaver.class);

    private final List<OntologySaveDescriptor> saveDescriptors = new ArrayList<>();

    private final ProgressDialog dlg = new ProgressDialog();

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

    private OntologySaver(List<OntologySaveDescriptor> saveDescriptors) {
        this.saveDescriptors.addAll(saveDescriptors);
    }

    /**
     * Save the ontologies that were added to this saver using the OntologySaver.Builder.
     * @throws OWLOntologyStorageException if there was a problem saving an ontology.
     */
    public void saveOntologies() throws OWLOntologyStorageException {
		ListenableFuture<Void> future = executorService.submit(() -> {			
			try {
				return saveOntologyInternal();
			} finally {
				dlg.setVisible(false);
			}
		});
		dlg.setVisible(true);
        try {
            future.get();
        } catch (InterruptedException e) {
            logger.info("Save was interrupted");
        } catch (ExecutionException e) {
            if(e.getCause() instanceof OWLOntologyStorageException) {
                throw (OWLOntologyStorageException) e.getCause();
            }
            else if(e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            else {
                logger.info("An error occurred during save: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Does the actual saving.
     * @return {@code null}.
     * @throws OWLOntologyStorageException if there was a problem saving the ontology.
     */
    private Void saveOntologyInternal() throws OWLOntologyStorageException {
        for (OntologySaveDescriptor descriptor : saveDescriptors) {
            try {
                OWLOntology ontology = descriptor.getOntology();
                OntologyIRIShortFormProvider sfp = new OntologyIRIShortFormProvider();
                String ontologyShortForm = sfp.getShortForm(ontology);
                logger.info("Saving {}", ontologyShortForm);
                dlg.setMessage(String.format("Saving ontology: %s", ontologyShortForm));
                IRI documentIRI = descriptor.getDocumentIRI();
                dlg.setSubMessage(String.format("Location: %s", formatIRI(documentIRI)));
                if ("file".equals(documentIRI.getScheme())) {
                    File tempFile;
                    // Save temp file first and then copy over.
                    tempFile = Files.createTempFile("temp-ontology", "").toFile();
                    try {
                        logger.info("Saving ontology to temp file: {}", tempFile);
                        ontology.saveOntology(descriptor.getDocumentFormat(), IRI.create(tempFile));
                        File destFile = new File(documentIRI.toURI());
                        logger.info("Copying ontology from temp file ({}) to actual destination ({})", tempFile, destFile);
                        FileUtils.copyFile(tempFile, destFile);
                    } finally {
                        logger.info("Removing temp file: {}", tempFile);
                        FileUtils.deleteQuietly(tempFile);
                    }
                }
                else {
                    logger.info("Saving ontology to: {}", documentIRI);
                    ontology.saveOntology(descriptor.getDocumentFormat(), documentIRI);
                }
            }
            catch (IOException e) {
                throw new OWLOntologyStorageException(e);
            }

        }
        return null;
    }

    private static String formatIRI(IRI iri) {
        if("file".equals(iri.getScheme())) {
            return new File(iri.toString()).toString();
        }
        else {
            return iri.toString();
        }
    }


    private static final class OntologySaveDescriptor {

        private final OWLOntology ontology;

        private final OWLDocumentFormat documentFormat;

        private final IRI documentIRI;

        public OntologySaveDescriptor(OWLOntology ontology, OWLDocumentFormat documentFormat, IRI documentIRI) {
            this.ontology = checkNotNull(ontology);
            this.documentFormat = checkNotNull(documentFormat);
            this.documentIRI = checkNotNull(documentIRI);
        }

        public OWLOntology getOntology() {
            return ontology;
        }

        public OWLDocumentFormat getDocumentFormat() {
            return documentFormat;
        }

        public IRI getDocumentIRI() {
            return documentIRI;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<OntologySaveDescriptor> saveDescriptors = new ArrayList<>();

        /**
         * Adds an ontology that will be saved in the specified format to the specified document IRI.
         * @param ontology The ontology.  Not {@code null}.
         * @param documentFormat The document format. Not {@code null}.
         * @param documentIRI The document IRI.  Not {@code null}.
         * @throws java.lang.NullPointerException if any parameters are null.
         */
        public Builder addOntology(OWLOntology ontology, OWLDocumentFormat documentFormat, IRI documentIRI) {
            saveDescriptors.add(new OntologySaveDescriptor(ontology, documentFormat, documentIRI));
            return this;
        }

        /**
         * Build the OntologySaver.
         * @return The built OntologySaver.
         */
        public OntologySaver build() {
            return new OntologySaver(saveDescriptors);
        }


    }
}
