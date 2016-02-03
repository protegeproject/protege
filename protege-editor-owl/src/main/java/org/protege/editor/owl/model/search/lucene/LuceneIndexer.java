package org.protege.editor.owl.model.search.lucene;

import java.io.IOException;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 10/11/2015
 */
public class LuceneIndexer extends AbstractLuceneIndexer {

    public LuceneIndexer() {
        super();
    }

    public void commit(IndexDelegator delegator) {
        try {
            delegator.commitIndex();
        } catch (IOException e) {
            logger.error("... commit index failed");
            e.printStackTrace();
        }
    }

    public void save(IndexDelegator delegator) {
        try {
            delegator.saveIndex();
        } catch (IOException e) {
            logger.error("... save index failed");
            e.printStackTrace();
        }
    }

    public void revert(IndexDelegator delegator) {
        try {
            delegator.revertIndex();
        } catch (IOException e) {
            logger.error("... revert index failed");
            e.printStackTrace();
        }
    }

    public void close(IndexDelegator delegator) {
        try {
            delegator.closeIndex();
        } catch (IOException e) {
            logger.error("... close index failed");
            e.printStackTrace();
        }
    }
}
