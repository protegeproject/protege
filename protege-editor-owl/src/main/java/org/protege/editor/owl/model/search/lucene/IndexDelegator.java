package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchContext;
import org.protege.editor.owl.model.search.SearchIndexPreferences;
import org.protege.editor.owl.model.search.lucene.AbstractLuceneIndexer.IndexProgressListener;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/01/2016
 */
public class IndexDelegator {

    private static final Logger logger = LoggerFactory.getLogger(IndexDelegator.class);

    private String currentOntologyVersion = "";

    private String previousOntologyVersion = "";

    private IndexWriter indexWriter;

    private IndexDirectory indexDirectory;

    private OWLOntology ontology;

    private final SearchIndexPreferences preferences = SearchIndexPreferences.getInstance();

    public IndexDelegator(OWLOntology ontology) {
        this.ontology = ontology;
        String ontologyVersion = OntologyVersion.create(ontology);
        Optional<String> indexLocation = preferences.getIndexLocation(ontologyVersion);
        if (indexLocation.isPresent()) {
            String existingIndexLocation = indexLocation.get();
            indexDirectory = IndexDirectory.load(existingIndexLocation);
            logger.info("Loading search index from " + indexDirectory.getLocation());
        } else {
            String newIndexLocation = preferences.createIndexLocation(ontology);
            indexDirectory = IndexDirectory.create(newIndexLocation);
        }
        setActiveOntologyVersion(ontologyVersion);
    }

    /**
     * Get the ontology object used by this delegator.
     *
     * @return Returns an {@link OWLOntology} object.
     */
    public OWLOntology getOntology() {
        return ontology;
    }

    /**
     * Get the current ontology version maintain by this delegator.
     */
    public String getOntologyVersion() {
        return currentOntologyVersion;
    }

    protected void setActiveOntologyVersion(String version) {
        previousOntologyVersion = version;
        currentOntologyVersion = version;
        preferences.registerIndexLocation(version, indexDirectory.getLocation());
    }

    protected void changeActiveOntologyVersion(String newVersion) {
        previousOntologyVersion = currentOntologyVersion;
        currentOntologyVersion = newVersion;
        preferences.clearIndexLocation(getPreviousOntologyVersion());
        preferences.registerIndexLocation(getOntologyVersion(), indexDirectory.getLocation());
    }

    protected String getPreviousOntologyVersion() {
        return previousOntologyVersion;
    }

    /**
     * Get the {@link Directory} object known by this delegator.
     * @throws IOException
     */
    public Directory getIndexDirectory() throws IOException {
        return indexDirectory.getPhysicalDirectory();
    }

    /**
     * Method to initiate the index writing.
     */
    public void writeIndex(AbstractLuceneIndexer indexer, SearchContext context, IndexProgressListener listener) throws IOException {
        if (!isIndexAvailable()) {
            logger.info("Building search index...");
            logger.info("... saving index to {}", indexDirectory.getLocation());
            openIndexWriter(indexer.getIndexWriterConfig());
            
            Set<Document> documents = new HashSet<>();
            for (OWLOntology ontology : context.getOntologies()) {
                logger.info("... preparing index for OntologyIRI<{}>", ontology.getOntologyID().getOntologyIRI().get());
                int declarationCounter = 0;
                int annotationCounter = 0;
                for (OWLEntity entity : ontology.getSignature(Imports.INCLUDED)) {
                    if (isExcludedEntity(entity)) continue; // skip excluded entities
                    documents.add(indexer.createEntityDocument(entity, context));
                    declarationCounter++;
                    for (OWLAnnotation annotation : EntitySearcher.getAnnotations(entity, ontology)) {
                        documents.add(indexer.createAnnotationDocument(entity, annotation, context));
                        annotationCounter++;
                    }
                }
                logger.info("... found {} entities to index", declarationCounter);
                logger.info("... found {} annotations to index", annotationCounter);
            }
            /*
             * Call the index writing procedure and do the optimization
             */
            long t0 = System.currentTimeMillis();
            logger.info("... start writing index");
            indexDirectory.doIndex(indexWriter, documents, listener);
            optimizeIndex();
            long t1 = System.currentTimeMillis();
            logger.info("... built index in " + (t1 - t0) + " ms");
        }
    }

    /**
     * Method to update (add/remove) the index item according to changes in the ontology.
     */
    public boolean updateIndex(AbstractLuceneIndexer indexer, ChangeSet changeSet, SearchContext context) throws IOException {
        if (isIndexAvailable()) {
            logger.info("Updating search index from {} change(s)", changeSet.size());
            openIndexWriter(indexer.getIndexWriterConfig());
            
            // Process the remove change set first ...
            for (OWLEntity entity : changeSet.getRemoveDeclarations()) {
                Term deletedEntityTerm = new Term(IndexField.ENTITY_IRI, indexer.getEntityId(entity));
                indexDirectory.doDelete(indexWriter, deletedEntityTerm);
            }
            for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getRemoveAnnotations()) {
                Term deletedEntityTerm = new Term(IndexField.ENTITY_IRI, indexer.getEntityId(annotationEntry.getKey()));
                Term deletedAnnotationTerm = new Term(IndexField.ANNOTATION_IRI, indexer.getAnnotationId(annotationEntry.getValue()));
                indexDirectory.doDelete(indexWriter, deletedEntityTerm, deletedAnnotationTerm);
            }
            // ... and then continue process the addition change set.
            for (OWLEntity entity : changeSet.getAddDeclarations()) {
                Document addedEntityDocument = indexer.createEntityDocument(entity, context);
                indexDirectory.doAdd(indexWriter, addedEntityDocument);
            }
            for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getAddAnnotations()) {
                Document addedAnnotationDocument = indexer.createAnnotationDocument(annotationEntry.getKey(), annotationEntry.getValue(), context);
                indexDirectory.doAdd(indexWriter, addedAnnotationDocument);
            }
            commitIndex();
            return true;
        }
        else {
            logger.error("Index is not available. Please initiate writing the index first");
            return false;
        }
    }

    /**
     * Method to perform index optimization by Lucene.
     */
    public void optimizeIndex() throws IOException {
        if (isOpen(indexWriter)) {
            indexWriter.close();
        }
    }

    /**
     * Method to commit update of index item.
     */
    public void commitIndex() throws IOException {
        if (isOpen(indexWriter)) {
            indexWriter.commit();
        }
    }

    /**
     * Method to save changes in the index tree.
     */
    public void saveIndex() throws IOException {
        updateOntologyVersion();
        optimizeIndex();
        logger.info("Saved search index");
    }

    /**
     * Method to rollback changes in the index tree.
     */
    public void revertIndex() throws IOException {
        preferences.clearIndexLocation(getOntologyVersion());
        FileUtils.deleteDirectory(new File(indexDirectory.getLocation()));
    }

    /**
     * Method to close the index.
     */
    public void closeIndex() throws IOException {
        if (isOpen(indexWriter)) {
            indexWriter.close();
            logger.info("Closed search index");
        }
    }

    /*
     * Private utility methods
     */

    private boolean isExcludedEntity(OWLEntity entity) {
        /*
         * Reduce the number of index size by excluding some type of entities. Rationale:
         * - OWLDatatype: searching for data type might not have significant use.
         * - OWLAnnotationProperty: searching for annotation property is already handled
         *   by indexing the OWLAnnotation objects. No need redundancy.
         */
        return (entity instanceof OWLDatatype) || (entity instanceof OWLAnnotationProperty);
    }

    private boolean isIndexAvailable() {
        return indexDirectory.isIndexAvailable();
    }

    private void openIndexWriter(IndexWriterConfig indexWriterConfig) throws IOException {
        if (!isOpen(indexWriter)) {
            indexWriter = new IndexWriter(indexDirectory.getPhysicalDirectory(), indexWriterConfig);
        }
    }

    private boolean isOpen(IndexWriter indexWriter) {
        if (indexWriter == null) return false;
        return indexWriter.isOpen();
    }

    private void updateOntologyVersion() {
        String newVersion = OntologyVersion.create(getOntology());
        if (!currentOntologyVersion.equals(newVersion)) {
            changeActiveOntologyVersion(newVersion);
        }
    }

    /**
     * Public utility factory to compute ontology version
     */
    public static class OntologyVersion {
        
        private OWLOntology ontology;
        
        private OntologyVersion(OWLOntology ontology) {
            this.ontology = ontology;
        }
        
        public static String create(OWLOntology ontology) {
            return new OntologyVersion(ontology).calculate();
        }
        
        private String calculate() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ontology.getSignature().hashCode();
            result = prime * result + ontology.getAnnotations().hashCode();
            return Integer.toHexString(result);
        }
    }
}
