package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryManager;
import org.semanticweb.owl.model.IRI;

import javax.swing.*;
import java.util.Set;
import java.util.TreeSet;


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
        Set<IRI> ontologyIRIs = new TreeSet<IRI>();
        OntologyLibraryManager libraryManager = owlModelManager.getOntologyLibraryManager();
        for (OntologyLibrary lib : libraryManager.getLibraries()) {
            ontologyIRIs.addAll(lib.getOntologyIRIs());
        }
        setListData(ontologyIRIs.toArray());
    }


    public IRI getSelectedOntologyIRI() {
        return (IRI)getSelectedValue();
    }
}
