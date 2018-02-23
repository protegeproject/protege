package org.protege.editor.owl.model.repository;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.repository.extractors.LastResortExtractor;
import org.protege.editor.owl.model.repository.extractors.OntologyIdExtractor;
import org.protege.editor.owl.model.repository.extractors.RdfXmlExtractor;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandlerUI;
import org.protege.editor.owl.ui.ontology.authentication.BasicAuthenticationDialog;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MasterOntologyIDExtractor implements OntologyIdExtractor {
    
    private List<OntologyIdExtractor> extractors = new ArrayList<>();
    
    // TODO - all the callers of this method have problems which need fixing.
    public MasterOntologyIDExtractor() {
        extractors.add(new RdfXmlExtractor());
        extractors.add(new LastResortExtractor());
    }
    
    public MasterOntologyIDExtractor(BasicAuthenticationDialog basicAuthenticationDialogHandler, OntologyLoadErrorHandlerUI ontologyLoadErrorHandler) {
        extractors.add(new RdfXmlExtractor());
        extractors.add(new LastResortExtractor(basicAuthenticationDialogHandler, ontologyLoadErrorHandler));
    }

    public Optional<OWLOntologyID> getOntologyId(URI location) {
        Optional<OWLOntologyID> id = Optional.absent();
        for (OntologyIdExtractor extractor : extractors) {
            id = extractor.getOntologyId(location);
            if (id.isPresent()) {
                break;
            }
        }
        return id;
    }


}
