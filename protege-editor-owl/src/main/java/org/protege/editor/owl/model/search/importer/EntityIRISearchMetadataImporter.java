package org.protege.editor.owl.model.search.importer;

import java.util.Set;

import org.protege.editor.owl.model.search.EntityBasedSearchMDImporter;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchMetadata;
import org.protege.editor.owl.model.search.SearchMetadataDB;
import org.protege.editor.owl.model.search.SearchMetadataImportContext;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 02/10/2012
 */
public class EntityIRISearchMetadataImporter extends EntityBasedSearchMDImporter {

    public static final String GROUP_DESCRIPTION = "IRI";

    @Override
    public boolean isImporterFor(Set<SearchCategory> categories) {
        return categories.contains(SearchCategory.IRI);
    }

    @Override
    public void generateSearchMetadataFor(OWLEntity entity, String entityRendering, SearchMetadataImportContext context, SearchMetadataDB db) {
        SearchMetadata md = new SearchMetadata(SearchCategory.IRI, GROUP_DESCRIPTION, entity, entityRendering, entity.getIRI().toString());
        db.addResult(md);
    }
}
