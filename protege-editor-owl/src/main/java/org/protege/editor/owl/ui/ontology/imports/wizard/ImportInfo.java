package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URI;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

import com.google.common.base.Optional;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportInfo {

    private OWLOntologyID ontologyID;

    private URI physicalLocation;

    private IRI importLocation;

    public OWLOntologyID getOntologyID() {
        return ontologyID;
    }

    public void setOntologyID(OWLOntologyID ontologyID) {
        this.ontologyID = ontologyID;
    }

    public URI getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(URI physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public IRI getImportLocation() {
        return importLocation;
    }

    public void setImportLocation(IRI importLocation) {
        this.importLocation = importLocation;
    }

    public boolean isReady() {
        return importLocation != null && ontologyID != null & physicalLocation != null;
    }

    /**
     * Gets the IRI that should be used as the target of the imports declaration.
     * @return  The IRI.  Not {@code null}.
     * @throws IllegalStateException if this methods is called and {@link #isReady()} returns false.
     */
    public IRI getImportsDeclarationIRI() {
        if(!isReady()) {
            throw new IllegalStateException("ImportInfo is not ready.");
        }
        if(ontologyID != null) {
            Optional<IRI> defaultDocumentIRI = ontologyID.getDefaultDocumentIRI();
            if(defaultDocumentIRI.isPresent()) {
                return defaultDocumentIRI.get();
            }
        }
        return importLocation;
    }
}
