package org.protege.editor.core.plugin;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;

import java.awt.*;
import java.util.HashSet;
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
 * Date: Mar 22, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginProperties {
    
    public static final String PLUGIN_XML_ID = "id";
    
    public static final String PLUGIN_XML_VALUE = "value";

    public static final String PROTEGE_PROPERTY_PREFIX = "@";

    /**
     * This is the name of the clsdescriptioneditor kit Id parameter, which is a "standard"
     * parameter.
     */
    public static final String EDITOR_KIT_PARAM_NAME = "editorKitId";

    /**
     * This is the name of the class that should be instantiated for a plugin,
     * which is a "standard" parameter.
     */
    public static final String CLASS_PARAM_NAME = "class";


    public static String getProtegeProperty(String key) {
        // The key should start with the prefix symbol.
        if (key.startsWith(PROTEGE_PROPERTY_PREFIX)) {
            return ProtegeProperties.getInstance().getProperty(key.substring(1, key.length()));
        }
        return null;
    }


    public static boolean isProtegeProperty(String key) {
        return key.startsWith(PROTEGE_PROPERTY_PREFIX);
    }


    public static String getParameterValue(IExtension ext, String key, String defaultValue) {
        String val  = PluginUtilities.getAttribute(ext, key);
        if (val == null) {
            return defaultValue;
        }
        if (isProtegeProperty(val)) {
            return getProtegeProperty(val);
        }
        else {
            return val;
        }
    }


    public static Set<String> getParameterValues(IExtension ext, String key) {
        Set<String> values = new HashSet<String>();
        for (IConfigurationElement config : ext.getConfigurationElements()) {
            if (key.equals(config.getAttribute(PLUGIN_XML_ID))) {
                String val = config.getAttribute(PLUGIN_XML_VALUE);
                if (val == null) continue;
                if (isProtegeProperty(val)) {
                    String protegeVal = getProtegeProperty(val);
                    if (protegeVal != null) {
                        values.add(protegeVal);
                    }
                }
                else {
                    values.add(val);
                }
            }  
        }
        return values;
    }


    public static boolean getBooleanParameterValue(IExtension ext, String key, boolean defaultValue) {
        return PropertyUtil.getBoolean(getParameterValue(ext, key, Boolean.toString(defaultValue)), defaultValue);
    }


    public static Color getColorParameterValue(IExtension ext, String key, Color defaultColor) {
        return PropertyUtil.getColor(getParameterValue(ext, key, null), defaultColor);
    }
}
