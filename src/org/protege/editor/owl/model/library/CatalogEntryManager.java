package org.protege.editor.owl.model.library;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.XmlBaseContext;
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
     
     public abstract boolean isSuitable(Entry ge);
     
     public abstract boolean update(Entry ge) throws IOException;
     
     public abstract boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException;
     
     public abstract Entry newEntryDialog(Frame parent, XmlBaseContext context);
     
     public abstract Entry editEntryDialog(Frame parent, XmlBaseContext context, Entry input);
     
     public abstract String getDescription(Entry ge);
     
     public void initialise() throws Exception {
    	 //not used
     }
     
     public void dispose() throws Exception {
    	 // not used
    }
}
