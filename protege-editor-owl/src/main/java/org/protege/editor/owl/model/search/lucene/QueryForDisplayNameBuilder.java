package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchKeyword;

import org.apache.lucene.search.BooleanQuery;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 10/11/2015
 */
public class QueryForDisplayNameBuilder extends SearchQueryBuilder {

    private BooleanQuery.Builder builder = new BooleanQuery.Builder();

    @Override
    public void add(SearchKeyword keyword) {
        if (keyword.isBlank()) return;
        if (keyword.searchWholeWords()) {
            builder.add(LuceneUtils.createPhraseQuery(
                    IndexField.DISPLAY_NAME,
                    keyword.getString()), LuceneUtils.toOccur(keyword.occurance()));
        } else {
            builder.add(LuceneUtils.createTermQuery(
                    IndexField.DISPLAY_NAME,
                    keyword.getString()), LuceneUtils.toOccur(keyword.occurance()));
        }
    }

    @Override
    public SearchQuery build() {
        return new SearchQuery(builder.build(), SearchCategory.DISPLAY_NAME);
    }

    @Override
    public boolean isBuilderFor(SearchKeyword keyword) {
        return (keyword.hasField()) ? false : true;
    }
}
