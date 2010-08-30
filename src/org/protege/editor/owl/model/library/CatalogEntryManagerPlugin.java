package org.protege.editor.owl.model.library;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;

public class CatalogEntryManagerPlugin extends AbstractProtegePlugin<CatalogEntryManager> {
	public static final String ID = "repository";
	
	public CatalogEntryManagerPlugin(IExtension extension) {
		super(extension);
	}

}
