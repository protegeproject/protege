package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.library.OntologyGroupManager;
import org.protege.editor.owl.model.library.LibraryUtilities;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.Prefer;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.protege.xmlcatalog.owl.update.Algorithm;
import org.protege.xmlcatalog.owl.update.XMLCatalogUpdater;
import org.protege.xmlcatalog.owl.update.XmlBaseAlgorithm;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FolderGroupManager implements OntologyGroupManager {
	private static final Logger logger = Logger.getLogger(FolderGroupManager.class);
	
	public static final String ID_PREFIX = "Folder Repository";
	public static final String DIR_PROP = "directory";
	
    public static final String FILE_KEY = "FILE";
    
    private XMLCatalogUpdater updater;

    public FolderGroupManager() {
    	Set<Algorithm> algorithms = new HashSet<Algorithm>();
    	algorithms.add(new XmlBaseAlgorithm());
        updater = new XMLCatalogUpdater();
        updater.setAlgorithms(algorithms);
    }
    
	@Override
	public boolean isSuitable(GroupEntry ge) {
		String dirName = LibraryUtilities.getStringProperty(ge, DIR_PROP);
		return ge.getId() != null 
					&& ge.getId().startsWith(ID_PREFIX)
					&& dirName != null
					&& new File(dirName).exists()
					&& new File(dirName).isDirectory();
	}

	@Override
	public boolean update(GroupEntry ge, long lastModifiedDate) {
		File folder = new File(LibraryUtilities.getStringProperty(ge, DIR_PROP));
		return updater.update(folder, ge, lastModifiedDate);
	}

	@Override
	public GroupEntry openGroupEntryDialog(XmlBaseContext context) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getDescription(GroupEntry ge) {
		return "Folder Repository for " + LibraryUtilities.getStringProperty(ge, DIR_PROP);
	}

	public XMLCatalog ensureFolderCatalogExists(File folder) throws IOException {
		XMLCatalog catalog = OntologyCatalogManager.ensureCatalogExists(folder);
		File catalogFile = OntologyCatalogManager.getCatalogFile(folder);
		GroupEntry ge = null;
		for (Entry e : catalog.getEntries()) {
			if (e instanceof GroupEntry) {
				if (folder.getCanonicalPath().equals(LibraryUtilities.getStringProperty((GroupEntry) e, DIR_PROP))) {
					ge = (GroupEntry) e;
					break;
				}
			}
		}
		if (ge == null) {
			ge = createGroupEntry(folder, catalog);
			catalog.addEntry(ge);
			update(ge, -1);
			CatalogUtilities.save(catalog, catalogFile);
		}
		else if (update(ge, catalogFile.lastModified())) {
			CatalogUtilities.save(catalog, catalogFile);
		}
		return catalog;
	}
	
	public GroupEntry createGroupEntry(File folder, XmlBaseContext context) throws IOException {
		String id = ID_PREFIX + ", " + DIR_PROP + "=" + folder.getCanonicalPath();
		return new GroupEntry(id, context, Prefer.PUBLIC, folder.toURI());
	}

    
}
