package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.protege.common.CommonProtegeProperties;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.protege.xmlcatalog.entry.UriEntry;


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
	
    private static final Logger logger = Logger.getLogger(OntologyCatalogManager.class);
    
    private XMLCatalog globalCatalog;
    
    private Map<File, XMLCatalog> localCatalogs = new HashMap<File, XMLCatalog>();
    
    private XMLCatalog activeCatalog;

    private List<OntologyGroupManager> updaters;
    
    private FolderGroupManager folderLibraryFactory;

    public static XMLCatalog ensureCatalogExists(File folder) {
		XMLCatalog catalog = null;
		File catalogFile = getCatalogFile(folder);
		if (catalogFile.exists()) {
			try {
				catalog = CatalogUtilities.parseDocument(catalogFile.toURI().toURL());
			}
			catch (IOException e) {
				ProtegeApplication.getErrorLog().logError(e);
				backup(folder, catalogFile);
			}
		}
		if (catalog == null) {
			catalog = new XMLCatalog(folder.toURI());
			catalog.setId("XML Catalog File (see http://www.oasis-open.org/committees/entity/spec-2001-08-06.html) - Created By Prot\u00E9g\u00E9 4");
			try {
				CatalogUtilities.save(catalog, catalogFile);
			}
			catch (IOException e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
		return catalog;
	}

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

	public static File getGlobalCatalogFile() {
		return getCatalogFile(CommonProtegeProperties.getDataDirectory());
	}
		
	public OntologyCatalogManager() {
		reloadGlobalCatalog();
    	// TODO replace with plugin mechanism here!
    	updaters = new ArrayList<OntologyGroupManager>();
    	folderLibraryFactory = new FolderGroupManager();
    	updaters.add(folderLibraryFactory);
    }
	
	public void reloadGlobalCatalog() {
    	globalCatalog = ensureCatalogExists(CommonProtegeProperties.getDataDirectory());
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
    
    public boolean update(XMLCatalog catalog, File location) throws IOException {
    	boolean modified = false;
    	for (Entry entry : catalog.getEntries()) {
    		if (entry instanceof GroupEntry) {
    			GroupEntry ge = (GroupEntry) entry;
    			for (OntologyGroupManager updater : updaters) {
    				if (updater.isSuitable(ge)) {
    					modified = modified | updater.update(ge, location.lastModified());
    				}
    			}
    		}
    	}
    	if (modified) {
    		CatalogUtilities.save(catalog, location);
    	}
    	return modified;
    }
    
    public XMLCatalog getGlobalCatalog() {
    	return globalCatalog;
    }
    
    public Collection<XMLCatalog> getLocalCatalogs() {
    	return localCatalogs.values();
    }
    
    public List<XMLCatalog> getAllCatalogs() {
    	List<XMLCatalog> catalogs = new ArrayList<XMLCatalog>();
    	catalogs.add(globalCatalog);
    	catalogs.addAll(getLocalCatalogs());
    	return catalogs;
    }
    
    public XMLCatalog getActiveCatalog() {
    	return activeCatalog;
    }
    
    public XMLCatalog addFolder(File dir) {
        XMLCatalog lib = localCatalogs.get(dir);
        // Add the parent file which will be the folder
        if (lib == null) {
            // Add automapped library
            try {
            	lib = folderLibraryFactory.ensureFolderCatalogExists(dir);
                localCatalogs.put(dir, lib);
            }
            catch (IOException ioe) {
                ProtegeApplication.getErrorLog().logError(ioe);
                logger.error("Could not look for possible imports in the directory " + dir);
            }
        }
        activeCatalog = lib;
        return lib;
    }
    
    public XMLCatalog removeFolder(File dir) {
    	return localCatalogs.remove(dir);
    }
    
    public void reloadFolder(File dir) throws MalformedURLException, IOException {
    	localCatalogs.remove(dir);
    	localCatalogs.put(dir, CatalogUtilities.parseDocument(getCatalogFile(dir).toURI().toURL()));
    }
    
    public FolderGroupManager getFolderOntologyLibraryBuilder() {
    	return folderLibraryFactory;
    }


}
