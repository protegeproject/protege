package org.protege.editor.owl.model.refactor.ontology;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.util.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 26/02/2012
 */
public class EntityIRIUpdaterOntologyChangeStrategy implements OntologyIDChangeStrategy {

    public List<OWLOntologyChange> getChangesForRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to) {
        if(!isEntityRenamingChange(from, to)) {
            return Collections.emptyList();
        }
        Map<OWLEntity, IRI> renameMap = new HashMap<>();
        getRenameMap(ontology, from, to, renameMap, Long.MAX_VALUE);
        OWLEntityRenamer entityRenamer = new OWLEntityRenamer(ontology.getOWLOntologyManager(), Collections.singleton(ontology));
        return entityRenamer.changeIRI(renameMap);
    }

    private boolean isEntityRenamingChange(OWLOntologyID from, OWLOntologyID to) {
        return !from.isAnonymous() && !to.isAnonymous() && !from.equals(to);
    }

    private void getRenameMap(OWLOntology ontology, OWLOntologyID fromId, OWLOntologyID toId, Map<OWLEntity, IRI> renameMap, long limit) {
        if(!isEntityRenamingChange(fromId, toId)) {
            return;
        }
        String fromBase = fromId.getOntologyIRI().get().toString();
        String toBase = toId.getOntologyIRI().get().toString();
        getEntitiesRenamings(ontology.getObjectPropertiesInSignature(false), fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(ontology.getDataPropertiesInSignature(false), fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(ontology.getAnnotationPropertiesInSignature(), fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(ontology.getClassesInSignature(false), fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(ontology.getIndividualsInSignature(false), fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(ontology.getDatatypesInSignature(false), fromBase, toBase, renameMap, limit);
    }
    
    public boolean hasEntitiesToRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to) {
        HashMap<OWLEntity, IRI> renameMap = new HashMap<>();
        getRenameMap(ontology, from, to, renameMap, 1);
        return !renameMap.isEmpty();
    }

    public Set<OWLEntity> getEntitiesToRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to) {
        if(!isEntityRenamingChange(from, to)) {
            return Collections.emptySet();
        }
        Map<OWLEntity, IRI> renameMap = new HashMap<>();
        getRenameMap(ontology, from, to, renameMap, Long.MAX_VALUE);
        return renameMap.keySet();
    }

    private void getEntitiesRenamings(Set<? extends OWLEntity> entities, String base, String toBase, Map<OWLEntity, IRI> renameMap, long limit) {
        int counter = 0;
        for(OWLEntity entity : entities) {
            IRI entityIRI = entity.getIRI();
            if(entityIRI.length() > base.length()) {
                if(entityIRI.subSequence(0, base.length()).equals(base)) {
                    renameMap.put(entity, IRI.create(toBase + entityIRI.subSequence(base.length(), entityIRI.length())));
                    counter++;
                    if(counter >= limit) {
                        return;
                    }
                }
            }
        }
    }
}
