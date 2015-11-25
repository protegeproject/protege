package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchInterruptionException;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/11/2015
 */
public class QueryRunner {

    private static final Logger logger = LoggerFactory.getLogger(QueryRunner.class);

    private AtomicLong currentSearchId;

    public QueryRunner(AtomicLong searchId) {
        currentSearchId = searchId;
    }

    public void execute(Long searchId, Iterable<SearchQuery> searchQueries, AbstractDocumentHandler handler, SearchProgressListener listener)
            throws IOException, SearchInterruptionException {
        for (SearchQuery searchQuery : searchQueries) {
            execute(searchId, searchQuery, handler, listener);
        }
    }

    public void execute(Long searchId, SearchQuery searchQuery, AbstractDocumentHandler handler, SearchProgressListener listener)
            throws IOException, SearchInterruptionException {
        logger.debug("... executing query " + searchQuery);
        Set<Document> docs = searchQuery.evaluate();
        int counter = 1;
        for (Document doc : docs) {
            if (!isLatestSearch(searchId)) { throw new SearchInterruptionException(); }
            listener.fireSearchingProgressed((counter++*100)/docs.size());
            handler.handle(searchQuery.getCategory(), doc);
        }
    }

    private boolean isLatestSearch(long searchId) {
        return searchId == currentSearchId.get();
    }

    public interface SearchProgressListener {
        
        void fireSearchingProgressed(int progress);
    }
}
