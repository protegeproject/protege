package org.protege.editor.owl.model.refactor.ontology;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 26/02/2012
 */
public class EntityIRIUpdaterOntologyChangeStrategy implements OntologyIDChangeStrategy {

    @Override
    public List<OWLOntologyChange> getChangesForRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to) {
        if(!isEntityRenamingChange(from, to)) {
            return Collections.emptyList();
        }
        Map<OWLEntity, IRI> renameMap = new HashMap<OWLEntity, IRI>();
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
        String fromBase = fromId.getOntologyIRI().toString();
        String toBase = toId.getOntologyIRI().toString();
        getEntitiesRenamings(
                ontology.getObjectPropertiesInSignature(Imports.EXCLUDED),
                fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(
                ontology.getDataPropertiesInSignature(Imports.EXCLUDED),
                fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(
                ontology.getAnnotationPropertiesInSignature(Imports.EXCLUDED),
                fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(ontology.getClassesInSignature(Imports.EXCLUDED),
                fromBase, toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(
                ontology.getIndividualsInSignature(Imports.EXCLUDED), fromBase,
                toBase, renameMap, limit);
        if(renameMap.size() >= limit) {
            return;
        }
        getEntitiesRenamings(
                ontology.getDatatypesInSignature(Imports.EXCLUDED), fromBase,
                toBase, renameMap, limit);
    }
    
    public boolean hasEntitiesToRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to) {
        HashMap<OWLEntity, IRI> renameMap = new HashMap<OWLEntity, IRI>();
        getRenameMap(ontology, from, to, renameMap, 1);
        return !renameMap.isEmpty();
    }

    public Set<OWLEntity> getEntitiesToRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to) {
        if(!isEntityRenamingChange(from, to)) {
            return Collections.emptySet();
        }
        Map<OWLEntity, IRI> renameMap = new HashMap<OWLEntity, IRI>();
        getRenameMap(ontology, from, to, renameMap, Long.MAX_VALUE);
        return renameMap.keySet();
    }

    private void getEntitiesRenamings(Set<? extends OWLEntity> entities, String base, String toBase, Map<OWLEntity, IRI> renameMap, long limit) {
        int counter = 0;
        for(OWLEntity entity : entities) {
            IRI entityIRI = entity.getIRI();
            if(entityIRI.length() > base.length()) {
                if(entityIRI.subSequence(0, base.length()).equals(base)) {
                    StringBuilder name = new StringBuilder();
                    name.append(toBase);
                    name.append(entityIRI.subSequence(base.length(), entityIRI.length()));
                    renameMap.put(entity, IRI.create(name.toString()));
                    counter++;
                    if(counter >= limit) {
                        return;
                    }
                }
            }
        }
    }
}
