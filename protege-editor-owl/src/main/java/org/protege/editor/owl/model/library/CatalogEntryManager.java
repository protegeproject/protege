package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;


/**
 * Author: Timothy Redmond
 */
public abstract class CatalogEntryManager implements ProtegePluginInstance {

    public static final String SHADOWED_SCHEME = "shadowed:";

    public static final String DUPLICATE_SCHEME = "duplicate:";

    public static final String[] IGNORED_SCHEMES = {DUPLICATE_SCHEME, SHADOWED_SCHEME};

    private String id;

    public abstract boolean isSuitable(Entry entry);

    public abstract boolean update(Entry entry) throws IOException;

    public abstract boolean initializeCatalog(File folder,
                                              XMLCatalog catalog) throws IOException;

    public abstract NewEntryPanel newEntryPanel(XMLCatalog catalog);

    public abstract String getDescription();

    public abstract String getDescription(Entry entry);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void initialise() throws Exception {
        //not used
    }

    public void dispose() throws Exception {
        // not used
    }
}
