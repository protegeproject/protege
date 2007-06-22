package org.protege.editor.owl.model.namespace;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
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
public class NamespaceManagerImpl extends AbstractNamespaceManager {

    Map<String, String> ns2Prefix;


    public NamespaceManagerImpl() {
        ns2Prefix = new TreeMap<String, String>();
    }


    public Map<String, String> getNamespaceMap() {
        return new TreeMap(new NamespaceComparator());
    }


    public boolean containsNamespace(String namespace) {
        return ns2Prefix.keySet().contains(namespace);
    }


    public void addMapping(String prefix, String namespace) {
        String old = ns2Prefix.put(namespace, prefix);
        fireMappingAdded(prefix, namespace);
    }


    public void removeMapping(String prefix, String namespace) {
        if (ns2Prefix.remove(namespace) != null) {
            fireMappingRemoved(prefix, namespace);
        }
    }


    public String getNamespace(URI uri) {
        String uriString = uri.toString();
        for (String ns : ns2Prefix.keySet()) {
            if (uriString.startsWith(ns)) {
                return ns;
            }
        }
        // Haven't found a namespace, so just return null
        return null;
    }


    public String getPrefix(URI uri) {
        String ns = getNamespace(uri);
        if (ns == null) {
            return null;
        }
        return ns2Prefix.get(ns);
    }


    public String getQName(URI uri) {
        String ns = getNamespace(uri);
        if (ns == null) {
            return null;
        }
        String prefix = getPrefix(uri);
        String uriString = uri.toString();
        String localName = uriString.substring(ns.length(), uriString.length());
        StringBuffer b = new StringBuffer(prefix.length() + localName.length() + 1);
        b.append(prefix);
        b.append(':');
        b.append(localName);
        return b.toString();
    }


    public String getLocalName(URI uri) {
        String uriString = uri.toString();
        String ns = getNamespace(uri);
        if (ns == null) {
            return null;
        }
        return uriString.substring(ns.length(), uriString.length());
    }


    private class NamespaceComparator implements Comparator<String> {

        public int compare(String o1, String o2) {
            int diff = o1.length() - o2.length();
            if (diff == 0) {
                diff = -1;
            }
            return diff;
        }
    }
}
