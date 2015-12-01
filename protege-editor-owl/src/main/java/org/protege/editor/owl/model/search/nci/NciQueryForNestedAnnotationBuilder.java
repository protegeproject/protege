package org.protege.editor.owl.model.search.nci;

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

import java.util.ArrayList;
import java.util.List;

public class NciQueryForNestedAnnotationBuilder extends SearchQueryBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(NciQueryForNestedAnnotationBuilder.class);

    private LuceneSearcher searcher;

    private List<SearchKeyword> hangingKeywords = new ArrayList<>();

    private BooleanQuery.Builder builder = new BooleanQuery.Builder();

    public NciQueryForNestedAnnotationBuilder(LuceneSearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void add(SearchKeyword keyword) {
        if (keyword.isBlank()) {
            if (keyword.hasField()) {
                hangingKeywords.add(keyword);
            }
            return;
        }
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
            // Silently show the exception as debug message
            logger.debug(e.getMessage());
        }
    }

    @Override
    public SearchQuery build() {
        return new TraversableQuery(builder.build(), hangingKeywords, searcher);
    }

    @Override
    public boolean isBuilderFor(SearchKeyword keyword) {
        return (!keyword.hasField()) ? false : true;
    }
}
