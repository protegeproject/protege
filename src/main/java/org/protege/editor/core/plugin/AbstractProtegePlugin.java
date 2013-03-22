package org.protege.editor.core.plugin;

import org.eclipse.core.runtime.IExtension;

public abstract class AbstractProtegePlugin<P extends ProtegePluginInstance> implements ProtegePlugin<P> {
	protected IExtension extension;
	
	protected AbstractProtegePlugin(IExtension extension) {
		this.extension = extension;
	}
	
	public IExtension getIExtension() {
		return extension;
	}

    public String getId() {
        return extension.getUniqueIdentifier();
    }
    
    public String getLabel() {
        return extension.getLabel();
    }
    

    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }

	public P newInstance() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
        ExtensionInstantiator<P> instantiator = new ExtensionInstantiator<P>(extension);
        return instantiator.instantiate();
	}
	
	protected String getPluginProperty(String key) {
	    return PluginProperties.getParameterValue(extension, key, null);
	}
	
	protected String getPluginProperty(String key, String defaultValue) {
        return PluginProperties.getParameterValue(extension, key, defaultValue);
	}

}
