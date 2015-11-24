package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.lucene.SearchQuery;

import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/11/2015
 */
public class TraversableQuery extends SearchQuery {

    private List<SearchKeyword> hangingKeywords = new ArrayList<>();

    public TraversableQuery(BooleanQuery query, List<SearchKeyword> hangingKeywords) {
        super(query, SearchCategory.ANNOTATION_VALUE);
        this.hangingKeywords.addAll(hangingKeywords);
    }
}
