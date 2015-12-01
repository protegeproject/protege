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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NciQueryForFilteredAnnotationBuilder extends SearchQueryBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(NciQueryForFilteredAnnotationBuilder.class);

    private LuceneSearcher searcher;

    private BooleanQuery.Builder builder = new BooleanQuery.Builder();

    public NciQueryForFilteredAnnotationBuilder(LuceneSearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void add(SearchKeyword keyword) {
        if (keyword.isBlank()) return;
        handleFilterField(keyword);
        handleSearchString(keyword);
    }

    private void handleFilterField(SearchKeyword keyword) {
        builder.add(LuceneUtils.createTermQuery(
                IndexField.ANNOTATION_DISPLAY_NAME,
                keyword.getField()), Occur.MUST);
    }
    
    private void handleSearchString(SearchKeyword keyword) {
        try {
            BooleanQuery query = LuceneUtils.createQuery(IndexField.ANNOTATION_TEXT, keyword.getString(), new StandardAnalyzer());
            for (BooleanClause clause : query.clauses()) {
                builder.add(clause);
            }
        }
        catch (ParseException e) {
            // Silently show is as debug message
            logger.debug(e.getMessage());
        }
    }

    @Override
    public SearchQuery build() {
        return new SearchQuery(builder.build(), SearchCategory.ANNOTATION_VALUE, searcher);
    }

    @Override
    public boolean isBuilderFor(SearchKeyword keyword) {
        return (!keyword.hasField()) ? false : true;
    }
}
