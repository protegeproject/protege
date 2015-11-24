package org.protege.editor.owl.model.search.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
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
public class LuceneSearcher {

    private IndexReader reader;
    private IndexSearcher searcher;

    public LuceneSearcher(AbstractLuceneIndexer indexer) {
        reader = indexer.getIndexReader();
        searcher = new IndexSearcher(reader);
    }

    public TopDocs search(Query query) throws IOException {
        return searcher.search(query, Integer.MAX_VALUE);
    }

    public Document find(int docId) throws IOException {
        return searcher.doc(docId);
    }
}
