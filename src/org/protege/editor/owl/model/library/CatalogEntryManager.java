package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;

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
public interface CatalogEntryManager {
     
     boolean isSuitable(Entry ge);
     
     boolean update(Entry ge) throws IOException;
     
     boolean initializeCatalog(File folder, XMLCatalog catalog) throws IOException;
     
     Entry newEntryDialog(XmlBaseContext context);
     
     Entry editEntryDialog(XmlBaseContext context, Entry input);
     
     String getDescription(Entry ge);
}
