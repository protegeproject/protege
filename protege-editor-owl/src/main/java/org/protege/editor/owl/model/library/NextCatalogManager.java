package org.protege.editor.owl.model.library;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.editor.owl.ui.library.plugins.NextCatalogPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;

import java.io.File;
import java.io.IOException;

public class NextCatalogManager extends CatalogEntryManager {

    
    public String getDescription() {
        return "Import XML Catalog";
    }

    
    public String getDescription(Entry entry) {
        return "<html><body><b>"+getDescription()+" " +
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
        return new NextCatalogPanel(catalog);
    }

    
    public boolean update(Entry entry) throws IOException {
        return false;
    }

}
