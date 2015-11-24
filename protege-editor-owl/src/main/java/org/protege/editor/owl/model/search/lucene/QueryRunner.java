package org.protege.editor.owl.model.search.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/11/2015
 */
public class QueryRunner {

    private static final Logger logger = LoggerFactory.getLogger(QueryRunner.class);

    private LuceneSearcher searcher;

    public QueryRunner(LuceneSearcher searcher) {
        this.searcher = searcher;
    }

    public void execute(SearchQuery searchQuery, AbstractDocumentHandler handler, SearchProgressListener listener) throws IOException {
        logger.debug("... executing query " + searchQuery);
        TopDocs hits = searcher.search(searchQuery);
        int hitNumber = hits.scoreDocs.length;
        for (int i = 1; i <= hitNumber; i++) {
            listener.fireSearchingProgressed((i*100)/hitNumber);
            Document doc = searcher.find(hits.scoreDocs[i-1].doc);
            handler.handle(searchQuery.getCategory(), doc);
        }
    }

    public interface SearchProgressListener {
        
        void fireSearchingProgressed(int progress);
    }
}
