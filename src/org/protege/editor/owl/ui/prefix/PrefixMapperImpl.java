package org.protege.editor.owl.ui.prefix;

import org.semanticweb.owlapi.vocab.Namespaces;

import java.net.URI;
import java.util.*;


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
	
	public static String[][] standardPrefixes = {
        {"owl:", Namespaces.OWL.toString()},
        {"rdfs:", Namespaces.RDFS.toString()},
        {"rdf:", Namespaces.RDF.toString()},
        {"xsd:", Namespaces.XSD.toString()},
        {"swrl:", Namespaces.SWRL.toString()},
        {"swrlb:", Namespaces.SWRLB.toString()},
        // We could load dublin core prefixes etc. here
        {"dc:", "http://purl.org/dc/elements/1.1/"},
        {"dcterms:", "http://purl.org/dc/terms/"},
        {"dctype:", "http://purl.org/dc/dcmitype/Image"},
        {"foaf:", "http://xmlns.com/foaf/0.1/"}
    };

    private Map<String, String> prefix2ValueMap;

    private Map<String, String> value2PrefixMap;

    public static boolean isStandardPrefix(String prefix) {
        prefix = adjustPrefix(prefix);
        for (String[] prefixPair : standardPrefixes) {
            if (prefix.equals(prefixPair[0])) {
                return true;
            }
        }
        return false;
    }


    public PrefixMapperImpl(Map<String, String> prefix2ValueMap) {
        this.prefix2ValueMap = new HashMap<String, String>();
        this.value2PrefixMap = new HashMap<String, String>();
        load(prefix2ValueMap);
        loadStandardPrefixes();
    }


    private void loadStandardPrefixes() {
        for (String[] prefixPair : standardPrefixes) {
            addPrefixMapping(prefixPair[0], prefixPair[1]);
        }
    }


    private void load(Map<String, String> prefix2Value) {
        for (String prefix : prefix2Value.keySet()) {
        	String adjustedPrefix = adjustPrefix(prefix);
            prefix2ValueMap.put(adjustedPrefix, prefix2Value.get(adjustedPrefix));
            value2PrefixMap.put(prefix2Value.get(adjustedPrefix), adjustedPrefix);
        }
    }


    public PrefixMapperImpl() {
        this(Collections.EMPTY_MAP);
    }


    public String getPrefix(String value) {
        return value2PrefixMap.get(value);
    }


    public boolean addPrefixMapping(String prefix, String value) {
    	prefix = adjustPrefix(prefix);
    	boolean changed = !prefix2ValueMap.containsKey(prefix) || !value.equals(prefix2ValueMap.get(prefix));
    	if (changed) {
    		prefix2ValueMap.put(prefix, value);
    		value2PrefixMap.put(value, prefix);
    	}
    	return changed;
    }


    public void removePrefixMapping(String prefix) {
    	prefix = adjustPrefix(prefix);
        String value = prefix2ValueMap.get(prefix);
        if (value == null) {
            return;
        }
        prefix2ValueMap.remove(prefix);
        value2PrefixMap.remove(value);
    }


    public String getValue(String prefix) {
    	prefix = adjustPrefix(prefix);
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
            	if (prefix.equals(":")) {
            		prefix = "";
            	}
                return prefix + uriString.substring(value.length(), uriString.length());
            }
        }
        return null;
    }
    
    private static String adjustPrefix(String prefix) {
    	if (!prefix.endsWith(":")) {
    		prefix  = prefix + ":";
    	}
    	return prefix;
    }
}
