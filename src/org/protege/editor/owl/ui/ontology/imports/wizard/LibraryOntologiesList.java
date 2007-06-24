package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JList;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LibraryOntologiesList extends JList {

    private OWLModelManager owlModelManager;


    public LibraryOntologiesList(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public void rebuildList() {
        Set<URI> ontologyURIs = new TreeSet<URI>();
        OntologyLibraryManager libraryManager = owlModelManager.getOntologyLibraryManager();
        for (OntologyLibrary lib : libraryManager.getLibraries()) {
            ontologyURIs.addAll(lib.getOntologyURIs());
        }
        setListData(ontologyURIs.toArray());
    }


    public URI getSelectedOntologyURI() {
        URI uri = (URI) getSelectedValue();
        return uri;
    }
}
