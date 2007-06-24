package org.protege.editor.core.plugin;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;




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
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class to obtain a set of plugin
 * <code>Extension</code>s that conform to some kind
 * of structure.
 */
public class PluginExtensionFilter {

    private String pluginId;

    private String extensionPointId;

    private PluginExtensionMatcher extensionMatcher;


    /**
     * Creates a <code>PluginExtensionFilter</code> that can be used
     * to obtain a set of <code>Extensions</code> that extend a given
     * extension point on a specific plugin.
     * @param pluginId         The plugin that the extension points will extend
     * @param extensionPointId The id of the plugin extension point
     * @param extensionMatcher A matcher that will be used to filter extensions.  The resulting
     *                         set of extensions will contain the extension for which the matcher returned <code>true</code>
     */
    public PluginExtensionFilter(String pluginId, String extensionPointId, PluginExtensionMatcher extensionMatcher) {
        this.pluginId = pluginId;
        this.extensionPointId = extensionPointId;
        this.extensionMatcher = extensionMatcher;
    }


    /**
     * Gets the <code>Extension</code>s that extend the specified extension point of
     * the specified plugin and match the criteria specified by the
     * <code>PluginExtensionMatcher</code>.
     */
    public Set<IExtension> getExtensions() {
        Set<IExtension> result = new HashSet<IExtension>();
        IExtensionRegistry registry = PluginUtilities.getInstance().getExtensionRegistry();
        IExtensionPoint extpt = registry.getExtensionPoint(pluginId, extensionPointId);
        for (IExtension ext : extpt.getExtensions()) {
            if (extensionMatcher.matches(ext)) {
                result.add(ext);
            }
        }
        return result;
    }
}
