package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.lucene.IndexField;
import org.protege.editor.owl.model.search.lucene.LuceneSearcher;
import org.protege.editor.owl.model.search.lucene.LuceneUtils;
import org.protege.editor.owl.model.search.lucene.SearchQuery;
import org.protege.editor.owl.model.search.lucene.SearchQueryBuilder;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NciQueryForDisplayNameBuilder extends SearchQueryBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(NciQueryForDisplayNameBuilder.class);

    private LuceneSearcher searcher;

    private BooleanQuery query;

    public NciQueryForDisplayNameBuilder(LuceneSearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void add(SearchKeyword keyword) {
        if (keyword.isBlank()) return;
        try {
            query = LuceneUtils.createQuery(IndexField.DISPLAY_NAME, keyword.getString(), new StandardAnalyzer());
        }
        catch (ParseException e) {
            // Silently show is as debug message
            logger.debug(e.getMessage());
        }
    }

    @Override
    public SearchQuery build() {
        return new SearchQuery(query, SearchCategory.DISPLAY_NAME, searcher);
    }

    @Override
    public boolean isBuilderFor(SearchKeyword keyword) {
        return (keyword.hasField()) ? false : true;
    }
}
