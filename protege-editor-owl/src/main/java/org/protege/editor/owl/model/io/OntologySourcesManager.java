package org.protege.editor.owl.model.io;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.*;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 23, 2008<br><br>
 *
 * Checks every t seconds to see if the ontology source files have changed since loading
 */
public class OntologySourcesManager extends IOListener implements Disposable {

    public static final String ID = OntologySourcesManager.class.getName();

    private static final Logger logger = LoggerFactory.getLogger(OntologySourcesManager.class);

    private final Map<URI, Long> timestamps = new HashMap<>();

    private final OWLModelManager modelManager;

    private final List<OntologySourcesListener> listeners = new ArrayList<>();

    public OntologySourcesManager(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        modelManager.addIOListener(this);
    }

    public void markSources() {
        for(OWLOntology ontology : modelManager.getOntologies()) {
            update(ontology.getOWLOntologyManager().getOntologyDocumentIRI(ontology).toURI());
        }
    }

    public void checkSources() {
        Set<OWLOntology> changedOntologies = getChangedOntologies();
        if (!changedOntologies.isEmpty()){
            OntologySourcesListener.OntologySourcesChangeEvent event = new OntologySourcesListener.OntologySourcesChangeEvent(getChangedOntologies());
            for (OntologySourcesListener l : listeners){
                l.ontologySourcesChanged(event);
            }
        }
    }

    private Set<OWLOntology> getChangedOntologies() {
        logger.debug(LogBanner.start("Ontology Sources Manager"));
        Set<OWLOntology> changedOntologies = new HashSet<>();
        for (OWLOntology ont : modelManager.getOntologies()){
            URI uri = modelManager.getOntologyPhysicalURI(ont);
            logger.debug("Checking to see if ontology document has changed.");
            logger.debug("    Ontology: {}", ont.getOntologyID());
            logger.debug("    Document: {}", uri);
            final Optional<Long> externalTimestamp = getTimestampOfUri(uri);
            final Optional<Long> internalTimestamp = getInternalTimestamp(uri);
            logger.debug("    Last modified in Protege: {}", internalTimestamp.orElse(0L));
            logger.debug("    Last modified externally: {}", externalTimestamp.orElse(0L));
            if (externalTimestamp.isPresent() && internalTimestamp.isPresent()
                    && externalTimestamp.get() > internalTimestamp.get()) {
                logger.debug("    Ontology document has changed externally");
                changedOntologies.add(ont);
            }
            else {
                logger.debug("    Ontology document has not changed externally");
            }
        }
        logger.debug(LogBanner.end());
        return changedOntologies;
    }

    private Optional<Long> getInternalTimestamp(URI documentURI) {
        Long cachedValue = timestamps.get(documentURI);
        if(cachedValue == null) {
            // Not modified
            return Optional.empty();
        }
        else {
            return Optional.of(cachedValue);
        }
    }

    private void update(URI documentUri) {
        Optional<Long> timestamp = getTimestampOfUri(documentUri);
        if (timestamp.isPresent()){
            timestamps.put(documentUri, timestamp.get());
        }
    }


    private Optional<Long> getTimestampOfUri(URI uri){
        if (!"file".equals(uri.getScheme())) {
            return Optional.empty();
        }
        File file = new File(uri);
        long value = file.lastModified();
        if(value == 0) {
            return Optional.empty();
        }
        return Optional.of(value);
    }


    public void beforeSave(IOListenerEvent event) {
        // do nothing
    }


    public void afterSave(IOListenerEvent event) {
        final URI uri = event.getPhysicalURI();
        update(uri);
    }


    public void beforeLoad(IOListenerEvent event) {
        // do nothing
    }


    public void afterLoad(IOListenerEvent event) {
        final URI uri = event.getPhysicalURI();
        update(uri);
    }


    public void dispose() {
        timestamps.clear();
    }

    public void addListener(OntologySourcesListener l){
        listeners.add(l);
    }

    public void removeListener(OntologySourcesListener l){
        listeners.remove(l);
    }


    public void ignoreUpdates(Set<OWLOntology> onts) {
        for (OWLOntology ont : onts){
            URI uri = modelManager.getOntologyPhysicalURI(ont);
            Optional<Long> timestamp = getTimestampOfUri(uri);
            if (timestamp.isPresent()){
                timestamps.put(uri, timestamp.get());
            }
        }
    }
}
