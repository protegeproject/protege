package org.protege.editor.core.plugin;


import org.eclipse.core.runtime.IExtension;

import java.util.HashSet;
import java.util.Set;

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
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A helper class, that takes the chore out of
 * instantiating types of plugins.  This class should
 * be subclassed to load specific types of plugins.  Note
 * that these "plugin" are generally wrappers around Java
 * Plugin Framework <code>Extension</code> objects.
 */
public abstract class AbstractPluginLoader<E> {

    private String pluginId;

    private String extensionPointId;


    /**
     * Creates a loader that will load (a subset) of the plugins
     * that extend the specified plugin at the specified plugin
     * extension point.
     * @param pluginId         The base plugin that the plugins to be loaded
     *                         extend.
     * @param extensionPointId The extension point of the base plugin
     */
    public AbstractPluginLoader(String pluginId, String extensionPointId) {
        this.pluginId = pluginId;
        this.extensionPointId = extensionPointId;
    }


    /**
     * Gets the plugins
     * @return A <code>Set</code> containing the plugins
     */
    public Set<E> getPlugins() {
        PluginExtensionFilter filter = new PluginExtensionFilter(pluginId, extensionPointId, getExtensionMatcher());
        Set<E> result = new HashSet<E>();
        for (IExtension ext : filter.getExtensions()) {
            result.add(createInstance(ext));
        }
        return result;
    }


    /**
     * This method needs to be overriden to provide a
     * <code>PluginExtensionMatcher</code>, which is used to filter
     * the plugin extensions to a desired subset.
     */
    /**
     * This method needs to be overriden to provide a
     * <code>PluginExtensionMatcher</code>, which is used to filter
     * the plugin extensions to a desired subset.
     */
    protected PluginExtensionMatcher getExtensionMatcher() {
        return new DefaultPluginExtensionMatcher();
    }

    /**
     * This method needs to be overriden to create an instance
     * of the desired plugin, based on the plugin <code>Extension</code>
     * @param extension The <code>Extension</code> that describes the
     *                  Java Plugin Framework extension.
     * @return A plugin object (typically some sort of wrapper around
     *         the extension)
     */
    protected abstract E createInstance(IExtension extension);
}
