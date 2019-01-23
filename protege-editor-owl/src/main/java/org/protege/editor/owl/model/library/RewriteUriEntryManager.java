package org.protege.editor.owl.model.library;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.editor.owl.ui.library.plugins.RewriteUriEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;
import org.protege.xmlcatalog.entry.RewriteUriEntry;

import java.io.File;
import java.io.IOException;

/**
 * Created by vblagodarov on 03-08-17.
 */
public class RewriteUriEntryManager extends CatalogEntryManager {
    @Override
    public boolean isSuitable(Entry entry) {
        return entry instanceof RewriteUriEntry;
    }

    @Override
    public boolean update(Entry entry) throws IOException {
        return false;
    }

    @Override
    public boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException {
        return false;
    }

    @Override
    public NewEntryPanel newEntryPanel(XMLCatalog catalog) {
        return new RewriteUriEntryPanel(catalog);
    }

    @Override
    public String getDescription() {
        return "Rewrite uri entry";
    }

    @Override
    public String getDescription(Entry entry) {
        RewriteUriEntry rewriteEntry = (RewriteUriEntry) entry;
        return "<html><body><b>Rewrite " + rewriteEntry.getUriStartString() + "</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + "<font color=\"gray\"> by: " + rewriteEntry.getRewritePrefix() + "</font><p> </p></body></html>";
    }

}
