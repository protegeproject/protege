package org.protege.editor.owl.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class OWLEntityDeleter {

    private static Logger logger = LoggerFactory.getLogger(OWLEntityDeleter.class);

    public static void deleteEntities(Collection<? extends OWLEntity> entities, OWLModelManager modelManager) {
        logger.info(LogBanner.start("Deleting entities"));
        logger.info("Generating changes to remove {} entities", entities.size());
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<OWLOntologyChange> allChanges = getChangesToDeleteEntities(entities, modelManager);
        logger.info("Generated {} changes to remove {} entities in {} ms", allChanges.size(), entities.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        modelManager.applyChanges(allChanges);
        logger.info("Applied {} changes in {}", allChanges.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        logger.info(LogBanner.end());
    }

    private static List<OWLOntologyChange> getChangesToDeleteEntities(Collection<? extends OWLEntity> entities, OWLModelManager modelManager) {
        List<OWLOntologyChange> allChanges = new ArrayList<>();
        for(OWLOntology ontology : modelManager.getOntologies()) {
            List<OWLOntologyChange> changeList = getChangesForOntology(entities, ontology);
            allChanges.addAll(changeList);
        }
        return allChanges;
    }

    private static List<OWLOntologyChange> getChangesForOntology(Collection<? extends OWLEntity> entities, OWLOntology ontology) {
        ReferenceFinder referenceFinder = new ReferenceFinder();
        ReferenceFinder.ReferenceSet referenceSet = referenceFinder.getReferenceSet(entities, ontology);
        List<OWLOntologyChange> changeList = new ArrayList<>(
                referenceSet.getReferencingAxioms().size() + referenceSet.getReferencingOntologyAnnotations().size()
        );
        for(OWLAxiom ax : referenceSet.getReferencingAxioms()) {
            changeList.add(new RemoveAxiom(referenceSet.getOntology(), ax));
        }
        for(OWLAnnotation annotation : referenceSet.getReferencingOntologyAnnotations()) {
            changeList.add(new RemoveOntologyAnnotation(referenceSet.getOntology(), annotation));
        }
        return changeList;
    }
}
