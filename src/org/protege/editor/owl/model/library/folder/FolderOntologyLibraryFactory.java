package org.protege.editor.owl.model.library.folder;

import java.io.File;

import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryFactory;
import org.protege.editor.owl.model.library.OntologyLibraryMemento;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FolderOntologyLibraryFactory implements OntologyLibraryFactory {

    public boolean isSuitable(OntologyLibraryMemento memento) {
        return memento.getLibraryTypeId().equals(FolderOntologyLibrary.ID);
    }


    public OntologyLibrary createLibrary(OntologyLibraryMemento memento) {
        File file = memento.getFile(FolderOntologyLibrary.FILE_KEY);
        if (file.exists() && file.isDirectory()) {
            return new FolderOntologyLibrary(file);
        }
        else {
            return null;
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
