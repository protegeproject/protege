package org.protege.editor.owl.model.library;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyLibraryManager {

    private static final Logger logger = Logger.getLogger(OntologyLibraryManager.class);

    private List<OntologyLibrary> libraries;


    public OntologyLibraryManager() {
        libraries = new ArrayList<OntologyLibrary>();
    }


    public void addLibrary(OntologyLibrary library) {
        libraries.add(library);
    }


    public List<OntologyLibrary> getLibraries() {
        return new ArrayList<OntologyLibrary>(libraries);
    }


    public void removeLibraray(OntologyLibrary library) {
        libraries.remove(library);
    }


    public OntologyLibrary getLibrary(URI ontologyURI) {
        for (OntologyLibrary library : libraries) {
            if (library.contains(ontologyURI)) {
                return library;
            }
        }
        return null;
    }
}
