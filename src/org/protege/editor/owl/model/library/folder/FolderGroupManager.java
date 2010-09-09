package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
	public static final String DEPRECATED_DUPLICATE_SCHEME="duplicate:";
	
    public static final String FILE_KEY = "FILE";
    
    private Set<Algorithm> algorithms;
    private boolean warnedUserOfBadRepositoryDeclaration = false;
    
    /*
     * parameters used by the non-reentrant update routine
     */
    private GroupEntry ge;
    private File folder;
    private boolean recursive = true;
    private long timeOfCurrentUpdate;
    private boolean modified = false;
    

    public static GroupEntry createGroupEntry(URI folder, boolean recursive, XmlBaseContext context) throws IOException {
        StringBuffer sb = new StringBuffer(ID_PREFIX);
        LibraryUtilities.addPropertyValue(sb, DIR_PROP, folder.toString());
        LibraryUtilities.addPropertyValue(sb, RECURSIVE_PROP, recursive);
        LibraryUtilities.addPropertyValue(sb, LibraryUtilities.AUTO_UPDATE_PROP, "true");
        LibraryUtilities.addPropertyValue(sb, LibraryUtilities.VERSION_PROPERTY, CURRENT_VERSION);
        return new GroupEntry(sb.toString(), context, Prefer.PUBLIC, folder);
    }
    
    public FolderGroupManager() {
        algorithms = new HashSet<Algorithm>();
    	algorithms.add(new XmlBaseAlgorithm());
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
        modified = false;
        this.ge = (GroupEntry) entry;
        folder = getDirectory(ge);
        recursive = LibraryUtilities.getBooleanProperty(entry, RECURSIVE_PROP, true);
        timeOfCurrentUpdate = System.currentTimeMillis();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("********************************* Starting Catalog Update ************************************************");
                logger.debug("Update of group entry " + ge.getId() + " started at " + new Date(timeOfCurrentUpdate));
            }
    
            removeOldDuplicates();
            Set<File> examined = handleStaleEntries();
            handleDiskEntries(folder, examined);
            if (logger.isDebugEnabled()) {
                logger.debug("********************************* Catalog Update Complete ************************************************");

            }
            return modified;
        }
        finally {
            this.ge = null;
            folder = null;
        }
    }
	
	public boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException {
	    URI relativeFolderUri = CatalogUtilities.relativize(folder.toURI(), catalog);
	    ge = FolderGroupManager.createGroupEntry(relativeFolderUri, true, catalog);
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
	
	private void removeOldDuplicates() {
	    for (Entry e : new ArrayList<Entry>(ge.getEntries())) {
            if (e instanceof UriEntry) {
                UriEntry ue = (UriEntry) e;
                if (ue.getName().startsWith(DEPRECATED_DUPLICATE_SCHEME)) {
                    ge.removeEntry(ue);
                }
            }
	    }
	}
	   
    private Set<File> handleStaleEntries() {
        Set<File> examined = new HashSet<File>();

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
                        modified = true; // an entry will be deleted.
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
        return examined;
    }
    
    
    private  void handleDiskEntries(File tree, Set<File> examined) {
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
                for (Algorithm algorithm : algorithms) {
                    Set<URI> webLocations = algorithm.getSuggestions(physicalLocation);
                    for (URI webLocation : webLocations) {
                        URI shortLocation = folder.toURI().relativize(physicalLocation.toURI());
                        String entryId = "Automatically generated entry, " + OntologyCatalogManager.TIMESTAMP + "=" + timeOfCurrentUpdate;
                        UriEntry u = new UriEntry(entryId, 
                                                  ge, 
                                                  webLocation.toString(),
                                                  shortLocation,
                                                  null);
                        ge.addEntry(u);
                        modified = true;
                    }
                }
            }
        }
        for (File physicalLocation : subFolders) {
            handleDiskEntries(physicalLocation, examined);
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
