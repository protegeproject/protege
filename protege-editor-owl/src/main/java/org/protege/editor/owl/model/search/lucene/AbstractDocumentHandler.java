package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchCategory;

import org.apache.lucene.document.Document;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/11/2015
 */
public abstract class AbstractDocumentHandler {

    public abstract void handle(SearchCategory category, Document doc);
}
