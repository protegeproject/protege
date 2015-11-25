package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchCategory;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SearchQuery {

    private BooleanQuery query;
    private SearchCategory category;
    private LuceneSearcher searcher;

    public SearchQuery(BooleanQuery query, SearchCategory category, LuceneSearcher searcher) {
        this.query = query;
        this.category = category;
        this.searcher = searcher;
    }

    public BooleanQuery getQuery() {
        return query;
    }

    public SearchCategory getCategory() {
        return category;
    }

    public Set<Document> evaluate() throws IOException {
        Set<Document> docs = new HashSet<>();
        TopDocs hits = searcher.search(query);
        int hitNumber = hits.scoreDocs.length;
        for (int i = 1; i <= hitNumber; i++) {
            Document doc = searcher.find(hits.scoreDocs[i-1].doc);
            docs.add(doc);
        }
        return docs;
    }

    public void evaluate(AbstractDocumentHandler handler) throws IOException {
        Set<Document> docs = evaluate();
        docs.stream().forEach((doc) -> handler.handle(category, doc));
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
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(category.name()).append(": ").append(query);
        return sb.toString();
    }
}
