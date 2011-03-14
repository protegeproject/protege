package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.library.CatalogEntryManager;
import org.protege.editor.owl.model.library.LibraryUtilities;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.editor.owl.ui.library.plugins.FolderGroupPanel;
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
public class FolderGroupManager extends CatalogEntryManager {
	private static final Logger logger = Logger.getLogger(FolderGroupManager.class);
	
	public static final int FOLDER_BY_URI_VERSION=1;
	public static final int CURRENT_VERSION = 1;
	
	public static final String ID_PREFIX = "Folder Repository";
	public static final String DIR_PROP = "directory";
	public static final String RECURSIVE_PROP = "recursive";
	public static final String DUPLICATE_SCHEME="duplicate:";
	
    public static final String FILE_KEY = "FILE";
    
    private Set<Algorithm> algorithms;
    private boolean autoUpdate = true;
    private boolean warnedUserOfBadRepositoryDeclaration = false;
    
    /*
     * parameters used by the non-reentrant update routine
     */
    private GroupEntry ge;
    private File folder;
    private boolean recursive = true;
    private long timeOfCurrentUpdate;
    private boolean modified = false;
    private Set<File> examined = new HashSet<File>();
    private Map<URI, Collection<URI>> webLocationToFileLocationMap = new TreeMap<URI, Collection<URI>>();
    

    public static GroupEntry createGroupEntry(URI folder, boolean recursive, boolean autoUpdate, XmlBaseContext context) throws IOException {
        StringBuffer sb = new StringBuffer(ID_PREFIX);
        LibraryUtilities.addPropertyValue(sb, DIR_PROP, folder.toString());
        LibraryUtilities.addPropertyValue(sb, RECURSIVE_PROP, recursive);
        LibraryUtilities.addPropertyValue(sb, LibraryUtilities.AUTO_UPDATE_PROP, autoUpdate ? "true" : "false");
        LibraryUtilities.addPropertyValue(sb, LibraryUtilities.VERSION_PROPERTY, CURRENT_VERSION);
        return new GroupEntry(sb.toString(), context, Prefer.PUBLIC, folder);
    }
    
    public FolderGroupManager() {
        algorithms = new HashSet<Algorithm>();
    	algorithms.add(new XmlBaseAlgorithm());
    }
    
    public void setAlgorithms(Algorithm... algorithms) {
    	this.algorithms.clear();
    	for (Algorithm algorithm : algorithms) {
    		this.algorithms.add(algorithm);
    	}
    }
    
    public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
    
    public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	public boolean isSuitable(Entry entry) {
		if  (!(entry instanceof GroupEntry)) {
			return false;
		}
		GroupEntry ge = (GroupEntry) entry;
		File dir = getDirectory(ge);
		
		boolean enabled = LibraryUtilities.getBooleanProperty(ge, LibraryUtilities.AUTO_UPDATE_PROP, false);
		boolean hasRightType = ge.getId() != null 
		                            && ge.getId().startsWith(ID_PREFIX)
		                            && enabled
		                            && dir != null;

		if (hasRightType && (!dir.exists() || !dir.isDirectory())) {
		    logger.warn("Folder repository probably came from another system");
		    logger.warn("Could not be updated because directory " + dir  + " does not exist");
		    if (!warnedUserOfBadRepositoryDeclaration) {
		        ProtegeApplication.getErrorLog().logError(new IOException("Bad ontology library declaration - check logs. Warnings now disabled for this session."));
		        warnedUserOfBadRepositoryDeclaration = true;
		    }
		    return false;
		}
		return hasRightType;
	}

	public boolean update(Entry entry) {
        this.ge = (GroupEntry) entry;
        reset();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("********************************* Starting Catalog Update ************************************************");
                logger.debug("Update of group entry " + ge.getId() + " started at " + new Date(timeOfCurrentUpdate));
            }
    
            removeStaleEntries();
            examineDiskEntries(folder);
            writeEntries();
            if (logger.isDebugEnabled()) {
                logger.debug("********************************* Catalog Update Complete ************************************************");

            }
            return modified;
        }
        finally {
            this.ge = null;
            reset();
        }
    }
	
	private void reset() {
		modified = false;
		timeOfCurrentUpdate = System.currentTimeMillis();
		examined.clear();
		webLocationToFileLocationMap.clear();
		if (ge != null) {
			folder = getDirectory(ge);
			recursive = LibraryUtilities.getBooleanProperty(ge, RECURSIVE_PROP, true);
		}
		else {
			folder = null;
		}
	}
	
	public boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException {
	    URI relativeFolderUri = CatalogUtilities.relativize(folder.toURI(), catalog);
	    ge = FolderGroupManager.createGroupEntry(relativeFolderUri, true, autoUpdate, catalog);
	    catalog.addEntry(ge);
	    update(ge);
	    return true;
	}
	
	public NewEntryPanel newEntryPanel(XMLCatalog catalog) {
	    return new FolderGroupPanel(catalog);
	}
	
	public String getDescription() {
	    return "Folder Repository";
	}

	public String getDescription(Entry ge) {
	    StringBuffer sb = new StringBuffer("<html><body><b>Folder Repository for ");
	    sb.append(getDirectory((GroupEntry) ge));
	    sb.append("</b>");
		if (LibraryUtilities.getBooleanProperty(ge, RECURSIVE_PROP, true)) {
		    sb.append(" <font color=\"gray\">(Recursive)</font>");
		}
		sb.append("</body></html>");
		return sb.toString();
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
	   
    private void removeStaleEntries() {
        for (Entry e : new ArrayList<Entry>(ge.getEntries())) {
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
                    if (!f.exists() || f.lastModified() >= lastUpdated) {
                        ge.removeEntry(ue);
                        modified = true;
                        if (logger.isDebugEnabled()) {
                            logger.debug("Map for file " + f + " is stale and has been removed");
                        }
                    }
                    else {
                        examined.add(getCanonicalFile(f));
                        if (logger.isDebugEnabled()) {
                            logger.debug("Map for file " + f + " is still good and will be kept");
                        }
                    }
                }
                catch (Throwable t) {
                    logger.info("Exception caught updating catalog entry " + t);
                }
            }
        }
    }
    
    
    private  void examineDiskEntries(File tree) {
        if (algorithms == null || algorithms.isEmpty()) {
            return;
        }
        Set<File> subFolders = new HashSet<File>();
        for (File physicalLocation : tree.listFiles()) {
        	if (recursive && physicalLocation.exists() && physicalLocation.isDirectory()) {
        	    subFolders.add(physicalLocation);
        	}
        	else if (physicalLocation.exists() 
                    && physicalLocation.isFile() 
                    && physicalLocation.getPath().endsWith(".owl")
                    && !physicalLocation.getName().startsWith(".")
                    && !examined.contains(getCanonicalFile(physicalLocation))) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Applying algorithms to " + physicalLocation);
                }
    			URI shortLocation = folder.toURI().relativize(physicalLocation.toURI());
                for (Algorithm algorithm : algorithms) {
                    Set<URI> webLocations = algorithm.getSuggestions(physicalLocation);
                    for (URI webLocation : webLocations) {
                    	recordEntry(webLocation, shortLocation);
                    }
                }
            }
        }
        for (File physicalLocation : subFolders) {
            examineDiskEntries(physicalLocation);
        }
    }
    
    private void recordEntry(URI webLocation, URI physicalLocation)	{ 
    	Collection<URI> possibleLocations = webLocationToFileLocationMap.get(webLocation);
    	if (possibleLocations == null) {
    		possibleLocations = new ArrayList<URI>();
    		webLocationToFileLocationMap.put(webLocation, possibleLocations);
    	}
    	possibleLocations.add(physicalLocation);
    }
    
    private void writeEntries() {
    	List<URI> duplicates = new ArrayList<URI>();
    	for (URI webLocation : webLocationToFileLocationMap.keySet()) {
    		Collection<URI> physicalLocations = webLocationToFileLocationMap.get(webLocation);
    		if (physicalLocations.size() > 1) {
    			duplicates.add(webLocation);
    			continue;
    		}
    		writeEntries(webLocation, physicalLocations);
    	}
    	for (URI webLocation : duplicates) {
    		Collection<URI> physicalLocations = webLocationToFileLocationMap.get(webLocation);
			try {
				webLocation = new URI(DUPLICATE_SCHEME + webLocation.toString());
			}
			catch (URISyntaxException use) {
				ProtegeApplication.getErrorLog().logError(use);
				continue;
			}
    		writeEntries(webLocation, physicalLocations);
    	}
    }
    
    private void writeEntries(URI webLocation, Collection<URI> physicalLocations) {
		for (URI physicalLocation : physicalLocations) {
			String entryId = "Automatically generated entry, " + OntologyCatalogManager.TIMESTAMP + "=" + timeOfCurrentUpdate;
			UriEntry u = new UriEntry(entryId, 
								      ge, 
								      webLocation.toString(),
								      physicalLocation,
								      null);
			ge.addEntry(u);
			modified = true;
		}
    }
    
    private File getDirectory(GroupEntry ge) {
        File folder = null;
        String dirName = LibraryUtilities.getStringProperty(ge, DIR_PROP);
        if (dirName == null) {
            return null;
        }
        if (LibraryUtilities.getVersion(ge) < FOLDER_BY_URI_VERSION) {
            folder = new File(dirName);
        }
        else {
            URI dirURI = CatalogUtilities.resolveXmlBase(ge).resolve(dirName);
            folder = new File(dirURI);
        }
        return folder;
    }

}
