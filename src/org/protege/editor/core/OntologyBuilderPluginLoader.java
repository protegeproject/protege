package org.protege.editor.core;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.DefaultPluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionMatcher;

public class OntologyBuilderPluginLoader extends AbstractPluginLoader<OntologyBuilderPlugin> {
    
    public OntologyBuilderPluginLoader() {
        super(ProtegeApplication.ID, OntologyBuilderPlugin.ID);
    }

    @Override
    protected OntologyBuilderPlugin createInstance(IExtension extension) {
        return new OntologyBuilderPlugin(extension);
    }

    @Override
    protected PluginExtensionMatcher getExtensionMatcher() {
        return new DefaultPluginExtensionMatcher();
    }

}
