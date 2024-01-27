package org.protege.editor.owl.model.search;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;


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
