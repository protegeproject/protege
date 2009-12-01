package org.protege.editor.owl.ui.library.auto;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;

public class XMLCatalogUpdater {
    private static Logger log = Logger.getLogger(XMLCatalogUpdater.class);
    
    private Set<Algorithm> algorithms;
    
    public void setAlgorithms(Set<Algorithm> algorithms) {
        this.algorithms = new HashSet<Algorithm>(algorithms);
    }

    public void update(File catalogFile) throws MalformedURLException, IOException {
        if (algorithms != null && !algorithms.isEmpty()) {
            boolean changed = false;
            long catalogDate = catalogFile.lastModified();
            XMLCatalog catalog = CatalogUtilities.parseDocument(catalogFile.toURL());
            File folder = obtainFolder(catalog);
            if (folder != null) {
                for (File ontology : folder.listFiles()) {
                    if (ontology.exists() 
                            && ontology.isFile() 
                            && ontology.getPath().endsWith(".owl")
                            && ontology.lastModified() >= catalogDate) {
                        for (Algorithm algorithm : algorithms) {
                            Set<URI> webLocations = algorithm.getSuggestions(ontology);
                            for (URI webLocation : webLocations) {
                                if (CatalogUtilities.getRedirect(webLocation, catalog) == null) {
                                    UriEntry u = new UriEntry(null, 
                                                              catalog, 
                                                              ontology.toURI().toString(),
                                                              webLocation,
                                                              catalogFile.toURI());
                                    catalog.addEntry(u);
                                    changed = true;
                                }
                                else {
                                    log.info("Location with duplicate redirects found " + webLocation);
                                }
                            }
                        }
                    }
                }
                if (changed) {
                    CatalogUtilities.save(catalog, catalogFile);
                }
            }
        }
    }
    
    private File obtainFolder(XMLCatalog catalog) {
        URI base = catalog.getXmlBase();
        if (base != null) {
            try {
                File folder = new File(base);
                if (folder.exists() && folder.isDirectory()) {
                    return folder;
                }
            }
            catch (IllegalArgumentException iae) {
                ; // expected if the base does not represent a file
            }
        }
        return null;
    }
}
