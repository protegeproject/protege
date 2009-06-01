package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.repository.OntologyIRIExtractor;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.util.SimpleIRIMapper;

import java.io.File;
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
public class LocalFileImportVerifier implements ImportVerifier {

    private File file;

    private OWLEditorKit owlEditorKit;


    public LocalFileImportVerifier(OWLEditorKit owlEditorKit, File file) {
        this.file = file;
        this.owlEditorKit = owlEditorKit;
    }


    public ImportParameters checkImports() {
        OntologyIRIExtractor extractor = new OntologyIRIExtractor(file.toURI());
        final IRI ontologyURI = extractor.getOntologyIRI();
        if (!extractor.isStartElementPresent() || ontologyURI == null) {
            String msg = "The ontology contained in " + file.toString() + " could " + "not be imported. ";
            if (!extractor.isStartElementPresent()) {
                msg += " The file does not appear to contain a valid RDF/XML representation" + "of an ontology.";
            }
            throw new OWLRuntimeException(msg);
        }
        else {
            return new ImportParameters() {
                public Set<IRI> getOntologiesToBeImported() {
                    return Collections.singleton(ontologyURI);
                }


                public String getOntologyLocationDescription(IRI iri) {
                    return file.toString();
                }


                public void performImportSetup(OWLEditorKit editorKit) {
                    final OWLOntologyManager mngr = owlEditorKit.getModelManager().getOWLOntologyManager();

                    // We need to copy the file to the root ontology folder
                    mngr.addIRIMapper(new SimpleIRIMapper(ontologyURI, file.toURI()));
                    // If this fails then we can only add a direct mapping
                }
            };
        }
    }
}
