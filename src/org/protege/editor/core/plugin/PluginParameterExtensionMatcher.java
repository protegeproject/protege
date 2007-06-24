package org.protege.editor.core.plugin;



import java.util.HashMap;
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
 * Date: Mar 22, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of <code>PluginExtensionMatcher</code> that returns the
 * extensions that match a given set of parameters.
 */
public class PluginParameterExtensionMatcher implements PluginExtensionMatcher {


    private Map<String, String> keyValueMap;


    public PluginParameterExtensionMatcher() {
        this(new HashMap<String, String>());
    }


    /**
     * Initialises the match with the parameter values that are
     * required to match.
     * @param keyValueMap The <code>Map</code> that will be used to
     *                    initialise the values to be matched.
     */
    public PluginParameterExtensionMatcher(Map<String, String> keyValueMap) {
        this.keyValueMap = new HashMap<String, String>();
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
    public boolean matches(Extension extension) {
        for (String key : keyValueMap.keySet()) {
            Extension.Parameter param = extension.getParameter(key);
            if (param == null) {
                return false;
            }
            if (param.valueAsString().equals(keyValueMap.get(key)) == false) {
                return false;
            }
        }
        return true;
    }
}
