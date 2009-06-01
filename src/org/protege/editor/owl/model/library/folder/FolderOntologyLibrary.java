package org.protege.editor.owl.model.library.folder;

import org.protege.editor.owl.model.library.AbstractOntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryMemento;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.util.AutoIRIMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;


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

    private AutoIRIMapper mapper;


    public FolderOntologyLibrary(File folder) {
        this.folder = folder;
    }


    public String getClassExpression() {
        try {
            return "Folder: " + folder.getCanonicalPath();
        }
        catch (IOException e) {
            return "Folder";
        }
    }


    public Set<IRI> getOntologyIRIs() {
        return getMapper().getOntologyIRIs();
    }


    public boolean contains(IRI ontologyIRI) {
        return getMapper().getPhysicalURI(ontologyIRI) != null;
    }


    public URI getPhysicalURI(IRI ontologyIRI) {
        return getMapper().getPhysicalURI(ontologyIRI);
    }


    public void rebuild() {
        mapper = null;
    }


    public OntologyLibraryMemento getMemento() {
        OntologyLibraryMemento memento = new OntologyLibraryMemento(ID);
        memento.putFile(FILE_KEY, folder);
        return memento;
    }


    private AutoIRIMapper getMapper(){
        if (mapper == null){
            mapper = new AutoIRIMapper(folder, false);
            mapper.update();
        }
        return mapper;
    }
}
