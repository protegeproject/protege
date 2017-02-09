package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.model.search.SearchCategory;

import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public abstract class EntityBasedSearchMDImporter {

    public abstract boolean isImporterFor(Set<SearchCategory> categories);

    public abstract void generateSearchMetadataFor(OWLEntity entity, String entityRendering, SearchMetadataImportContext context, SearchMetadataDB db);
}
