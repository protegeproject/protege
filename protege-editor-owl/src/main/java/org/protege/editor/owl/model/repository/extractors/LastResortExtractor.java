package org.protege.editor.owl.model.repository.extractors;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandler;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandlerUI;
import org.protege.editor.owl.ui.ontology.authentication.BasicAuthenticationDialog;
import org.protege.editor.owl.ui.ontology.authentication.BasicAuthenticationHandler;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


public class LastResortExtractor implements OntologyIdExtractor {

    private Logger log = LoggerFactory.getLogger(LastResortExtractor.class);
    
    private BasicAuthenticationHandler basicAuthenticationHandler;

    private OntologyLoadErrorHandler loadErrorHandler;
    
    public LastResortExtractor() {
    }
    
    public LastResortExtractor(BasicAuthenticationDialog basicAuthenticationDialogHandler, OntologyLoadErrorHandlerUI ontologyLoadErrorHandler) {
        this.basicAuthenticationHandler = basicAuthenticationDialogHandler;
        this.loadErrorHandler = ontologyLoadErrorHandler;
    }

    public Optional<OWLOntologyID> getOntologyId(URI location) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(location));
            return Optional.of(ontology.getOntologyID());
        } catch (Throwable t) {
            log.info("Exception caught trying to get ontology id for " + location, t);
            OWLOntologyID id = new OWLOntologyID(com.google.common.base.Optional.of(IRI.create(location)), com.google.common.base.Optional.<IRI>absent());
            // Check error message contain HTTP response code: 401 or 403 status then show the popup for basic authentication
            if (t.getMessage().contains("Server returned HTTP response code: 401") || t.getMessage().contains("Server returned HTTP response code: 403")) {
                String base64AuthenticationValue = handleBasicAuthentication(id, location, (Exception) t);
                
                if (base64AuthenticationValue != null) {
                    return getOntologyId(location, base64AuthenticationValue);
                } else {
                    handleLoadError(id, location, (Exception) t);
                }
            } else {
                handleLoadError(id, location, (Exception) t);
            }
            return null;
        }
    }

    public Optional<OWLOntologyID> getOntologyId(URI location, String authenticationValue) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
            configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
            if (authenticationValue != null && !authenticationValue.isEmpty()) {
                configuration = configuration.setAuthorizationValue(authenticationValue);
            }
            IRIDocumentSource documentSource = new IRIDocumentSource(IRI.create(location));
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentSource, configuration);
            return Optional.of(ontology.getOntologyID());
        } catch (Throwable t) {
            log.info("Exception caught trying to get ontology id for " + location, t);
            OWLOntologyID id = new OWLOntologyID(com.google.common.base.Optional.of(IRI.create(location)), com.google.common.base.Optional.<IRI>absent());
            // Check error message contain HTTP response code: 401 or 403 status then show the popup for basic authentication
            if (t.getMessage().contains("Server returned HTTP response code: 401") || t.getMessage().contains("Server returned HTTP response code: 403")) {
                String base64AuthenticationValue = handleBasicAuthentication(id, location, (Exception) t);
                if (base64AuthenticationValue != null) {
                    return getOntologyId(location, base64AuthenticationValue);
                } else {
                    handleLoadError(id, location, (Exception) t);
                }
            } else {
                handleLoadError(id, location, (Exception) t);
            }
            return null;
        }
    }

    private String handleBasicAuthentication(OWLOntologyID owlOntologyID, URI documentURI, Exception e) {
        if (basicAuthenticationHandler != null) {
            try {
                return basicAuthenticationHandler.handleBasicAuthenticationOntology(owlOntologyID, documentURI, e);
            } catch (Throwable e1) {
                // if, for any reason, the loadErrorHandler cannot report the error
                ErrorLogPanel.showErrorDialog(e1);
            }
        }
        return null;
    }

    private void handleLoadError(OWLOntologyID owlOntologyID, URI documentURI, Exception e) {
        if (loadErrorHandler != null) {
            try {
                loadErrorHandler.handleErrorLoadingOntology(owlOntologyID, documentURI, e);
            } catch (Throwable e1) {
                // if, for any reason, the loadErrorHandler cannot report the error
                ErrorLogPanel.showErrorDialog(e1);
            }
        }
    }

    public void setLoadErrorHandler(OntologyLoadErrorHandler handler) {
        this.loadErrorHandler = handler;
    }

    public void setBasicAuthenticationHandler(BasicAuthenticationHandler handler) {
        this.basicAuthenticationHandler = handler;
    }
}
