package org.protege.editor.owl.model.namespace;

import java.net.URI;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>

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
