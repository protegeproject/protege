package org.protege.editor.owl.ui.prefix;

import java.net.URI;
import java.util.Map;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface PrefixMapper {

    /**
     * For the given value, gets the prefix that maps
     * to this value.
     * @param value The value whose prefix is to be determined.
     * @return The prefix that maps to the specified value, or
     *         <code>null</code> if there isn't a prefix that maps to
     *         this value.
     */
    public String getPrefix(String value);


    /**
     * Adds a prefix-value mapping
     * @param prefix The prefix to map to the specified value
     * @param value  The value that th prefix should be mapped to.
     */
    public boolean addPrefixMapping(String prefix, String value);


    /**
     * Removes a previously added value.
     * @param prefix The prefix whose value should be removed
     */
    public void removePrefixMapping(String prefix);


    /**
     * Gets a value that the specified prefix maps to.
     * @param prefix The prefix whose value is to be determined.
     * @return The value corresponding to the prefix, or <code>null</code>
     *         if there isn't a mapping for the specified prefix.
     */
    public String getValue(String prefix);


    /**
     * Gets the set of all prefixes for which there are mappings.
     */
    public Set<String> getPrefixes();


    /**
     * Gets the set of all values for which there are prefixes.
     */
    public Set<String> getValues();


    /**
     * Gets a copy of the prefix map held by this mapper.
     */
    public Map<String, String> getPrefixMap();


    public String getShortForm(URI uri);
}
