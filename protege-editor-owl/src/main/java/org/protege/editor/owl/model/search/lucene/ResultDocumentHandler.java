package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultMatch;

import org.apache.lucene.document.Document;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/11/2015
 */
public class ResultDocumentHandler extends AbstractDocumentHandler {

    private OWLEditorKit editorKit;
    private ImmutableSet.Builder<SearchResult> builder = new ImmutableSet.Builder<>();
    
    public ResultDocumentHandler(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public Set<SearchResult> getSearchResults() {
        return builder.build();
    }

    @Override
    public void handle(SearchCategory category, Document doc) {
        Optional<OWLEntity> subject = getOWLEntity(doc.get(IndexField.ENTITY_IRI));
        if (subject.isPresent()) {
            ResultDocument resultDocument = createResultDocument(category, doc, subject.get());
            SearchResult searchResult = new SearchResult(resultDocument, createEmptySearchResultMatch());
            builder.add(searchResult);
        }
    }

    private ResultDocument createResultDocument(SearchCategory category, Document doc, OWLEntity subject) {
        String subjectName = editorKit.getOWLModelManager().getRendering(subject);
        switch (category) {
            case IRI: return new ResultDocument(category, "IRI", subject, subjectName, getContent(doc, IndexField.ENTITY_IRI));
            case DISPLAY_NAME: return new ResultDocument(category, "DISPLAY NAME", subject, subjectName, getContent(doc, IndexField.DISPLAY_NAME));
            case ANNOTATION_VALUE: return new ResultDocument(category, "@" + getContent(doc, IndexField.ANNOTATION_DISPLAY_NAME), subject, subjectName, getContent(doc, IndexField.ANNOTATION_TEXT));
            default: break;
        }
        return null;
    }

    private String getContent(Document doc, String fieldName) {
        return doc.get(fieldName);
    }

    private Optional<OWLEntity> getOWLEntity(String identifier) {
        OWLEntityFinder finder = editorKit.getOWLModelManager().getOWLEntityFinder();
        return finder.getEntities(IRI.create(identifier)).stream().findFirst();
    }

    private ImmutableList<SearchResultMatch> createEmptySearchResultMatch() {
        ImmutableList.Builder<SearchResultMatch> builder = new ImmutableList.Builder<>();
        builder.add(new SearchResultMatch("", 0, 0));
        return builder.build();
    }
}
