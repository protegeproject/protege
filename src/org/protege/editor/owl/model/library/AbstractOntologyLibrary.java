package org.protege.editor.owl.model.library;

import org.semanticweb.owlapi.model.IRI;


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
        builder.append(getClassExpression());
        builder.append("\n");
        for (IRI iri : getOntologyIRIs()) {
            builder.append("    ");
            builder.append(iri);
            builder.append(" --> ");
            builder.append(getPhysicalURI(iri));
            builder.append("\n");
        }
        return builder.toString();
    }
}
