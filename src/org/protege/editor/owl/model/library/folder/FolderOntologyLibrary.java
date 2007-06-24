package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.protege.editor.owl.model.library.AbstractOntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryMemento;
import org.semanticweb.owl.util.AutoURIMapper;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FolderOntologyLibrary extends AbstractOntologyLibrary {


    public static final String ID = FolderOntologyLibrary.class.getName();

    public static final String FILE_KEY = "FILE";

    private File folder;

    private AutoURIMapper mapper;


    public FolderOntologyLibrary(File folder) {
        this.folder = folder;
        mapper = new AutoURIMapper(folder, false);
        rebuild();
    }


    public String getDescription() {
        try {
            return "Folder: " + folder.getCanonicalPath();
        }
        catch (IOException e) {
            return "Folder";
        }
    }


    public Set<URI> getOntologyURIs() {
        return mapper.getOntologyURIs();
    }


    public boolean contains(URI ontologyURI) {
        return mapper.getPhysicalURI(ontologyURI) != null;
    }


    public URI getPhysicalURI(URI ontologyURI) {
        return mapper.getPhysicalURI(ontologyURI);
    }


    public void rebuild() {
        mapper = new AutoURIMapper(folder, false);
        mapper.update();
    }


    public OntologyLibraryMemento getMemento() {
        OntologyLibraryMemento memento = new OntologyLibraryMemento(ID);
        memento.putFile(FILE_KEY, folder);
        return memento;
    }
}
