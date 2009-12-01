package org.protege.editor.owl.ui.library.auto;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.protege.xmlcatalog.Util;
import org.protege.xmlcatalog.XMLCatalog;

public class XMLCatalogUpdater {
    private Set<Algorithm> algorithms;
    
    public void setAlgorithms(Set<Algorithm> algorithms) {
        this.algorithms = new HashSet<Algorithm>(algorithms);
    }

    public void update(File catalogFile) throws MalformedURLException, IOException {
        if (algorithms != null && !algorithms.isEmpty()) {
            long catalogDate = catalogFile.lastModified();
            XMLCatalog catalog = Util.parseDocument(catalogFile.toURL(), null);
            File folder = obtainFolder(catalog);
            if (folder != null) {
                for (File ontology : folder.listFiles()) {
                    if (ontology.exists() 
                            && ontology.isFile() 
                            && ontology.getPath().endsWith(".owl")
                            && ontology.lastModified() >= catalogDate) {
                        for (Algorithm algorithm : algorithms) {
                            
                        }
                    }
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
