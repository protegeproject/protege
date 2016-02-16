package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchManager;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public abstract class LuceneSearcher extends SearchManager {

    public abstract IndexSearcher getIndexSearcher();

    public TopDocs search(Query query) throws IOException {
        return getIndexSearcher().search(query, Integer.MAX_VALUE);
    }

    public Document find(int docId) throws IOException {
        return getIndexSearcher().doc(docId);
    }
}
