package org.protege.editor.owl.model.library;

import org.protege.editor.core.util.ProtegeDirectories;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.UriEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;


/**
 * Author: Tim Redmond, Stanford University.
 */
public class OntologyCatalogManager {

    public static final String CATALOG_NAME = "catalog-v001.xml";

    public static final String CATALOG_BACKUP_PREFIX = "catalog-backup-";

    public static final String TIMESTAMP = "Timestamp";

    private static Logger logger = LoggerFactory.getLogger(OntologyCatalogManager.class);

    private Map<File, XMLCatalog> localCatalogs = new HashMap<>();

    private XMLCatalog activeCatalog;

    /** Id of a catalog entry written when the user resolves a missing import by hand. */
    public static final String USER_IMPORT_RESOLUTION_ID = "User Entered Import Resolution";

    private File activeCatalogFolder;

    private List<CatalogEntryManager> entryManagers;

    public OntologyCatalogManager() {
        entryManagers = new ArrayList<>();
        CatalogEntryManagerLoader pluginLoader = new CatalogEntryManagerLoader();
        for(CatalogEntryManagerPlugin plugin : pluginLoader.getPlugins()) {
            try {
                entryManagers.add(plugin.newInstance());
            } catch(Throwable t) {
                logger.warn("An error occurred whilst instantiating a CatalogEntryManager plugin: {}", t);
            }
        }
    }

    public OntologyCatalogManager(List<? extends CatalogEntryManager> entryManagers) {
        this.entryManagers = new ArrayList<>(entryManagers);
    }

    /**
     * this works for catalogs that are generated from a parse or created
     * by the OntologyCatalogManager.
     */
    @Nullable
    public static File getCatalogFile(XMLCatalog catalog) {
        if(catalog == null || catalog.getXmlBaseContext() == null) {
            return null;
        }
        File f = new File(catalog.getXmlBaseContext().getXmlBase());
        if(f.exists() && f.isDirectory()) {
            f = getCatalogFile(f);
        }
        return f.exists() ? f : null;
    }

    /**
     * A utility method.
     * Gets a File representing the path of the catalog file in the specified folder.
     * This simply retrieves the path and does not imply the existence of the catalog file.
     *
     * @param folder The folder
     */
    @Nonnull
    public static File getCatalogFile(@Nonnull File folder) {
        return new File(folder, CATALOG_NAME);
    }

    @Nonnull
    public static File getGlobalCatalogFile() {
        return getCatalogFile(ProtegeDirectories.getDataDirectory());
    }

    @Nonnull
    public List<CatalogEntryManager> getCatalogEntryManagers() {
        return Collections.unmodifiableList(entryManagers);
    }


    public Optional<URI> getRedirectForUri(@Nonnull URI originalURI) {
        for(XMLCatalog catalog : getAllCatalogs()) {
            URI redirect = CatalogUtilities.getRedirect(originalURI, catalog);
            if(redirect != null) {
                return Optional.of(redirect);
            }
        }
        return Optional.empty();
    }

    /**
     * @deprecated Use {@link #getRedirectForUri(URI)} instead
     */
    @Deprecated
    @Nullable
    public URI getRedirect(URI original) {
        return getRedirectForUri(original).orElse(null);
    }

    @Nonnull
    public List<XMLCatalog> getAllCatalogs() {
        List<XMLCatalog> catalogs = new ArrayList<>();
        catalogs.addAll(getLocalCatalogs());
        return catalogs;
    }

    public Collection<XMLCatalog> getLocalCatalogs() {
        return localCatalogs.values();
    }

    @Nonnull
    public Optional<XMLCatalog> getCurrentCatalog() {
        return Optional.ofNullable(activeCatalog);
    }

    /**
     * @deprecated Use {@link #getCurrentCatalog()} instead
     */
    @Deprecated
    @Nullable
    public XMLCatalog getActiveCatalog() {
        return activeCatalog;
    }

    /**
     * Records that the user pointed a missing import at a local file. The mapping
     * goes to the top of the catalog of the folder currently open, so it wins over
     * generated entries, and the catalog is saved.
     */
    public void addUserImportResolution(URI ontologyIri, File file) throws IOException {
        XMLCatalog catalog = getCurrentCatalog()
                .orElseThrow(() -> new IOException("No catalog is open to record the import resolution in"));
        URI relativeFile = CatalogUtilities.relativize(file.toURI(), catalog);
        catalog.addEntry(0, new UriEntry(USER_IMPORT_RESOLUTION_ID, catalog, ontologyIri.toString(), relativeFile, null));
        // A catalog created in this session has the folder as its base, not the file.
        File catalogFile = getCatalogFile(catalog);
        if (catalogFile == null) {
            throw new IOException("Cannot find the catalog file for " + catalog.getXmlBaseContext().getXmlBase());
        }
        CatalogUtilities.save(catalog, catalogFile);
    }

    @Nonnull
    public Optional<Path> getCurrentCatalogDirectory() {
        return Optional.ofNullable(activeCatalogFolder).map(File::toPath);
    }

    /**
     * @deprecated Use {@link #getCurrentCatalogDirectory()} instead.
     */
    @Deprecated
    @Nullable
    public File getActiveCatalogFolder() {
        return activeCatalogFolder;
    }

    public XMLCatalog addFolder(File dir) {
        XMLCatalog lib = localCatalogs.get(dir);
        // Add the parent file which will be the folder
        if(lib == null) {
            // Add automapped library
            logger.info("Adding folder to ontology catalog: {}", dir);
            lib = ensureCatalogExists(dir);
            localCatalogs.put(dir, lib);
        }
        activeCatalog = lib;
        activeCatalogFolder = dir;
        return lib;
    }

    public XMLCatalog ensureCatalogExists(File folder) {
        XMLCatalog catalog = null;
        File catalogFile = getCatalogFile(folder);
        boolean alreadyExists = catalogFile.exists();
        boolean modified = false;
        if(alreadyExists) {
            try {
                catalog = CatalogUtilities.parseDocument(catalogFile.toURI().toURL());
            } catch(Throwable e) {
                logger.warn("An error occurred whilst parsing the catalog document at {}.  Error: {}", catalogFile.getAbsolutePath(), e);
                backup(folder, catalogFile);
            }
        }
        if(catalog == null) {
            catalog = new XMLCatalog(folder.toURI());
            modified = true;
        }
        if(alreadyExists) {
            try {
                modified = modified | update(catalog);
            } catch(Throwable t) {
                logger.warn("An error occurred whilst updating the catalog document at {}.  Error: {}", catalogFile.getAbsolutePath(), t);
            }
        }
        else {
            for(CatalogEntryManager entryManager : entryManagers) {
                try {
                    modified = modified | entryManager.initializeCatalog(folder, catalog);
                } catch(Throwable t) {
                    logger.warn("An error occurred whilst initializing the catalog at {}.  Error: {}", catalogFile.getAbsolutePath(), t);
                }
            }
        }
        if(modified) {
            try {
                CatalogUtilities.save(catalog, catalogFile);
            } catch(IOException e) {
                logger.warn("An error occurred whilst saving the catalog at {}.  Error: {}", catalogFile.getAbsolutePath(), e);
            }
        }
        return catalog;
    }

    private static void backup(File folder,
                               File catalogFile) {
        File backup;
        int i = 0;
        while(true) {
            if(!((backup = new File(folder, CATALOG_BACKUP_PREFIX + (i++) + ".xml")).exists())) {
                break;
            }
        }
        catalogFile.renameTo(backup);
    }

    public boolean update(XMLCatalog catalog) throws IOException {
        boolean modified = false;
        for(Entry entry : catalog.getEntries()) {
            for(CatalogEntryManager updater : entryManagers) {
                if(updater.isSuitable(entry)) {
                    modified = modified | updater.update(entry);
                }
            }
        }
        return modified;
    }

    public void reloadFolder(File dir) throws IOException {
        localCatalogs.remove(dir);
        localCatalogs.put(dir, CatalogUtilities.parseDocument(getCatalogFile(dir).toURI().toURL()));
        activeCatalog = localCatalogs.get(activeCatalogFolder);
    }
}
