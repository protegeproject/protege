package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public abstract class AbstractLuceneIndexer {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractLuceneIndexer.class);

    private final Analyzer textAnalyzer;

    public AbstractLuceneIndexer() {
        this(new StandardAnalyzer());
    }

    public AbstractLuceneIndexer(Analyzer analyzer) {
        textAnalyzer = analyzer;
    }

    public void doIndex(IndexDelegator delegator, SearchContext context, IndexProgressListener listener) throws IOException {
        delegator.writeIndex(this, context, listener);
    }

    public boolean doUpdate(IndexDelegator delegator, ChangeSet changeSet, SearchContext context) throws IOException {
        return delegator.updateIndex(this, changeSet, context);
    }

    public IndexWriterConfig getIndexWriterConfig() {
        return new IndexWriterConfig(textAnalyzer);
    }

    public DirectoryReader getIndexReader(IndexDelegator delegator) {
        try {
            return DirectoryReader.open(delegator.getIndexDirectory());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected Document createEntityDocument(OWLEntity entity, SearchContext context) {
        Document doc = new Document();
        doc.add(new TextField(IndexField.ENTITY_IRI, getEntityId(entity), Store.YES));
        doc.add(new TextField(IndexField.DISPLAY_NAME, getDisplayName(entity, context), Store.YES));
        doc.add(new StringField(IndexField.ENTITY_TYPE, getType(entity), Store.YES));
        return doc;
    }

    protected Document createAnnotationDocument(OWLEntity entity, OWLAnnotation annotation, SearchContext context) {
        Document doc = new Document();
        doc.add(new TextField(IndexField.ENTITY_IRI, getEntityId(entity), Store.YES));
        doc.add(new TextField(IndexField.DISPLAY_NAME, getDisplayName(entity, context), Store.YES));
        doc.add(new StringField(IndexField.ANNOTATION_IRI, getAnnotationId(annotation), Store.YES));
        doc.add(new TextField(IndexField.ANNOTATION_DISPLAY_NAME, getDisplayName(annotation.getProperty(), context), Store.YES));
        doc.add(new TextField(IndexField.ANNOTATION_TEXT, getAnnotationText(annotation, context), Store.YES));
        return doc;
    }

    protected String getEntityId(OWLEntity entity) {
        return entity.getIRI().toString();
    }

    protected String getDisplayName(OWLObject object, SearchContext context) {
        return context.getRendering(object);
    }

    protected String getType(OWLEntity entity) {
        return entity.getEntityType().getName();
    }

    protected String getAnnotationId(OWLAnnotation annotation) {
        return annotation.getProperty().getIRI().toString();
    }

    protected String getAnnotationText(OWLAnnotation annotation, SearchContext context) {
        return context.getStyledStringRendering(annotation).getString();
    }

    public interface IndexProgressListener {
        
        void fireIndexingProgressed(long progress);
    }
}
