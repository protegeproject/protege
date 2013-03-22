package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.HashSet;
import java.util.Set;

public class CollectingHierarchyListener implements OWLObjectHierarchyProviderListener<OWLClass> {
    
    private Set<OWLClass> collected = new HashSet<OWLClass>();

    public void hierarchyChanged() {
        ;
    }

    public void nodeChanged(OWLClass node) {
        collected.add(node);
    }
    
    public void clear() {
        collected.clear();
    }
    
    public Set<OWLClass> getCollectedNodes() {
        return new HashSet<OWLClass>(collected);
    }

}
