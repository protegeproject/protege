package org.protege.editor.owl.model.cache;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityRenderingCacheImpl implements OWLEntityRenderingCache {

    private final SetMultimap<String, OWLClass> owlClassMap = HashMultimap.create();

    private final SetMultimap<String, OWLObjectProperty> owlObjectPropertyMap = HashMultimap.create();

    private final SetMultimap<String, OWLDataProperty> owlDataPropertyMap = HashMultimap.create();

    private final SetMultimap<String, OWLAnnotationProperty> owlAnnotationPropertyMap = HashMultimap.create();

    private final SetMultimap<String, OWLNamedIndividual> owlIndividualMap = HashMultimap.create();

    private final SetMultimap<String, OWLDatatype> owlDatatypeMap = HashMultimap.create();

    private final Map<OWLEntity, String> entityRenderingMap = new HashMap<>();

    private final OWLOntologyChangeListener listener = this::processChanges;

    private OWLModelManager owlModelManager;

    public OWLEntityRenderingCacheImpl() {
    }


    public void setOWLModelManager(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        owlModelManager.addOntologyChangeListener(listener);
    }


    private void processChanges(List<? extends OWLOntologyChange> changes) {
        changes.stream()
                .filter(OWLOntologyChange::isAxiomChange)
                .flatMap(chg -> chg.getSignature().stream())
                .distinct()
                .forEach(this::updateRendering);
    }


    public void rebuild() {
        clear();
        owlModelManager.getOWLEntityRenderer();
        OWLDataFactory factory = owlModelManager.getOWLDataFactory();
        
        addRendering(factory.getOWLThing(), owlClassMap);
        addRendering(factory.getOWLNothing(), owlClassMap);
        addRendering(factory.getOWLTopObjectProperty(), owlObjectPropertyMap);
        addRendering(factory.getOWLBottomObjectProperty(), owlObjectPropertyMap);
        addRendering(factory.getOWLTopDataProperty(), owlDataPropertyMap);
        addRendering(factory.getOWLBottomDataProperty(), owlDataPropertyMap);

        for (OWLOntology ont : owlModelManager.getOntologies()) {
            for (OWLClass cls : ont.getClassesInSignature()) {
                addRendering(cls, owlClassMap);
            }
            for (OWLObjectProperty prop : ont.getObjectPropertiesInSignature()) {
                addRendering(prop, owlObjectPropertyMap);
            }
            for (OWLDataProperty prop : ont.getDataPropertiesInSignature()) {
                addRendering(prop, owlDataPropertyMap);
            }
            for (OWLIndividual ind : ont.getIndividualsInSignature()) {
                if (!ind.isAnonymous()){
                    addRendering(ind.asOWLNamedIndividual(), owlIndividualMap);
                }
            }
            for (OWLAnnotationProperty prop : ont.getAnnotationPropertiesInSignature()) {
                addRendering(prop, owlAnnotationPropertyMap);
            }
        }

        // standard annotation properties        
        for (IRI uri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTY_IRIS){
            addRendering(factory.getOWLAnnotationProperty(uri), owlAnnotationPropertyMap);
        }

        // Dublin Core
        for(DublinCoreVocabulary vocabulary : DublinCoreVocabulary.values()) {
            addRendering(factory.getOWLAnnotationProperty(vocabulary.getIRI()), owlAnnotationPropertyMap);
        }

        // datatypes
        final OWLDataTypeUtils datatypeUtils = new OWLDataTypeUtils(owlModelManager.getOWLOntologyManager());
        for (OWLDatatype dt : datatypeUtils.getKnownDatatypes(owlModelManager.getActiveOntologies())) {
            addRendering(dt, owlDatatypeMap);
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

    private static <E extends OWLEntity> E getFirstEntityOrNull(Multimap<String, E> renderingMap, String rendering) {
        return renderingMap.get(rendering).stream().findFirst().orElse(null);
    }

    public OWLClass getOWLClass(String rendering) {
        return getFirstEntityOrNull(owlClassMap, rendering);
    }

    public Set<OWLEntity> getOWLEntities(String rendering) {
        ImmutableSet.Builder<OWLEntity> builder = ImmutableSet.builder();
        builder.addAll(owlClassMap.get(rendering));
        builder.addAll(owlObjectPropertyMap.get(rendering));
        builder.addAll(owlDataPropertyMap.get(rendering));
        builder.addAll(owlAnnotationPropertyMap.get(rendering));
        builder.addAll(owlIndividualMap.get(rendering));
        builder.addAll(owlDatatypeMap.get(rendering));
        return builder.build();
    }


    public OWLObjectProperty getOWLObjectProperty(String rendering) {
        return getFirstEntityOrNull(owlObjectPropertyMap, rendering);
    }


    public OWLDataProperty getOWLDataProperty(String rendering) {
        return getFirstEntityOrNull(owlDataPropertyMap, rendering);
    }


    public OWLAnnotationProperty getOWLAnnotationProperty(String rendering) {
        return getFirstEntityOrNull(owlAnnotationPropertyMap, rendering);
    }


    public OWLNamedIndividual getOWLIndividual(String rendering) {
        return getFirstEntityOrNull(owlIndividualMap, rendering);
    }


    public OWLDatatype getOWLDatatype(String rendering) {
        return getFirstEntityOrNull(owlDatatypeMap, rendering);
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
        entity = getOWLAnnotationProperty(rendering);
        if (entity != null) {
            return entity;
        }
        return null;
    }


    public void addRendering(OWLEntity owlEntity) {
        owlEntity.accept(new OWLEntityVisitor() {
            public void visit(@Nonnull OWLDataProperty entity) {
                addRendering(entity, owlDataPropertyMap);
            }

            public void visit(@Nonnull OWLObjectProperty entity) {
                addRendering(entity, owlObjectPropertyMap);
            }

            public void visit(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
                addRendering(owlAnnotationProperty, owlAnnotationPropertyMap);
            }

            public void visit(@Nonnull OWLNamedIndividual entity) {
                addRendering(entity, owlIndividualMap);
            }

            public void visit(@Nonnull OWLClass entity) {
                addRendering(entity, owlClassMap);
            }

            public void visit(@Nonnull OWLDatatype entity) {
                addRendering(entity, owlDatatypeMap);
            }
        });
    }


    private <T extends OWLEntity> void addRendering(T entity, Multimap<String, T> map) {
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

            public void visit(@Nonnull OWLClass entity) {
                owlClassMap.remove(oldRendering, entity);
            }

            public void visit(@Nonnull OWLDataProperty entity) {
                owlDataPropertyMap.remove(oldRendering, entity);
            }

            public void visit(@Nonnull OWLObjectProperty entity) {
                owlObjectPropertyMap.remove(oldRendering, entity);
            }

            public void visit(@Nonnull OWLAnnotationProperty entity) {
                owlAnnotationPropertyMap.remove(oldRendering, entity);
            }

            public void visit(@Nonnull OWLNamedIndividual entity) {
                owlIndividualMap.remove(oldRendering, entity);
            }

            public void visit(@Nonnull OWLDatatype entity) {
                owlDatatypeMap.remove(oldRendering, entity);
            }
        });
    }


    public void updateRendering(final OWLEntity ent) {
        boolean updateRendering = false;
        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            if (ont.containsEntityInSignature(ent)) {
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
        Set<String> renderings = new HashSet<>(owlClassMap.size() +
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
