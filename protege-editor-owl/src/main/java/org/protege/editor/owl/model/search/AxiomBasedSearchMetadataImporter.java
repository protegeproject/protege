package org.protege.editor.owl.model.search;

import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public abstract class AxiomBasedSearchMetadataImporter {

    public abstract boolean isImporterFor(AxiomType<?> axiomType, Set<SearchCategory> categories);

    public abstract void generateSearchMetadataFor(OWLAxiom axiom, OWLEntity axiomSubject, String axiomSubjectRendering, SearchMetadataImportContext context, SearchMetadataDB db);

}
