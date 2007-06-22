package org.protege.editor.owl.model.namespace;

import java.net.URI;
import java.util.Map;
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
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface NamespaceManager {

    /**
     * Gets a map of the registered namespaces and prefixes.
     * This maps prefixes to namespaces.
     */
    public Map<String, String> getNamespaceMap();


    /**
     * Determines whether or not the namespace map
     * contains the specified namespace.
     * @param namespace The namespace.
     * @return <code>true</code> if the map contains
     *         the specified namespace, or <code>false</code> if
     *         the map doesn't contain the specified namespace.
     */
    public boolean containsNamespace(String namespace);


    /**
     * Adds a prefix namespace mapping.
     */
    public void addMapping(String prefix, String namespace);


    /**
     * Removes a previously added namespace prefix mapping.
     */
    public void removeMapping(String prefix, String namespace);


    /**
     * Gets the namespace for a URI.
     * @param uri The URI whose namespace it to be determined.
     * @return A <code>String</code> that represents the namespace
     *         that the URI resides in, or <code>null</code> if there is
     *         no namespace for the specified URI.
     */
    public String getNamespace(URI uri);


    /**
     * Gets the namespace prefix for the specified URI
     * @param uri The URI whose prefix is to be determined.
     * @return A <code>String</code> that represents the prefix, or
     *         <code>null</code> if there is no namespace and hence no prefix
     *         for this URI.
     */
    public String getPrefix(URI uri);


    /**
     * Gets the QName for the specified URI.
     * @param uri The URI whose QName it to be determined.
     * @return A <code>String</code> that represents the
     *         QName for the specified URI, or <code>null</code> if a
     *         QName doesn't exist because the URI doesn't reside in a
     *         defined namespace.
     */
    public String getQName(URI uri);


    public void addListener(NamespaceManagerListener listener);


    public void removeListener(NamespaceManagerListener listener);
}
