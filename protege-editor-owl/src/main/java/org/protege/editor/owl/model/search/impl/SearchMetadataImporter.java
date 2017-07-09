package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchCategory;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 */
public interface SearchMetadataImporter {

    SearchMetadataDB getSearchMetadata(OWLEditorKit editorKit, Set<SearchCategory> categories);
}
