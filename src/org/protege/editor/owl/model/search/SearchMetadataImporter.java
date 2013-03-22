package org.protege.editor.owl.model.search;

import org.protege.editor.owl.OWLEditorKit;

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
