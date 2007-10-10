package org.protege.editor.owl.model.library;

import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOntologyLibrary implements OntologyLibrary {

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getDescription());
        builder.append("\n");
        for (URI ontURI : getOntologyURIs()) {
            builder.append("    ");
            builder.append(ontURI);
            builder.append(" --> ");
            builder.append(getPhysicalURI(ontURI));
            builder.append("\n");
        }
        return builder.toString();
    }
}
