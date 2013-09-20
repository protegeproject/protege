package org.protege.editor.owl.model.library;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;

public class CatalogEntryManagerPlugin extends AbstractProtegePlugin<CatalogEntryManager> {
	public static final String ID = "repository";
	
	public CatalogEntryManagerPlugin(IExtension extension) {
		super(extension);
	}
	
	@Override
	public CatalogEntryManager newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
	    CatalogEntryManager pluginInstance = super.newInstance();
	    pluginInstance.setId(getId());
	    return pluginInstance;
	}

}
