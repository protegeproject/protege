package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchKeyword;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 10/11/2015
 */
public class QueryForFilteredAnnotationBuilder extends SearchQueryBuilder {

    private BooleanQuery.Builder builder = new BooleanQuery.Builder();

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
        if (keyword.searchWholeWords()) {
            builder.add(LuceneUtils.createPhraseQuery(
                    IndexField.ANNOTATION_TEXT,
                    keyword.getString()), LuceneUtils.toOccur(keyword.occurance()));
        } else {
            builder.add(LuceneUtils.createTermQuery(
                    IndexField.ANNOTATION_TEXT,
                    keyword.getString()), LuceneUtils.toOccur(keyword.occurance()));
        }
    }

    @Override
    public SearchQuery build() {
        return new SearchQuery(builder.build(), SearchCategory.ANNOTATION_VALUE);
    }

    @Override
    public boolean isBuilderFor(SearchKeyword keyword) {
        return (keyword.hasField()) ? true : false;
    }
}
