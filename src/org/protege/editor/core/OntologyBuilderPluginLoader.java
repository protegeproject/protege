package org.protege.editor.core;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;

public class OntologyBuilderPluginLoader extends AbstractPluginLoader<OntologyBuilderPlugin> {
    
    public OntologyBuilderPluginLoader() {
        super(ProtegeApplication.ID, OntologyBuilderPlugin.ID);
    }

    @Override
    protected OntologyBuilderPlugin createInstance(IExtension extension) {
        return new OntologyBuilderPlugin(extension);
    }

}
