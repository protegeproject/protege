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
public class DisplayNameSearchMetadataImporter extends EntityBasedSearchMDImporter {

    public static final String GROUP_DESCRIPTION = "Display name";

    @Override
    public boolean isImporterFor(Set<SearchCategory> categories) {
        return categories.contains(SearchCategory.DISPLAY_NAME);
    }

    @Override
    public void generateSearchMetadataFor(OWLEntity entity, String entityRendering, SearchMetadataImportContext context, SearchMetadataDB searchMetadataDB) {
        SearchMetadata searchMetadata = new SearchMetadata(SearchCategory.DISPLAY_NAME, GROUP_DESCRIPTION, entity, entityRendering, entityRendering);
        searchMetadataDB.addResult(searchMetadata);
    }
}
