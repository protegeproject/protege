package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.semanticweb.owlapi.model.IRI;

import java.util.HashSet;
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
public class LibraryVerifier implements ImportVerifier {

    private OWLModelManager owlModelManager;

    private Set<IRI> iris;


    public LibraryVerifier(OWLModelManager owlModelManager, Set<IRI> iris) {
        this.owlModelManager = owlModelManager;
        this.iris = new HashSet<IRI>(iris);
    }


    public ImportParameters checkImports() {
//        // All should be o.k.
        return new ImportParameters() {
            public Set<IRI> getOntologiesToBeImported() {
                return iris;
            }


            public String getOntologyLocationDescription(IRI ontologyIRI) {
                OntologyLibrary lib = owlModelManager.getOntologyLibraryManager().getLibrary(ontologyIRI);
                return lib.getPhysicalURI(ontologyIRI).toString();
            }


            public void performImportSetup(OWLEditorKit editorKit) {
                // Nothing to do
            }
        };
    }
}
