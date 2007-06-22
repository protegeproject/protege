package org.protege.editor.owl.model.library;

import org.protege.editor.core.plugin.ProtegePluginInstance;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
