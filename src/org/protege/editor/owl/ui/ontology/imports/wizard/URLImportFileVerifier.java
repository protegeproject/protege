package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;


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
            MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor(url.toURI());
            final OWLOntologyID id = extractor.getOntologyId();
            if (id == null) {
                String msg = "The ontology contained in the document located at " + url.toString() + " could " + "not be imported. ";
                throw new RuntimeException(msg);
            }
            else {
                return new ImportParameters() {
                    public Set<IRI> getOntologiesToBeImported() {
                        return Collections.singleton(id.getOntologyIRI());
                    }


                    public String getOntologyLocationDescription(IRI ontologyURI) {
                        return url.toString();
                    }


                    public void performImportSetup(OWLEditorKit editorKit) {
                        // May be we need to add a mapping?
                        try {
                            OWLOntologyManager man = editorKit.getModelManager().getOWLOntologyManager();
                            man.addIRIMapper(new SimpleIRIMapper(id.getOntologyIRI(), url.toURI()));
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
