package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.OntologyURIExtractor;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.util.SimpleURIMapper;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LocalFileImportVerifier implements ImportVerifier {

    private File file;

    private OWLEditorKit owlEditorKit;


    public LocalFileImportVerifier(OWLEditorKit owlEditorKit, File file) {
        this.file = file;
        this.owlEditorKit = owlEditorKit;
    }


    public ImportParameters checkImports() {
        OntologyURIExtractor extractor = new OntologyURIExtractor(file.toURI());
        final URI ontologyURI = extractor.getOntologyURI();
        if (!extractor.isStartElementPresent() || ontologyURI == null) {
            String msg = "The ontology contained in " + file.toString() + " could " + "not be imported. ";
            if (!extractor.isStartElementPresent()) {
                msg += " The file does not appear to contain a valid RDF/XML representation" + "of an ontology.";
            }
            throw new OWLRuntimeException(msg);
        }
        else {
            return new ImportParameters() {
                public Set<URI> getOntologiesToBeImported() {
                    return Collections.singleton(ontologyURI);
                }


                public String getOntologyLocationDescription(URI ontologyURI) {
                    return file.toString();
                }


                public void performImportSetup(OWLEditorKit editorKit) {
                    // We need to copy the file to the root ontology folder
                    owlEditorKit.getOWLModelManager().getOWLOntologyManager().addURIMapper(new SimpleURIMapper(
                            ontologyURI,
                            file.toURI()));
                    // If this fails then we can only add a direct mapping
                }
            };
        }
    }
}
