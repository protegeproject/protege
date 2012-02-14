package org.protege.editor.owl.model.library.folder;

import org.protege.editor.owl.model.library.LibraryUtilities;
import org.protege.xmlcatalog.entry.Entry;

public class ImportByNameManager extends FolderGroupManager {

	@Override
	public boolean update(Entry entry) {
		setAlgorithms(new OntologyNameAlgorithm());
		super.update(entry);
		String dir = LibraryUtilities.getStringProperty(entry, DIR_PROP);
		
		return true;
	}
}
