package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.protege.xmlcatalog.entry.UriEntry;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * 
 * This is the much simpler version of the FolderGroupManager without timestamps.  If the xmlbase algorithm
 * is used it appears that this version is fine and there is no need for the additional complexity of the timestamp
 * version.  But if an accurate ontology name is calculated then this version is too slow on large ontologies like the 
 * fma.  I am currently waiting on an rdf parse that is taking over six minutes.
 * 
 */
public class UntimeStampedFolderGroupManager implements OntologyGroupManager {
	private static final Logger logger = Logger.getLogger(UntimeStampedFolderGroupManager.class);
	
	public static final String ID_PREFIX = "Folder Repository";
	public static final String DIR_PROP = "directory";
    public static final String DUPLICATE_SCHEME = "duplicate:";

	
    public static final String FILE_KEY = "FILE";
    
    private Set<Algorithm> algorithms;
    
    public UntimeStampedFolderGroupManager() {
        algorithms = new HashSet<Algorithm>();
    	algorithms.add(new RdfXmlNameAlgorithm());
    }
    
	public boolean isSuitable(GroupEntry ge) {
		String dirName = LibraryUtilities.getStringProperty(ge, DIR_PROP);
		return ge.getId() != null 
					&& ge.getId().startsWith(ID_PREFIX)
					&& dirName != null
					&& new File(dirName).exists()
					&& new File(dirName).isDirectory();
	}

	public boolean update(GroupEntry ge) {
		File folder = new File(LibraryUtilities.getStringProperty(ge, DIR_PROP));
        long timeOfCurrentUpdate = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Update of group entry " + ge.getId() + " started at " + timeOfCurrentUpdate);
        }

        boolean modified  = removeStaleEntries(ge);
        if (algorithms == null || algorithms.isEmpty()) {
            return modified;
        }
        Map<URI, Set<File>> importToDiskLocationMap = getFileToImportLocationMap(folder);
        rebuild(ge, folder, importToDiskLocationMap, timeOfCurrentUpdate);
        if (logger.isDebugEnabled()) {
            logger.debug("Update took " + (System.currentTimeMillis() - timeOfCurrentUpdate) + "ms");
        }
        return modified;
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
		else if (ge != null && update(ge)) {
		    CatalogUtilities.save(catalog, catalogFile);
		}
		return catalog;
	}
	
	public GroupEntry createGroupEntry(File folder, XmlBaseContext context) throws IOException {
		String id = ID_PREFIX + ", " + DIR_PROP + "=" + folder.getCanonicalPath();
		return new GroupEntry(id, context, Prefer.PUBLIC, folder.toURI());
	}
	
	
	   
    private boolean removeStaleEntries(GroupEntry ge) {
        boolean removed = false;
        for (Entry e : ge.getEntries()) {
            if (e instanceof UriEntry) {
                UriEntry ue = (UriEntry) e;
                ge.removeEntry(ue);
                removed = true;
            }
        }
        return removed;
    }
   
    private boolean rebuild(GroupEntry ge, File folder, Map<URI, Set<File>> importToDiskLocationMap, long timeOfCurrentUpdate) {
        boolean modified = false;
        for (java.util.Map.Entry<URI, Set<File>> entry : importToDiskLocationMap.entrySet()) {
            URI webLocation = entry.getKey();
            Set<File> diskLocations = entry.getValue();
            if (diskLocations == null || diskLocations.isEmpty()) {
                continue;
            }
            boolean duplicatesFound = (diskLocations.size() != 1);

            if (duplicatesFound) {
                logger.warn("Multiple file locations found for import " + webLocation);
                continue;
            }
            for (File physicalLocation : diskLocations) {
                URI shortLocation = folder.toURI().relativize(physicalLocation.toURI());
                UriEntry u = new UriEntry("Automatically generated entry, " + OntologyCatalogManager.TIMESTAMP + "=" + timeOfCurrentUpdate, 
                                          ge, 
                                          webLocation.toString(),
                                          shortLocation,
                                          null);
                ge.addEntry(u);
                modified = true;
            }
        }
        return modified;
    }
    
    private  Map<URI, Set<File>> getFileToImportLocationMap(File folder) {
        Map<URI, Set<File>> importToDiskLocationMap = new HashMap<URI, Set<File>>();
        for (File physicalLocation : folder.listFiles()) {
            if (physicalLocation.exists() 
                    && physicalLocation.isFile() 
                    && physicalLocation.getPath().endsWith(".owl")
                    && !physicalLocation.getName().startsWith(".")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Folder " + physicalLocation + " modified at " + physicalLocation.lastModified() + " is getting new import to file mapping");
                }
                for (Algorithm algorithm : algorithms) {
                    Set<URI> webLocations = algorithm.getSuggestions(physicalLocation);
                    for (URI webLocation : webLocations) {
                        Set<File> diskLocations = importToDiskLocationMap.get(webLocation);
                        if (diskLocations == null) {
                            diskLocations = new HashSet<File>();
                            importToDiskLocationMap.put(webLocation, diskLocations);
                        }
                        diskLocations.add(physicalLocation);
                    }
                }
            }
        }
        return importToDiskLocationMap;
    }


}
