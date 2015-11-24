package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchContext;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public abstract class AxiomBasedSearchMetadataImporter {

    public abstract boolean isImporterFor(AxiomType<?> axiomType, Set<SearchCategory> categories);

    public abstract void generateSearchMetadataFor(OWLAxiom axiom, OWLEntity axiomSubject, String axiomSubjectRendering, SearchContext context, SearchMetadataDB db);

}
