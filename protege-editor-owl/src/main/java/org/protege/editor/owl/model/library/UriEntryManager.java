package org.protege.editor.owl.model.library;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.editor.owl.ui.library.plugins.UriEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.UriEntry;

import java.io.File;
import java.io.IOException;

public class UriEntryManager extends CatalogEntryManager {

    
    public String getDescription() {
        return "Single Ontology Redirect";
    }

    
    public String getDescription(Entry entry) {
        UriEntry uriEntry = (UriEntry) entry;
        return "<html><body><b>Ontology IRI: " + uriEntry.getName() + "</b><br>"
        + "Redirected To: " + uriEntry.getUri() + "</body></html>";
    }

    public boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException {
        return false;
    }

    public boolean isSuitable(Entry entry) {
        return entry instanceof UriEntry;
    }

    
    public NewEntryPanel newEntryPanel(XMLCatalog catalog) {
        return new UriEntryPanel(catalog);
    }

    
    public boolean update(Entry entry) throws IOException {
        return false;
    }

}
