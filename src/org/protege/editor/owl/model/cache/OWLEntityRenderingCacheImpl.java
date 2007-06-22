package org.protege.editor.owl.model.cache;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.model.*;

import java.util.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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


    private OWLModelManager owlModelManager;

    private Map<String, OWLClass> owlClassMap;

    private Map<String, OWLObjectProperty> owlObjectPropertyMap;

    private Map<String, OWLDataProperty> owlDataPropertyMap;

    private Map<String, OWLIndividual> owlIndividualMap;

    private Map<OWLEntity, String> entityRenderingMap;

    private OWLOntologyChangeListener listener;


    public OWLEntityRenderingCacheImpl() {
        owlClassMap = new HashMap<String, OWLClass>();
        owlObjectPropertyMap = new HashMap<String, OWLObjectProperty>();
        owlDataPropertyMap = new HashMap<String, OWLDataProperty>();
        owlIndividualMap = new HashMap<String, OWLIndividual>();
        entityRenderingMap = new HashMap<OWLEntity, String>();
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                processChanges(changes);
            }
        };
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
                    boolean added = false;
                    for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
                        if (ont.containsEntityReference(ent)) {
                            addRendering(ent);
                            added = true;
                        }
                    }
                    if (!added) {
                        removeRendering(ent);
                    }
                }
            }
        }
    }


    public void rebuild() {
        clear();
        OWLEntityRenderer entityRenderer = owlModelManager.getOWLEntityRenderer();
        OWLClass thing = owlModelManager.getOWLDataFactory().getOWLThing();
        owlClassMap.put(entityRenderer.render(thing), thing);
        OWLClass nothing = owlModelManager.getOWLDataFactory().getOWLNothing();
        owlClassMap.put(entityRenderer.render(nothing), nothing);
        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            for (OWLClass cls : ont.getReferencedClasses()) {
                if (entityRenderingMap.containsKey(cls)) {
                    continue;
                }
                String rendering = entityRenderer.render(cls);
                owlClassMap.put(rendering, cls);
                entityRenderingMap.put(cls, rendering);
            }
            for (OWLObjectProperty prop : ont.getReferencedObjectProperties()) {
                if (entityRenderingMap.containsKey(prop)) {
                    continue;
                }
                String rendering = entityRenderer.render(prop);
                owlObjectPropertyMap.put(rendering, prop);
                entityRenderingMap.put(prop, rendering);
            }
            for (OWLDataProperty prop : ont.getReferencedDataProperties()) {
                if (entityRenderingMap.containsKey(prop)) {
                    continue;
                }
                String rendering = entityRenderer.render(prop);
                owlDataPropertyMap.put(rendering, prop);
                entityRenderingMap.put(prop, rendering);
            }
            for (OWLIndividual ind : ont.getReferencedIndividuals()) {
                if (entityRenderingMap.containsKey(ind)) {
                    continue;
                }
                String rendering = entityRenderer.render(ind);
                owlIndividualMap.put(rendering, ind);
                entityRenderingMap.put(ind, rendering);
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
        owlIndividualMap.clear();
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


    public OWLIndividual getOWLIndividual(String rendering) {
        return owlIndividualMap.get(rendering);
    }


    public String getRendering(OWLEntity owlEntity) {
        return entityRenderingMap.get(owlEntity);
    }


    public void addRendering(OWLEntity owlEntity) {
        owlEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLDataProperty entity) {
                String rendering = owlModelManager.getOWLEntityRenderer().render(entity);
                owlDataPropertyMap.put(rendering, entity);
                entityRenderingMap.put(entity, rendering);
            }


            public void visit(OWLObjectProperty entity) {
                String rendering = owlModelManager.getOWLEntityRenderer().render(entity);
                owlObjectPropertyMap.put(owlModelManager.getOWLEntityRenderer().render(entity), entity);
                entityRenderingMap.put(entity, rendering);
            }


            public void visit(OWLIndividual entity) {
                String rendering = owlModelManager.getOWLEntityRenderer().render(entity);
                owlIndividualMap.put(owlModelManager.getOWLEntityRenderer().render(entity), entity);
                entityRenderingMap.put(entity, rendering);
            }


            public void visit(OWLClass entity) {
                String rendering = owlModelManager.getOWLEntityRenderer().render(entity);
                owlClassMap.put(owlModelManager.getOWLEntityRenderer().render(entity), entity);
                entityRenderingMap.put(entity, rendering);
            }


            public void visit(OWLDataType dataType) {
            }
        });
    }


    public void removeRendering(OWLEntity owlEntity) {
        owlEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLDataProperty entity) {
                owlDataPropertyMap.remove(owlModelManager.getOWLEntityRenderer().render(entity));
            }


            public void visit(OWLObjectProperty entity) {
                owlObjectPropertyMap.remove(owlModelManager.getOWLEntityRenderer().render(entity));
            }


            public void visit(OWLIndividual entity) {
                owlIndividualMap.remove(owlModelManager.getOWLEntityRenderer().render(entity));
            }


            public void visit(OWLAnonymousIndividual individual) {
            }


            public void visit(OWLDataType dataType) {
            }


            public void visit(OWLClass entity) {
                owlClassMap.remove(owlModelManager.getOWLEntityRenderer().render(entity));
            }
        });
        entityRenderingMap.remove(owlEntity);
    }


    public void updateRendering(final OWLEntity owlEntity) {
        // Need to update the maps
        owlEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLDataProperty entity) {

            }


            public void visit(OWLObjectProperty entity) {
            }


            public void visit(OWLClass entity) {
            }


            public void visit(OWLAnonymousIndividual individual) {
            }


            public void visit(OWLIndividual individual) {
            }


            public void visit(OWLDataType dataType) {
            }


            private void update(OWLEntity entity, Map<String, OWLEntity> map) {
                for (String s : map.keySet()) {
                    OWLEntity ent = map.get(s);
                    if (owlEntity.equals(ent)) {
                        // Remove the original rendering
                        map.remove(s);
                        addRendering(entity);
                        break;
                    }
                }
            }
        });
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


    public Set<String> getOWLIndividualRenderings() {
        return owlIndividualMap.keySet();
    }


    public Set<String> getOWLEntityRenderings() {
        Set<String> renderings = new HashSet<String>(owlClassMap.size() + owlObjectPropertyMap.size() + owlDataPropertyMap.size() + owlIndividualMap.size());
        renderings.addAll(owlClassMap.keySet());
        renderings.addAll(owlObjectPropertyMap.keySet());
        renderings.addAll(owlDataPropertyMap.keySet());
        renderings.addAll(owlIndividualMap.keySet());
        return renderings;
    }
}
