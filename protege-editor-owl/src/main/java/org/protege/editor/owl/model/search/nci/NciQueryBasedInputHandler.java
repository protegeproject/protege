package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchInput;
import org.protege.editor.owl.model.search.SearchInputHandlerBase;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.lucene.LuceneSearcher;
import org.protege.editor.owl.model.search.lucene.SearchQueries;
import org.protege.editor.owl.model.search.lucene.SearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class NciQueryBasedInputHandler extends SearchInputHandlerBase<UserQueries> {

    private UserQueries userQueries = new UserQueries();

    private LuceneSearcher searcher;

    private boolean isLinked = false;

    public NciQueryBasedInputHandler(LuceneSearcher searcher) {
        this.searcher = searcher;
    }

    private List<SearchQueryBuilder> getBuilders() {
        List<SearchQueryBuilder> builders = new ArrayList<>();
        builders.add(new NciQueryForEntityIriBuilder(searcher));
        builders.add(new NciQueryForDisplayNameBuilder(searcher));
        builders.add(new NciQueryForAnnotationValueBuilder(searcher));
        builders.add(new NciQueryForFilteredAnnotationBuilder(searcher));
        return builders;
    }

    @Override
    public UserQueries getQueryObject() {
        return userQueries;
    }

    private void handle(SearchInput searchInput) {
        if (searchInput instanceof SearchKeyword) {
            handle((SearchKeyword) searchInput);
        } else if (searchInput instanceof CompoundKeyword) {
            handle((CompoundKeyword) searchInput);
        }
    }

    @Override
    public void handle(SearchKeyword searchKeyword) {
        SearchQueries searchQueries = new SearchQueries();
        for (SearchQueryBuilder builder : getBuilders()) {
            if (builder.isBuilderFor(searchKeyword)) {
                builder.add(searchKeyword);
                searchQueries.add(builder.build());
            }
        }
        userQueries.add(searchQueries, isLinked);
    }

    @Override
    public void handle(CompoundKeyword compoundKeyword) {
        if (compoundKeyword instanceof OrSearch) {
            handle((OrSearch) compoundKeyword);
        } else if (compoundKeyword instanceof AndSearch) {
            handle((AndSearch) compoundKeyword);
        } else if (compoundKeyword instanceof NestedSearch) {
            handle((NestedSearch) compoundKeyword);
        }
    }

    public void handle(OrSearch orSearch) {
        for (SearchInput searchGroup : orSearch.getSearchGroup()) {
            isLinked = false;
            handle(searchGroup);
        }
    }

    public void handle(AndSearch andSearch) {
        for (SearchKeyword keyword : andSearch) {
            isLinked = true;
            handle(keyword);
        }
    }

    public void handle(NestedSearch nestedSearch) {
        SearchQueries searchQueries = new SearchQueries();
        SearchQueryBuilder builder = new NciQueryForNestedAnnotationBuilder(searcher);
        for (SearchKeyword keyword : nestedSearch) {
            builder.add(keyword);
            searchQueries.add(builder.build());
        }
        userQueries.add(searchQueries, isLinked);
    }
}
