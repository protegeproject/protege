package org.protege.editor.core.plugin;


import org.eclipse.core.runtime.IExtension;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * An implementation of <code>PluginExtensionMatcher</code> that returns the
 * extensions that match a given set of parameters.
 */
public class PluginParameterExtensionMatcher implements PluginExtensionMatcher {


    private Map<String, String> keyValueMap;


    public PluginParameterExtensionMatcher() {
        this(new HashMap<>());
    }


    /**
     * Initialises the match with the parameter values that are
     * required to match.
     * @param keyValueMap The <code>Map</code> that will be used to
     *                    initialise the values to be matched.
     */
    public PluginParameterExtensionMatcher(Map<String, String> keyValueMap) {
        this.keyValueMap = new HashMap<>();
        this.keyValueMap.putAll(keyValueMap);
    }


    /**
     * Adds a paramter value that must be matched
     * @param parameter The name of the paramter to be matched.
     * @param value     The values of the parameter that must be matched.
     */
    public void put(String parameter, String value) {
        this.keyValueMap.put(parameter, value);
    }


    /**
     * Determines whether the specified <code>Extension</code>
     * constitutes a "match" or not.
     * @param extension The <code>Extension</code> to test.
     * @return <code>true</code> if the <code>Extension</code> matches
     *         or <code>false</code> if the <code>Extension</code> doesn't match.
     */
    public boolean matches(IExtension extension) {
        for (String key : keyValueMap.keySet()) {
            String value = PluginUtilities.getAttribute(extension, key);
            if (value == null)  return false;
            if (!value.equals(keyValueMap.get(key))) return false;
        }
        return true;
    }
}
