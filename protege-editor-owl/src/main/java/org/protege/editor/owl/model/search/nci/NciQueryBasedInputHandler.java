package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.model.search.CompoundKeyword;
import org.protege.editor.owl.model.search.SearchInput;
import org.protege.editor.owl.model.search.SearchInputHandlerBase;
import org.protege.editor.owl.model.search.SearchKeyword;
import org.protege.editor.owl.model.search.lucene.LuceneSearcher;
import org.protege.editor.owl.model.search.lucene.SearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class NciQueryBasedInputHandler extends SearchInputHandlerBase {

    private UnionQuerySet unionQuery = new UnionQuerySet();

    private LuceneSearcher searcher;

    private AbstractQuerySet placeholder;

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

    public UnionQuerySet getSearchQuery() {
        return unionQuery;
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
        QuerySet querySet = new QuerySet();
        for (SearchQueryBuilder builder : getBuilders()) {
            if (builder.isBuilderFor(searchKeyword)) {
                builder.add(searchKeyword);
                querySet.add(builder.build());
            }
        }
        placeholder = querySet;
    }

    @Override
    public void handle(CompoundKeyword compoundKeyword) {
        if (compoundKeyword instanceof OrSearch) {
            handle((OrSearch) compoundKeyword);
        } else if (compoundKeyword instanceof AndSearch) {
            handle((AndSearch) compoundKeyword);
        }
    }

    public void handle(OrSearch orSearch) {
        for (SearchInput searchGroup : orSearch.getSearchGroup()) {
            handle(searchGroup);
            unionQuery.add(placeholder);
        }
    }

    public void handle(AndSearch andSearch) {
        LinkedQuerySet linkedQuerySet = new LinkedQuerySet();
        for (SearchKeyword keyword : andSearch) {
            handle(keyword);
            linkedQuerySet.add((QuerySet) placeholder);
        }
        placeholder = linkedQuerySet;
    }
}
