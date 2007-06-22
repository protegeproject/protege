package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

import java.util.List;
import java.util.Set;
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
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An interface to an object that can provide a hierarchy of objects, for
 * example a class, property or individual hierarchy.
 */
public interface OWLObjectHierarchyProvider<N extends OWLObject> {


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies);


    /**
     * Disposes of the provider.  This should be called when the
     * provider is no longer required in order to remove listeners
     * etc.
     */
    public void dispose();


    /**
     * Gets the objects that represent the roots of the hierarchy.
     */
    public Set<N> getRoots();


    public Set<N> getChildren(N object);


    public Set<N> getDescendants(N object);


    public Set<N> getParents(N object);


    public Set<N> getAncestors(N object);


    public Set<N> getEquivalents(N object);


    public Set<List<N>> getPathsToRoot(N object);


    public boolean containsReference(N object);


    public void addListener(OWLObjectHierarchyProviderListener<N> listener);


    public void removeListener(OWLObjectHierarchyProviderListener<N> listener);
}
