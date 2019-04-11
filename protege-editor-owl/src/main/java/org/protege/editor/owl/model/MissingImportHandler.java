package org.protege.editor.owl.model;

import org.protege.editor.core.ui.util.Resettable;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 31-Aug-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * The missing import handler is called by the system as a last resort if
 * it cannot obtain a physical URI of an ontology (and hence cannot load
 * the ontology).
 */
public interface MissingImportHandler extends OWLOntologyIRIMapper, Resettable {

    @Override
    default void reset() {
    }
}
