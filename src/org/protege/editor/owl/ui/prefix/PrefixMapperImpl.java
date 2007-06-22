package org.protege.editor.owl.ui.prefix;

import org.semanticweb.owl.vocab.Namespaces;

import java.net.URI;
import java.util.*;
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
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMapperImpl implements PrefixMapper {

    private Map<String, String> prefix2ValueMap;

    private Map<String, String> value2PrefixMap;


    public PrefixMapperImpl(Map<String, String> prefix2ValueMap) {
        this.prefix2ValueMap = new HashMap<String, String>();
        this.value2PrefixMap = new HashMap<String, String>();
        load(prefix2ValueMap);
        loadStandardPrefixes();
    }


    public void loadStandardPrefixes() {
        addPrefixMapping("owl", Namespaces.OWL.toString());
        addPrefixMapping("rdfs", Namespaces.RDFS.toString());
        addPrefixMapping("rdf", Namespaces.RDF.toString());
        addPrefixMapping("xsd", Namespaces.XSD.toString());
        addPrefixMapping("swrl", Namespaces.SWRL.toString());
        addPrefixMapping("swrlb", Namespaces.SWRLB.toString());
        // We could load dublin core prefixes etc. here
        addPrefixMapping("dc", "http://purl.org/dc/elements/1.1/");
        addPrefixMapping("dcterms", "http://purl.org/dc/terms/");
        addPrefixMapping("dctype", "http://purl.org/dc/dcmitype/Image");
        addPrefixMapping("foaf", "http://xmlns.com/foaf/0.1/");
    }


    private void load(Map<String, String> prefix2Value) {
        for (String prefix : prefix2Value.keySet()) {
            prefix2ValueMap.put(prefix, prefix2Value.get(prefix));
            value2PrefixMap.put(prefix2Value.get(prefix), prefix);
        }
    }


    public PrefixMapperImpl() {
        this(Collections.EMPTY_MAP);
    }


    public String getPrefix(String value) {
        return value2PrefixMap.get(value);
    }


    public boolean addPrefixMapping(String prefix, String value) {
        prefix2ValueMap.put(prefix, value);
        value2PrefixMap.put(value, prefix);
        return true;
    }


    public void removePrefixMapping(String prefix) {
        String value = prefix2ValueMap.get(prefix);
        if (value == null) {
            return;
        }
        prefix2ValueMap.remove(prefix);
        value2PrefixMap.remove(value);
    }


    public String getValue(String prefix) {
        return prefix2ValueMap.get(prefix);
    }


    public Set<String> getPrefixes() {
        return new HashSet<String>(prefix2ValueMap.keySet());
    }


    public Set<String> getValues() {
        return new HashSet<String>(value2PrefixMap.keySet());
    }


    public Map<String, String> getPrefixMap() {
        return new HashMap<String, String>(prefix2ValueMap);
    }


    public String getShortForm(URI uri) {
        String uriString = uri.toString();
        for (String prefix : getPrefixes()) {
            String value = prefix2ValueMap.get(prefix);
            if (uriString.startsWith(value)) {
                // We have our mapping
                return prefix + ":" + uriString.substring(value.length(), uriString.length());
            }
        }
        return null;
    }
}
