package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchInterruptionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/11/2015
 */
public class QueryRunner {

    private static final Logger logger = LoggerFactory.getLogger(QueryRunner.class);

    private AtomicLong mutexId;

    public QueryRunner(AtomicLong mutexId) {
        this.mutexId = mutexId;
    }

    public void execute(Long searchId, SearchQueries searchQueries, AbstractDocumentHandler handler, SearchProgressListener listener)
            throws IOException, SearchInterruptionException {
        int counter = 1;
        for (SearchQuery searchQuery : searchQueries) {
            if (!isLatestSearch(searchId)) {
                throw new SearchInterruptionException();
            }
            execute(searchId, searchQuery, handler, listener);
            listener.fireSearchingProgressed((counter++*100)/searchQueries.size());
        }
    }

    public void execute(Long searchId, SearchQuery searchQuery, AbstractDocumentHandler handler, SearchProgressListener listener)
            throws IOException, SearchInterruptionException {
        logger.debug("... executing query " + searchQuery);
        searchQuery.evaluate(handler);
    }

    private boolean isLatestSearch(long searchId) {
        return searchId == mutexId.get();
    }

    public interface SearchProgressListener {
        
        void fireSearchingProgressed(int progress);
    }
}
