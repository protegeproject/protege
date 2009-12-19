package org.protege.editor.owl.model.library;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;


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

    private OntologyLibraryLoader loader;


    public OntologyLibraryManager() {
    }


    private void ensureLoaded(){
        if (libraries == null){
            libraries = new ArrayList<OntologyLibrary>();
            loader = new OntologyLibraryLoader(this);
            loader.loadOntologyLibraries();
        }
    }

    public void addLibrary(OntologyLibrary library) {
        ensureLoaded();
        libraries.add(library);
        loader.saveLibraries();
    }


    public List<OntologyLibrary> getLibraries() {
        ensureLoaded();
        return new ArrayList<OntologyLibrary>(libraries);
    }


    public void removeLibraray(OntologyLibrary library) {
        ensureLoaded();
        libraries.remove(library);
        loader.saveLibraries();
    }

    public URI getPhysicalURI(IRI ontologyIRI) {
        ensureLoaded();
        List<OntologyLibrary> toRemove = new ArrayList<OntologyLibrary>();
        URI physicalUri = null;
        for (OntologyLibrary library : libraries) {
            if ((physicalUri = library.getPhysicalURI(ontologyIRI)) != null) {
                break;
            }
        }
        if (!toRemove.isEmpty()) {
            ensureLoaded();
            libraries.removeAll(toRemove);
            loader.saveLibraries();
        }
        return physicalUri;
    }


}
