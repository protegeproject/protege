package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;

public class NextCatalogManager extends CatalogEntryManager {

    
    public String getDescription() {
        return "Import Ontology Library";
    }

    
    public String getDescription(Entry entry) {
        StringBuffer sb = new StringBuffer("<html><body><b>Import Repository ");
        sb.append(((NextCatalogEntry) entry).getCatalog());
        sb.append("</b></body></html>");
        return sb.toString();
    }

    
    public boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException {
        return false;
    }

    
    public boolean isSuitable(Entry entry) {
        return entry instanceof NextCatalogEntry;
    }

    
    public NewEntryPanel newEntryPanel(XmlBaseContext xmlBase) {
        return null;
    }

    
    public boolean update(Entry entry) throws IOException {
        return false;
    }

}
