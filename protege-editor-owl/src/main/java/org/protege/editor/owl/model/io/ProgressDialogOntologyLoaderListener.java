package org.protege.editor.owl.model.io;

import java.io.File;
import java.util.Stack;

import org.protege.editor.owl.ui.util.ProgressDialog;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.slf4j.Logger;

/**
* Matthew Horridge
* Stanford Center for Biomedical Informatics Research
* 12 May 16
*/
public class ProgressDialogOntologyLoaderListener implements OWLOntologyLoaderListener {

    private final Stack<IRI> loadStack = new Stack<>();

    private final ProgressDialog progressDialog;

    private final Logger logger;

    public ProgressDialogOntologyLoaderListener(ProgressDialog progressDialog, Logger logger) {
        this.progressDialog = progressDialog;
        this.logger = logger;
    }

    @Override
    public void startedLoadingOntology(LoadingStartedEvent event) {
        IRI documentIRI = event.getDocumentIRI();
        loadStack.push(documentIRI);
        if (loadStack.size() == 1) {
            progressDialog.setMessage(
                    String.format("Loading %s", formatIRI(documentIRI))
            );
        }
        else {
            progressDialog.setSubMessage(
                    formatImportedDocumentIRIMessage(documentIRI)
            );
        }
    }

    @Override
    public void finishedLoadingOntology(LoadingFinishedEvent event) {
        if (event.isImported()) {
            if (event.isSuccessful()) {
                logger.info("Finished loading imported ontology at {}", event.getDocumentIRI());
            }
            else {
                logger.info("Failed to load imported ontology at {}", event.getDocumentIRI());
            }
        }
        else {
            logger.info("Finished loading {}", event.getDocumentIRI());
        }
        loadStack.pop();
        if (loadStack.size() > 1) {
            progressDialog.setSubMessage(
                    formatImportedDocumentIRIMessage(loadStack.peek())
            );
        }
        else {
            progressDialog.clearSubMessage();
        }
    }

    private String formatImportedDocumentIRIMessage(IRI documentIRI) {
        return String.format("Loading imported ontology %s", formatIRI(documentIRI));
    }



    private static String formatIRI(IRI iri) {
        if ("file".equalsIgnoreCase(iri.getScheme())) {
            return new File(iri.toURI()).toString();
        }
        else {
            return iri.toString();
        }
    }

}
