package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.OntologyIRIExtractor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class URLImportFileVerifier implements ImportVerifier {

    private URL url;


    public URLImportFileVerifier(URL url) {
        this.url = url;
    }


    public ImportParameters checkImports() {
        try {
            OntologyIRIExtractor extractor = new OntologyIRIExtractor(url.toURI());
            final IRI ontologyIRI = extractor.getOntologyIRI();
            if (!extractor.isStartElementPresent() || ontologyIRI == null) {
                String msg = "The ontology contained in the document located at " + url.toString() + " could " + "not be imported. ";
                if (!extractor.isStartElementPresent()) {
                    msg += " The document does not appear to contain a valid RDF/XML representation" + "of an ontology.";
                }
                throw new RuntimeException(msg);
            }
            else {
                return new ImportParameters() {
                    public Set<IRI> getOntologiesToBeImported() {
                        return Collections.singleton(ontologyIRI);
                    }


                    public String getOntologyLocationDescription(IRI ontologyURI) {
                        return url.toString();
                    }


                    public void performImportSetup(OWLEditorKit editorKit) {
                        // May be we need to add a mapping?
                        try {
                            OWLOntologyManager man = editorKit.getModelManager().getOWLOntologyManager();
                            man.addIRIMapper(new SimpleIRIMapper(ontologyIRI, url.toURI()));
                        }
                        catch (URISyntaxException e) {
                            throw new OWLRuntimeException(e);
                        }
                    }
                };
            }
        }
        catch (URISyntaxException e) {
            throw new OWLRuntimeException(e);
        }
    }
}
