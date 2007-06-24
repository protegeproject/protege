package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LibraryVerifier implements ImportVerifier {

    private OWLModelManager owlModelManager;

    private Set<URI> uris;


    public LibraryVerifier(OWLModelManager owlModelManager, Set<URI> uris) {
        this.owlModelManager = owlModelManager;
        this.uris = new HashSet<URI>(uris);
    }


    public ImportParameters checkImports() {
//        // All should be o.k.
        return new ImportParameters() {
            public Set<URI> getOntologiesToBeImported() {
                return uris;
            }


            public String getOntologyLocationDescription(URI ontologyURI) {
                OntologyLibrary lib = owlModelManager.getOntologyLibraryManager().getLibrary(ontologyURI);
                return lib.getPhysicalURI(ontologyURI).toString();
            }


            public void performImportSetup(OWLEditorKit editorKit) {
                // Nothing to do
            }
        };
    }
}
