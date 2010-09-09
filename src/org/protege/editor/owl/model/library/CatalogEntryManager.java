package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A library contains a set of ontologies.  Each ontology
 * is intuitvely identified by a logical URI - the ontology URI.
 * If a library contains an ontology then the library can provide
 * a physical URI for that ontology.
 */
public abstract class CatalogEntryManager implements ProtegePluginInstance {
    private String id;

    public abstract boolean isSuitable(Entry entry);

    public abstract boolean update(Entry entry) throws IOException;

    public abstract boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException;

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
