package org.protege.editor.owl.model;

import org.semanticweb.owlapi.model.IRI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 31-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A default implementation of the missing import handler.
 */
public class MissingImportHandlerImpl implements MissingImportHandler {

    public IRI getDocumentIRI(IRI ontologyIRI) {
        // Do nothing - just pass back the ontology URI and
        // hope that it can be resolved.
        return ontologyIRI;
    }
}
