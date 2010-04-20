package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.library.LibraryUtilities;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.library.OntologyGroupManager;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.Prefer;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.XmlBaseContext;
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
public class FolderGroupManager implements OntologyGroupManager {
	private static final Logger logger = Logger.getLogger(FolderGroupManager.class);
	
	public static final String ID_PREFIX = "Folder Repository";
	public static final String DIR_PROP = "directory";
    public static final String DUPLICATE_SCHEME = "duplicate:";

	
    public static final String FILE_KEY = "FILE";
    
    private Set<Algorithm> algorithms;
    
    /*
     * parameters used by the non-reentrant update routine
     */
    private GroupEntry ge;
    private File folder;
    private long timeOfCurrentUpdate;
    private boolean modified = false;
    private Map<URI, Set<File>> importDeclToFileMap = new HashMap<URI, Set<File>>();
    
    public FolderGroupManager() {
        algorithms = new HashSet<Algorithm>();
    	algorithms.add(new XmlBaseAlgorithm());
    }
    
	public boolean isSuitable(GroupEntry ge) {
		String dirName = LibraryUtilities.getStringProperty(ge, DIR_PROP);
		String enabledName = LibraryUtilities.getStringProperty(ge, OntologyCatalogManager.AUTO_UPDATE_PROP);
		return ge.getId() != null 
					&& ge.getId().startsWith(ID_PREFIX)
					&& (enabledName == null || enabledName.toLowerCase().equals("true"))
					&& dirName != null
					&& new File(dirName).exists()
					&& new File(dirName).isDirectory();
	}

	public GroupEntry openGroupEntryDialog(XmlBaseContext context) {
	    throw new UnsupportedOperationException("Not implemented yet");
	}
	
	public String getDescription(GroupEntry ge) {
		return "Folder Repository for " + LibraryUtilities.getStringProperty(ge, DIR_PROP);
	}

	public XMLCatalog ensureFolderCatalogExists(File folder) throws IOException {
	    File catalogFile = OntologyCatalogManager.getCatalogFile(folder);
	    boolean createFolderGroup = !catalogFile.exists();
		XMLCatalog catalog = OntologyCatalogManager.ensureCatalogExists(folder);
		GroupEntry ge = null;
		for (Entry e : catalog.getEntries()) {
		    if (e instanceof GroupEntry) {
		        if (folder.getCanonicalPath().equals(LibraryUtilities.getStringProperty((GroupEntry) e, DIR_PROP))) {
		            ge = (GroupEntry) e;
		            break;
		        }
		    }
		}
		if (ge == null && createFolderGroup) {
		    ge = createGroupEntry(folder, catalog);
		    catalog.addEntry(ge);
		    update(ge);
		    CatalogUtilities.save(catalog, catalogFile);
		}
		else if (ge != null && isSuitable(ge) && update(ge)) {
		    CatalogUtilities.save(catalog, catalogFile);
		}
		return catalog;
	}
	
	public GroupEntry createGroupEntry(File folder, XmlBaseContext context) throws IOException {
		String id = ID_PREFIX + ", " + DIR_PROP + "=" + folder.getCanonicalPath() + ", " + OntologyCatalogManager.AUTO_UPDATE_PROP + "=true";
		return new GroupEntry(id, context, Prefer.PUBLIC, folder.toURI());
	}
	
	public boolean update(GroupEntry ge) {
        modified = false;
        importDeclToFileMap.clear();
        this.ge = ge;
        folder = new File(LibraryUtilities.getStringProperty(ge, DIR_PROP));
        timeOfCurrentUpdate = System.currentTimeMillis();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("********************************* Starting Catalog Update ************************************************");
                logger.debug("Update of group entry " + ge.getId() + " started at " + new Date(timeOfCurrentUpdate));
            }
    
            Set<File> examined = handleFreshEntries();
            handleDiskEntries(examined);
            rebuild();
            if (logger.isDebugEnabled()) {
                logger.debug("********************************* Catalog Update Complete ************************************************");

            }
            return modified;
        }
        finally {
            importDeclToFileMap.clear();
            this.ge = null;
            folder = null;
        }
    }
	
	private void addMapping(URI webLocation, File physicalLocation) {
	    if (logger.isDebugEnabled()) {
	        logger.debug("\"import " + webLocation + "\" --> " + physicalLocation);
	    }
	    Set<File> physicalLocations = importDeclToFileMap.get(webLocation);
	    if (physicalLocations == null) {
	        physicalLocations = new HashSet<File>();
	        importDeclToFileMap.put(webLocation, physicalLocations);
	    }
	    physicalLocations.add(physicalLocation);
	}
	
	private static File getCanonicalFile(File f) {
	    try {
	        return f.getCanonicalFile();
	    }
	    catch (IOException e) {
	        logger.info("Could not find canonical path for file " + f);
	        return f;
	    }
	}
	   
    private Set<File> handleFreshEntries() {
        Set<File> examined = new HashSet<File>();

        for (Entry e : ge.getEntries()) {
            if (e instanceof UriEntry) {
                UriEntry ue = (UriEntry) e;
                try {
                    long lastUpdated = -1;
                    String updatedString = LibraryUtilities.getStringProperty(ue, OntologyCatalogManager.TIMESTAMP);
                    try {
                        if (updatedString != null) {
                            lastUpdated = Long.parseLong(updatedString);
                        }
                    }
                    catch (NumberFormatException nfe) {
                        logger.info("Could not parse timestamps in catalog file " + nfe);
                    }
                    File f = new File(ue.getAbsoluteURI());
                    if (f.exists() && f.lastModified() < lastUpdated) {
                        examined.add(getCanonicalFile(f));
                        URI webLocation = URI.create(ue.getName());
                        addMapping(webLocation, f);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Mapping kept!");
                        }
                    }
                    else {
                        modified = true; // an entry will be deleted.
                        if (logger.isDebugEnabled()) {
                            logger.debug("Map for file " + f + " is stale and will be removed");
                        }
                    }
                }
                catch (Throwable t) {
                    logger.info("Exception caught updating catalog entry " + t);
                }
            }
        }
        return examined;
    }
    
    
    private  void handleDiskEntries(Set<File> examined) {
        if (algorithms == null || algorithms.isEmpty()) {
            return;
        }
        for (File physicalLocation : folder.listFiles()) {
            if (physicalLocation.exists() 
                    && physicalLocation.isFile() 
                    && physicalLocation.getPath().endsWith(".owl")
                    && !physicalLocation.getName().startsWith(".")
                    && !examined.contains(getCanonicalFile(physicalLocation))) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Applying algorithms to " + physicalLocation);
                }
                for (Algorithm algorithm : algorithms) {
                    Set<URI> webLocations = algorithm.getSuggestions(physicalLocation);
                    for (URI webLocation : webLocations) {
                        addMapping(webLocation, physicalLocation);
                        modified = true;
                    }
                }
            }
        }
    }
    
    private void rebuild() {
        if (!modified) {
            return;
        }
        for (Entry e : ge.getEntries()) {
            ge.removeEntry(e);
        }
        for (java.util.Map.Entry<URI, Set<File>> entry : importDeclToFileMap.entrySet()) {
            URI webLocation = entry.getKey();
            Set<File> diskLocations = entry.getValue();
            if (diskLocations == null || diskLocations.isEmpty()) {
                continue;
            }
            boolean duplicatesFound = (diskLocations.size() != 1);
            for (File physicalLocation : diskLocations) {
                URI shortLocation = folder.toURI().relativize(physicalLocation.toURI());
                UriEntry u = new UriEntry("Automatically generated entry, " + OntologyCatalogManager.TIMESTAMP + "=" + timeOfCurrentUpdate, 
                                          ge, 
                                          (duplicatesFound ? DUPLICATE_SCHEME : "") + webLocation.toString(),
                                          shortLocation,
                                          null);
                ge.addEntry(u);
            }
        }
    }



}
