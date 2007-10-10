package org.protege.editor.owl.model;

import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 31-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The missing import handler is called by the system as a last resort if
 * it cannot obtain a physical URI of an ontology (and hence cannot load
 * the ontology).
 */
public interface MissingImportHandler {

    /**
     * Given an ontology URI, the missing import handler
     * sets up the appropriate libraries/mappings so that
     * the system can obtain a physical URI for the given
     * ontology.
     * @param ontologyURI The ontology URI that identifies the
     *                    ontology that the system is trying to obtain a physical
     *                    URI for.
     * @return A physical URI that locates the specified ontology.
     *         <b>Must NOT be <code>null</code></b>
     */
    public URI getPhysicalURI(URI ontologyURI);
}
