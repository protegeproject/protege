package org.protege.editor.owl.model.library;

import org.protege.editor.core.plugin.ProtegePluginInstance;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OntologyLibraryFactory extends ProtegePluginInstance {

    /**
     * Determines if the factory can create an ontology library
     * from the specified memento.
     * @return <code>true</code> if the factory can create a library,
     *         or <code>false</code> if the factory cannot create a library.
     */
    public boolean isSuitable(OntologyLibraryMemento memento);


    /**
     * Creates an ontology library from the suitable memento.
     * @param memento The memento that describes the library.
     * @return A library created based on the memento, or <code>null</code>
     *         if there was a problem creating the library
     */
    public OntologyLibrary createLibrary(OntologyLibraryMemento memento);
}
