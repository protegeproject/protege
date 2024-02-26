package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntology;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Oct-2007<br><br>
 */
public class OWLOntologyHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLOntology> {
	
	/*
	 * The internal state of this class is synchronized by the roots object.
	 */

    private Set<OWLOntology> roots;

    private Map<OWLOntology, Set<OWLOntology>> parent2ChildMap;

    private Map<OWLOntology, Set<OWLOntology>> child2ParentMap;

    private OWLModelManager mngr;

    private OWLModelManagerListener modelManagerListener = event -> {
        if(event.isType(EventType.ONTOLOGY_LOADED) ||
           event.isType(EventType.ONTOLOGY_RELOADED) ||
           event.isType(EventType.ONTOLOGY_CREATED)) {
            rebuild();
        }
    };


    public OWLOntologyHierarchyProvider(OWLModelManager mngr) {
        super(mngr.getOWLOntologyManager());
        this.mngr = mngr;
        roots = new HashSet<>();
        parent2ChildMap = new HashMap<>();
        child2ParentMap = new HashMap<>();
        rebuild();
        mngr.addListener(modelManagerListener);

    }


    public void setOntologies(Set<OWLOntology> ontologies) {
    }

    private void rebuild() {
    	synchronized (roots) {
    		roots.clear();
    		parent2ChildMap.clear();
    		child2ParentMap.clear();
    		for(OWLOntology ont : mngr.getOntologies()) {
    			for(OWLOntology imp : mngr.getOWLOntologyManager().getImports(ont)) {
    				add(ont, imp);
    			}
    		}
    		for(OWLOntology ont : mngr.getOntologies()) {
    			if(!child2ParentMap.containsKey(ont)) {
    				roots.add(ont);
    			}
    		}
    	}
        fireHierarchyChanged();
    }

    /*
     * only called inside of rebuild so the roots lock is taken.
     */
    private void add(OWLOntology ont, OWLOntology imp) {
        getChildren(ont, true).add(imp);
        getParents(imp, true).add(ont);
    }

    private Set<OWLOntology> getChildren(OWLOntology parent, boolean add) {
    	synchronized (roots) {
    		Set<OWLOntology> children = parent2ChildMap.get(parent);
    		if(children == null) {
    			children = new HashSet<>();
    			if(add) {
    				parent2ChildMap.put(parent, children);
    			}
    		}
    		return children;
    	}
    }

    private Set<OWLOntology> getParents(OWLOntology child, boolean add) {
    	synchronized (roots) {
    		Set<OWLOntology> parents = child2ParentMap.get(child);
    		if(parents == null) {
    			parents = new HashSet<>();
    			if(add) {
    				child2ParentMap.put(child, parents);
    			}
    		}
    		return parents;
    	}
    }


    public Set<OWLOntology> getRoots() {
    	synchronized (roots) {
    		return Collections.unmodifiableSet(roots);
    	}
    }


    public Set<OWLOntology> getParents(OWLOntology object) {
        return getParents(object, true);
    }


    public Set<OWLOntology> getEquivalents(OWLOntology object) {
        return Collections.emptySet();
    }


    public Set<OWLOntology> getUnfilteredChildren(OWLOntology object) {
        return getChildren(object, true);
    }


    public boolean containsReference(OWLOntology object) {
    	synchronized (roots) {
    		return parent2ChildMap.containsKey(object) ||
    				roots.contains(object);
    	}
    }


    public void dispose() {
        super.dispose();
        mngr.removeListener(modelManagerListener);
    }
}
