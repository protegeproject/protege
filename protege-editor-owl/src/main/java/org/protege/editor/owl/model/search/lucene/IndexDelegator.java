package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.model.search.SearchContext;
import org.protege.editor.owl.model.search.SearchIndexPreferences;
import org.protege.editor.owl.model.search.lucene.AbstractLuceneIndexer.IndexProgressListener;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/01/2016
 */
public class IndexDelegator {

    private static final Logger logger = LoggerFactory.getLogger(IndexDelegator.class);

    public static final String PREFIX_LABEL = "ProtegeIndex";

    private String currentOntologyVersion = "";

    private String previousOntologyVersion = "";

    private IndexWriter indexWriter;

    private IndexDirectory indexDirectory;

    private OWLOntology ontology;

    private boolean isIndexReady = false;

    private final SearchIndexPreferences preferences = SearchIndexPreferences.getInstance();

    public IndexDelegator(OWLOntology ontology) {
        this.ontology = ontology;
        String ontologyVersion = getOntologyVersion(ontology);
        Optional<String> indexLocation = preferences.getIndexLocation(ontologyVersion);
        if (indexLocation.isPresent()) {
            String existingIndexLocation = indexLocation.get();
            indexDirectory = new IndexDirectory(existingIndexLocation);
            logger.info("Loading search index from " + indexDirectory.getLocation());
            isIndexReady = true;
        } else {
            String directoryName = buildIndexDirectoryName(ontology);
            String newIndexLocation = preferences.createIndexLocation(directoryName);
            indexDirectory = new IndexDirectory(newIndexLocation);
            isIndexReady = false;
        }
        setActiveOntologyVersion(ontologyVersion);
    }

    public OWLOntology getActiveOntology() {
        return ontology;
    }

    public String getActiveOntologyVersion() {
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
        preferences.registerIndexLocation(getActiveOntologyVersion(), indexDirectory.getLocation());
    }

    protected String getPreviousOntologyVersion() {
        return previousOntologyVersion;
    }

    public Directory getIndexDirectory() throws IOException {
        return indexDirectory.getPhysicalDirectory();
    }

    public void doIndex(AbstractLuceneIndexer indexer, SearchContext context, IndexProgressListener listener) throws IOException {
        if (!isIndexReady) {
            logger.info("Building search index...");
            logger.info("... saving index to {}", indexDirectory.getLocation());
            openIndexWriter(indexer.getIndexWriterConfig());
            long t0 = System.currentTimeMillis();
            for (OWLOntology ontology : context.getOntologies()) {
                logger.info("... building index for OntologyIRI<{}>", ontology.getOntologyID().getOntologyIRI().get());
                
                // Calculate first the total number of axioms that will get indexed
                int totalAxiomCount = ontology.getAxiomCount(AxiomType.DECLARATION) + ontology.getAxiomCount(AxiomType.ANNOTATION_ASSERTION);
                if (totalAxiomCount == 0) continue; // skip if zero
                
                // Start indexing
                int progress = 1;
                for (OWLEntity entity : ontology.getSignature(Imports.INCLUDED)) {
                    indexWriter.addDocument(indexer.createEntityDocument(entity, context));
                    listener.fireIndexingProgressed(percentage(progress++, totalAxiomCount));
                    for (OWLAnnotation annotation : EntitySearcher.getAnnotations(entity, ontology)) {
                        indexWriter.addDocument(indexer.createAnnotationDocument(entity, annotation, context));
                        listener.fireIndexingProgressed(percentage(progress++, totalAxiomCount));
                    }
                }
            }
            optimizeIndex();
            long t1 = System.currentTimeMillis();
            logger.info("... built index in " + (t1 - t0) + " ms");
            isIndexReady = true;
        }
    }

    public boolean doUpdate(AbstractLuceneIndexer indexer, ChangeSet changeSet, SearchContext context) throws IOException {
        if (isIndexReady) {
            logger.info("Updating search index from {} change(s)", changeSet.size());
            openIndexWriter(indexer.getIndexWriterConfig());
            
            // Process the remove change set first ...
            for (OWLEntity entity : changeSet.getRemoveDeclarations()) {
                indexWriter.deleteDocuments(new Term(IndexField.ENTITY_IRI, indexer.getEntityId(entity)));
            }
            for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getRemoveAnnotations()) {
                indexWriter.deleteDocuments(
                        new Term(IndexField.ENTITY_IRI, indexer.getEntityId(annotationEntry.getKey())),
                        new Term(IndexField.ANNOTATION_IRI, indexer.getAnnotationId(annotationEntry.getValue())));
            }
            // ... and then continue process the addition change set.
            for (OWLEntity entity : changeSet.getAddDeclarations()) {
                indexWriter.addDocument(indexer.createEntityDocument(entity, context));
            }
            for (Entry<OWLEntity, OWLAnnotation> annotationEntry : changeSet.getAddAnnotations()) {
                indexWriter.addDocument(indexer.createAnnotationDocument(annotationEntry.getKey(), annotationEntry.getValue(), context));
            }
            commitIndex();
            return true;
        }
        return false;
    }

    public void optimizeIndex() throws IOException {
        if (isOpen(indexWriter)) {
            indexWriter.close();
        }
    }

    public void commitIndex() throws IOException {
        if (isOpen(indexWriter)) {
            indexWriter.commit();
        }
    }

    public void saveIndex() throws IOException {
        updateOntologyVersion();
        optimizeIndex();
        logger.info("Saved search index");
    }

    public void revertIndex() throws IOException {
        preferences.clearIndexLocation(getActiveOntologyVersion());
        FileUtils.deleteDirectory(new File(indexDirectory.getLocation()));
    }

    public void closeIndex() throws IOException {
        if (isOpen(indexWriter)) {
            indexWriter.close();
            logger.info("Closed search index");
        }
    }

    /*
     * Private utility methods
     */

    private void openIndexWriter(IndexWriterConfig indexWriterConfig) throws IOException {
        if (!isOpen(indexWriter)) {
            indexWriter = new IndexWriter(indexDirectory.getPhysicalDirectory(), indexWriterConfig);
        }
    }

    private boolean isOpen(IndexWriter indexWriter) {
        if (indexWriter == null) return false;
        return indexWriter.isOpen();
    }

    private String buildIndexDirectoryName(OWLOntology ontology) {
        String ontologyIdHex = Integer.toHexString(ontology.getOntologyID().toString().hashCode());
        String timestampHex = Integer.toHexString(new Date().hashCode());
        return String.format("%s-%s-%s", PREFIX_LABEL, ontologyIdHex, timestampHex);
    }

    private String getOntologyVersion(OWLOntology ontology) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ontology.getSignature().hashCode();
        result = prime * result + ontology.getAnnotations().hashCode();
        return Integer.toHexString(result);
    }

    private void updateOntologyVersion() {
        String newVersion = getOntologyVersion(getActiveOntology());
        if (!currentOntologyVersion.equals(newVersion)) {
            changeActiveOntologyVersion(newVersion);
        }
    }

    private int percentage(int progress, int total) {
        return (progress * 100) / total;
    }
}
