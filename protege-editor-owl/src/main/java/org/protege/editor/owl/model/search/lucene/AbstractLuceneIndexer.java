package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map.Entry;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public abstract class AbstractLuceneIndexer {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractLuceneIndexer.class);

    private final Analyzer textAnalyzer;

    private IndexDirectory indexDirectory;

    public AbstractLuceneIndexer() {
        this(new StandardAnalyzer());
    }

    public AbstractLuceneIndexer(Analyzer analyzer) {
        textAnalyzer = analyzer;
    }

    protected IndexWriter getIndexWriter(IndexWriterConfig indexWriterConfig) throws IOException {
        return new IndexWriter(indexDirectory.getPhysicalDirectory(), indexWriterConfig);
    }

    public void doIndex(IndexDelegator delegator, SearchContext context, IndexProgressListener listener) throws IOException {
        indexDirectory = delegator.getIndexDirectory();
        if (!indexDirectory.exists()) {
            final IndexWriter writer = getIndexWriter(new IndexWriterConfig(textAnalyzer));
            for (OWLOntology ontology : context.getOntologies()) {
                logger.info("... building index for " + ontology.getOntologyID());
                
                // Calculate first the total number of axioms that will get indexed
                int totalAxiomCount = ontology.getAxiomCount(AxiomType.DECLARATION) + ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION);
                if (totalAxiomCount == 0) continue; // skip if zero
                
                // Start indexing
                int progress = 1;
                for (OWLEntity entity : ontology.getSignature(Imports.INCLUDED)) {
                    writer.addDocument(createEntityDocument(entity, context));
                    listener.fireIndexingProgressed(percentage(progress++, totalAxiomCount));
                    for (OWLAnnotation annotation : EntitySearcher.getAnnotations(entity, ontology)) {
                        writer.addDocument(createAnnotationDocument(entity, annotation, context));
                        listener.fireIndexingProgressed(percentage(progress++, totalAxiomCount));
                    }
                }
            }
            writer.close();
        }
    }

    public void doUpdate(ChangeSet changeSet, SearchContext context) throws IOException {
        final IndexWriter writer = getIndexWriter(new IndexWriterConfig(textAnalyzer));
        
        // Process the remove change set first ...
        for (OWLEntity entity : changeSet.getRemoveDeclarations()) {
            writer.deleteDocuments(new Term(IndexField.ENTITY_IRI, getEntityId(entity)));
        }
        for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getRemoveAnnotations()) {
            writer.deleteDocuments(new Term(IndexField.ENTITY_IRI, getEntityId(annotationEntry.getKey())),
                    new Term(IndexField.ANNOTATION_IRI, getAnnotationId(annotationEntry.getValue())));
        }
        // ... and then continue process the addition change set.
        for (OWLEntity entity : changeSet.getAddDeclarations()) {
            writer.addDocument(createEntityDocument(entity, context));
        }
        for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getAddAnnotations()) {
            writer.addDocument(createAnnotationDocument(annotationEntry.getKey(), annotationEntry.getValue(), context));
        }
        writer.close();
    }

    private int percentage(int progress, int total) {
        return (progress * 100) / total;
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

    public DirectoryReader getIndexReader() {
        try {
            return DirectoryReader.open(indexDirectory.getPhysicalDirectory());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
