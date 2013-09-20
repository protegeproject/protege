package org.protege.editor.owl.model.library;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.ProtegeOWL;

public class CatalogEntryManagerLoader extends AbstractPluginLoader<CatalogEntryManagerPlugin> {
	
	public CatalogEntryManagerLoader() {
		super(ProtegeOWL.ID, CatalogEntryManagerPlugin.ID);
	}

	@Override
	protected CatalogEntryManagerPlugin createInstance(IExtension extension) {
		return new CatalogEntryManagerPlugin(extension);
	}

}
