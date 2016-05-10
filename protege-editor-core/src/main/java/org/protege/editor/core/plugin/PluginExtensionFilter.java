package org.protege.editor.core.plugin;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A utility class to obtain a set of plugin
 * <code>Extension</code>s that conform to some kind
 * of structure.
 */
public class PluginExtensionFilter {

    private final Logger logger = LoggerFactory.getLogger(PluginExtensionFilter.class.getName());

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
        Set<IExtension> result = new HashSet<>();
        IExtensionRegistry registry = PluginUtilities.getInstance().getExtensionRegistry();
        IExtensionPoint extpt = registry.getExtensionPoint(pluginId, extensionPointId);
        if(extpt == null) {
            logger.warn("Extension point not defined: {}@{}", extensionPointId, pluginId);
            return Collections.emptySet();
        }
        IExtension [] extensions = extpt.getExtensions();
        if(extensions == null) {
            return Collections.emptySet();
        }
        for (IExtension ext : extensions) {
            // check this extension has an identifier
            if (extensionMatcher.matches(ext)){
                if (ext.getUniqueIdentifier() != null){
                    result.add(ext);
                }
                else{
                    logger.warn("No ID found for {} in bundle: {}", extensionPointId, ext.getContributor().getName());
                }
            }
        }
        return result;
    }
}
