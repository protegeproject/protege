package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchCategory;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;

import java.io.IOException;
import java.util.Iterator;

public class SearchQuery extends Query implements Iterable<BooleanClause> {

    private BooleanQuery query;
    private SearchCategory category;

    public SearchQuery(BooleanQuery query, SearchCategory category) {
        this.query = query;
        this.category = category;
    }

    public BooleanQuery getQuery() {
        return query;
    }

    public SearchCategory getCategory() {
        return category;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        return query.createWeight(searcher, needsScores);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        return query.rewrite(reader);
    }

    @Override
    public Iterator<BooleanClause> iterator() {
        return query.iterator();
    }

    @Override
    public int hashCode() {
        return SearchQuery.class.getSimpleName().hashCode() + query.hashCode() + category.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SearchQuery)) {
            return false;
        }
        SearchQuery other = (SearchQuery) obj;
        return this.query.equals(other.query) && this.category.equals(other.category);
    }

    @Override
    public String toString(String field) {
        StringBuffer sb = new StringBuffer();
        sb.append(category.name()).append(": ").append(query.toString(field));
        return sb.toString();
    }
}
