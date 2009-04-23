package org.protege.editor.owl.model.cache;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityRenderingCacheImpl implements OWLEntityRenderingCache {

    private static final Logger logger = Logger.getLogger(OWLEntityRenderingCacheImpl.class);

    private Map<String, OWLClass> owlClassMap = new HashMap<String, OWLClass>();

    private Map<String, OWLObjectProperty> owlObjectPropertyMap = new HashMap<String, OWLObjectProperty>();

    private Map<String, OWLDataProperty> owlDataPropertyMap = new HashMap<String, OWLDataProperty>();

    private Map<String, OWLAnnotationProperty> owlAnnotationPropertyMap = new HashMap<String, OWLAnnotationProperty>();

    private Map<String, OWLNamedIndividual> owlIndividualMap = new HashMap<String, OWLNamedIndividual>();

    private Map<String, OWLDatatype> owlDatatypeMap = new HashMap<String, OWLDatatype>();

    private Map<OWLEntity, String> entityRenderingMap = new HashMap<OWLEntity, String>();

    private OWLModelManager owlModelManager;

    private OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            processChanges(changes);
        }
    };


    public OWLEntityRenderingCacheImpl() {
    }


    public void setOWLModelManager(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        owlModelManager.addOntologyChangeListener(listener);
    }


    private void processChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            if (change instanceof OWLAxiomChange) {
                OWLAxiomChange chg = (OWLAxiomChange) change;
                for (OWLEntity ent : chg.getEntities()) {
                    updateRendering(ent);
                }
            }
        }
    }


    public void rebuild() {
        clear();
        OWLEntityRenderer entityRenderer = owlModelManager.getOWLEntityRenderer();

        OWLClass thing = owlModelManager.getOWLDataFactory().getOWLThing();
        owlClassMap.put(entityRenderer.render(thing), thing);
        entityRenderingMap.put(thing, entityRenderer.render(thing));
        OWLClass nothing = owlModelManager.getOWLDataFactory().getOWLNothing();
        entityRenderingMap.put(nothing, entityRenderer.render(nothing));
        owlClassMap.put(entityRenderer.render(nothing), nothing);

        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            for (OWLClass cls : ont.getReferencedClasses()) {
                addRendering(cls, owlClassMap);
            }
            for (OWLObjectProperty prop : ont.getReferencedObjectProperties()) {
                addRendering(prop, owlObjectPropertyMap);
            }
            for (OWLDataProperty prop : ont.getReferencedDataProperties()) {
                addRendering(prop, owlDataPropertyMap);
            }
            for (OWLAnnotationProperty prop : ont.getReferencedAnnotationProperties()) {
                addRendering(prop, owlAnnotationPropertyMap);
            }
            for (OWLIndividual ind : ont.getReferencedIndividuals()) {
                if (!ind.isAnonymous()){
                    addRendering(ind.asNamedIndividual(), owlIndividualMap);
                }
            }
            for (OWLDatatype dt : new OWLDataTypeUtils(owlModelManager.getOWLOntologyManager()).getBuiltinDatatypes()) {
                addRendering(dt, owlDatatypeMap);
            }
        }
    }


    public void dispose() {
        clear();
        owlModelManager.removeOntologyChangeListener(listener);
    }


    private void clear() {
        owlClassMap.clear();
        owlObjectPropertyMap.clear();
        owlDataPropertyMap.clear();
        owlAnnotationPropertyMap.clear();
        owlIndividualMap.clear();
        owlDatatypeMap.clear();
        entityRenderingMap.clear();
    }


    public OWLClass getOWLClass(String rendering) {
        return owlClassMap.get(rendering);
    }


    public OWLObjectProperty getOWLObjectProperty(String rendering) {
        return owlObjectPropertyMap.get(rendering);
    }


    public OWLDataProperty getOWLDataProperty(String rendering) {
        return owlDataPropertyMap.get(rendering);
    }


    public OWLAnnotationProperty getOWLAnnotationProperty(String rendering) {
        return owlAnnotationPropertyMap.get(rendering);
    }


    public OWLNamedIndividual getOWLIndividual(String rendering) {
        return owlIndividualMap.get(rendering);
    }


    public OWLDatatype getOWLDatatype(String rendering) {
        return owlDatatypeMap.get(rendering);
    }


    public String getRendering(OWLEntity owlEntity) {
        return entityRenderingMap.get(owlEntity);
    }


    public OWLEntity getOWLEntity(String rendering) {
        // Examine in the order of class, property, individual
        OWLEntity entity = getOWLClass(rendering);
        if (entity != null) {
            return entity;
        }
        entity = getOWLObjectProperty(rendering);
        if (entity != null) {
            return entity;
        }
        entity = getOWLDataProperty(rendering);
        if (entity != null) {
            return entity;
        }
        entity = getOWLIndividual(rendering);
        if (entity != null) {
            return entity;
        }
        entity = getOWLDatatype(rendering);
        if (entity != null) {
            return entity;
        }
        return null;
    }


    public void addRendering(OWLEntity owlEntity) {
        owlEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLDataProperty entity) {
                addRendering(entity, owlDataPropertyMap);
            }

            public void visit(OWLObjectProperty entity) {
                addRendering(entity, owlObjectPropertyMap);
            }

            public void visit(OWLAnnotationProperty owlAnnotationProperty) {
                addRendering(owlAnnotationProperty, owlAnnotationPropertyMap);
            }

            public void visit(OWLNamedIndividual entity) {
                addRendering(entity, owlIndividualMap);
            }

            public void visit(OWLClass entity) {
                addRendering(entity, owlClassMap);
            }

            public void visit(OWLDatatype entity) {
                addRendering(entity, owlDatatypeMap);
            }
        });
    }


    private <T extends OWLEntity> void addRendering(T entity, Map<String, T> map) {
        if (!entityRenderingMap.containsKey(entity)) {
            String rendering = owlModelManager.getRendering(entity);
            map.put(rendering, entity);
            entityRenderingMap.put(entity, rendering);
        }
    }


    public void removeRendering(OWLEntity owlEntity) {
        final String oldRendering = entityRenderingMap.get(owlEntity);
        entityRenderingMap.remove(owlEntity);

        owlEntity.accept(new OWLEntityVisitor() {

            public void visit(OWLClass entity) {
                owlClassMap.remove(oldRendering);
            }

            public void visit(OWLDataProperty entity) {
                owlDataPropertyMap.remove(oldRendering);
            }

            public void visit(OWLObjectProperty entity) {
                owlObjectPropertyMap.remove(oldRendering);
            }

            public void visit(OWLAnnotationProperty owlAnnotationProperty) {
                owlAnnotationPropertyMap.remove(oldRendering);
            }

            public void visit(OWLNamedIndividual entity) {
                owlIndividualMap.remove(oldRendering);
            }

            public void visit(OWLDatatype entity) {
                owlDatatypeMap.remove(oldRendering);
            }
        });
    }


    public void updateRendering(final OWLEntity ent) {
        boolean updateRendering = false;
        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            if (ont.containsEntityReference(ent)) {
                updateRendering = true;
                break;
            }
        }
        removeRendering(ent); // always remove the old rendering
        if (updateRendering) {
            addRendering(ent);
        }
    }


    public Set<String> getOWLClassRenderings() {
        return owlClassMap.keySet();
    }


    public Set<String> getOWLObjectPropertyRenderings() {
        return owlObjectPropertyMap.keySet();
    }


    public Set<String> getOWLDataPropertyRenderings() {
        return owlDataPropertyMap.keySet();
    }


    public Set<String> getOWLAnnotationPropertyRenderings() {
        return owlAnnotationPropertyMap.keySet();
    }


    public Set<String> getOWLIndividualRenderings() {
        return owlIndividualMap.keySet();
    }


    public Set<String> getOWLDatatypeRenderings() {
        return owlDatatypeMap.keySet();
    }


    public Set<String> getOWLEntityRenderings() {
        Set<String> renderings = new HashSet<String>(owlClassMap.size() +
                                                     owlObjectPropertyMap.size() +
                                                     owlDataPropertyMap.size() +
                                                     owlAnnotationPropertyMap.size() +
                                                     owlIndividualMap.size() +
                                                     owlDatatypeMap.size());
        renderings.addAll(owlClassMap.keySet());
        renderings.addAll(owlObjectPropertyMap.keySet());
        renderings.addAll(owlDataPropertyMap.keySet());
        renderings.addAll(owlAnnotationPropertyMap.keySet());
        renderings.addAll(owlIndividualMap.keySet());
        renderings.addAll(owlDatatypeMap.keySet());
        return renderings;
    }
}
