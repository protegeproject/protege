package org.protege.editor.owl.model.library.folder;

import java.io.IOException;
import java.net.URI;

import org.protege.editor.owl.model.library.LibraryUtilities;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.Prefer;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;

public class ImportByNameManager extends FolderGroupManager {
	
	public static final String ID_PREFIX = "Import By Name Repository";
	
    public static GroupEntry createGroupEntry(URI folder, boolean recursive, boolean autoUpdate, XmlBaseContext context) throws IOException {
        return new GroupEntry(getIdString(ID_PREFIX, folder, recursive, autoUpdate), context, Prefer.PUBLIC, folder);
    }

	@Override
	public boolean update(Entry entry) {
		String dir = LibraryUtilities.getStringProperty(entry, FolderGroupManager.DIR_PROP);
		boolean recursive = LibraryUtilities.getBooleanProperty(entry, FolderGroupManager.RECURSIVE_PROP, true);
		setAlgorithms(new OntologyNameAlgorithm());
		super.update(entry);
		StringBuffer initializedId = new StringBuffer(ID_PREFIX);
		LibraryUtilities.addPropertyValue(initializedId, FolderGroupManager.DIR_PROP, dir);
		entry.setId(getIdString(getIdPrefix(), dir, recursive, false));  // turn off autoupdate
		return true;
	}
	
	public boolean initializeCatalog(java.io.File folder, org.protege.xmlcatalog.XMLCatalog catalog) throws IOException {
		return false;
	}
	
	@Override
	public NewEntryPanel newEntryPanel(XMLCatalog catalog) {
		return null;
	}
	
	@Override
	protected String getIdPrefix() {
		return ID_PREFIX;
	}
}
