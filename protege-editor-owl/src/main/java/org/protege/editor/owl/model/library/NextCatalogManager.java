package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;

public class NextCatalogManager extends CatalogEntryManager {

    
    public String getDescription() {
        return "Import Ontology Library";
    }

    
    public String getDescription(Entry entry) {
        return "<html><body><b>Import Repository " +
                ((NextCatalogEntry) entry).getCatalog() +
                "</b></body></html>";
    }

    
    public boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException {
        return false;
    }

    
    public boolean isSuitable(Entry entry) {
        return entry instanceof NextCatalogEntry;
    }

    
    public NewEntryPanel newEntryPanel(XMLCatalog catalog) {
        return null;
    }

    
    public boolean update(Entry entry) throws IOException {
        return false;
    }

}
