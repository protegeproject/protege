package org.protege.editor.owl.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class SearchMetadataImportManager {

    public List<SearchMetadataImporter> getImporters() {
        List<SearchMetadataImporter> importers = new ArrayList<SearchMetadataImporter>();
        importers.add(new DefaultSearchMetadataImporter());
        return importers;
    }

}
