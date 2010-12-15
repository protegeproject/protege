package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protege.common.CommonProtegeProperties;
import org.protege.editor.core.ProtegeApplication;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyCatalogManager {
	public static final String CATALOG_NAME = "catalog-v001.xml";
	public static final String CATALOG_BACKUP_PREFIX = "catalog-backup-";
	
	public static final String TIMESTAMP        = "Timestamp";
	    
    private Map<File, XMLCatalog> localCatalogs = new HashMap<File, XMLCatalog>();
    
    private XMLCatalog activeCatalog;
    private File activeCatalogFolder;

    private List<CatalogEntryManager> entryManagers;
    
    private static void backup(File folder, File catalogFile) {
	    File backup;
	    int i = 0;
	    while ((backup = new File(folder, CATALOG_BACKUP_PREFIX + (i++) + ".xml")).exists()) {
	        ;
	    }
	    catalogFile.renameTo(backup);
	}
	
	public static File getCatalogFile(File folder) {
		return new File(folder, CATALOG_NAME);
	}
	
	/**
	 * this works for catalogs that are generated from a parse or created
	 * by the OntologyCatalogManager.
	 */
	public static File getCatalogFile(XMLCatalog catalog) {
		if (catalog.getXmlBaseContext() == null) {
			return  null;
		}
		File f = new File(catalog.getXmlBaseContext().getXmlBase());
		if (f.exists() && f.isDirectory())  {
		    f = getCatalogFile(f);
		}
		return f.exists() ? f : null;
	}

	public static File getGlobalCatalogFile() {
		return getCatalogFile(CommonProtegeProperties.getDataDirectory());
	}
		
	public OntologyCatalogManager() {
    	entryManagers = new ArrayList<CatalogEntryManager>();
    	CatalogEntryManagerLoader pluginLoader = new CatalogEntryManagerLoader();
    	for (CatalogEntryManagerPlugin plugin : pluginLoader.getPlugins()) {
    		try {
    			entryManagers.add(plugin.newInstance());
    		}
    		catch (Throwable t) {
    			ProtegeApplication.getErrorLog().logError(t);
    		}
    	}
    }
	
	public OntologyCatalogManager(List<? extends CatalogEntryManager> entryManagers) {
		this.entryManagers = new ArrayList<CatalogEntryManager>(entryManagers);
	}
	
	public List<CatalogEntryManager>  getCatalogEntryManagers() {
		return Collections.unmodifiableList(entryManagers);
	}
	
    public XMLCatalog ensureCatalogExists(File folder) {
		XMLCatalog catalog = null;
		File catalogFile = getCatalogFile(folder);
		boolean alreadyExists = catalogFile.exists();
		boolean modified = false;
		if (alreadyExists) {
			try {
				catalog = CatalogUtilities.parseDocument(catalogFile.toURI().toURL());
			}
			catch (Throwable e) {
				ProtegeApplication.getErrorLog().logError(e);
				backup(folder, catalogFile);
			}
		}
		if (catalog == null) {
			catalog = new XMLCatalog(folder.toURI());
			modified = true;
		}
		if (alreadyExists) {
			try {
				modified = modified | update(catalog);
			}
			catch (Throwable t) {
				ProtegeApplication.getErrorLog().logError(t);
			}
		}
		else {
			for (CatalogEntryManager entryManager : entryManagers) {
				try {
					modified = modified | entryManager.initializeCatalog(folder, catalog);
				}
				catch (Throwable t) {
					ProtegeApplication.getErrorLog().logError(t);
				}
			}
		}
		if (modified) {
			try {
				CatalogUtilities.save(catalog, catalogFile);
			}
			catch (IOException e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
		return catalog;
	}

	public URI getRedirect(URI original) {
    	URI redirect = null;
    	for (XMLCatalog catalog : getAllCatalogs()) {
    		redirect = CatalogUtilities.getRedirect(original, catalog);
    		if (redirect != null) {
    			break;
    		}
    	}
    	return redirect;
    }
    
    public boolean update(XMLCatalog catalog) throws IOException {
    	boolean modified = false;
    	for (Entry entry : catalog.getEntries()) {
    		for (CatalogEntryManager updater : entryManagers) {
    			if (updater.isSuitable(entry)) {
    				modified = modified | updater.update(entry);
    			}
    		}
    	}
    	return modified;
    }
    
    public Collection<XMLCatalog> getLocalCatalogs() {
    	return localCatalogs.values();
    }
    
    public List<XMLCatalog> getAllCatalogs() {
    	List<XMLCatalog> catalogs = new ArrayList<XMLCatalog>();
    	catalogs.addAll(getLocalCatalogs());
    	return catalogs;
    }
    
    public XMLCatalog getActiveCatalog() {
    	return activeCatalog;
    }
    
    public File getActiveCatalogFolder() {
		return activeCatalogFolder;
	}
    
    public XMLCatalog addFolder(File dir) {
        XMLCatalog lib = localCatalogs.get(dir);
        // Add the parent file which will be the folder
        if (lib == null) {
            // Add automapped library
        	lib = ensureCatalogExists(dir);
        	localCatalogs.put(dir, lib);
        }
        activeCatalog = lib;
        activeCatalogFolder = dir;
        return lib;
    }
    
    public void reloadFolder(File dir) throws MalformedURLException, IOException {
    	localCatalogs.remove(dir);
    	localCatalogs.put(dir, CatalogUtilities.parseDocument(getCatalogFile(dir).toURI().toURL()));
    	activeCatalog = localCatalogs.get(activeCatalogFolder);
    }
}
