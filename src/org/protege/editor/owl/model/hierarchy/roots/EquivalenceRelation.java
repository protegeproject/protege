package org.protege.editor.owl.model.hierarchy.roots;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Lightweight implementation of an equivalence relation which is optimized for 
 * answering whether two elements are equivalent but not for traversal of an equivalence 
 * class.
 * 
 * @author tredmond
 *
 */
public class EquivalenceRelation<X> {
    private Map<X, EquivalenceClass> equivalenceMap = new HashMap<X, EquivalenceClass>();
    
    public boolean equivalent(X x, X y) {
        return getEquivalenceRepresentative(x).equals(getEquivalenceRepresentative(y));
    }
    
    
    public void merge(Collection<X> toBeMerged) {
        EquivalenceClass merged = new EquivalenceClass();
        for (X x : toBeMerged) {
            EquivalenceClass existingEq = equivalenceMap.put(x, merged);
            if (existingEq != null) {
                existingEq = existingEq.getMostMerged();
                if (!existingEq.equals(merged)) {
                    existingEq.mergedInto = merged;
                }
            }
        }
    }
    
    public Set<X> getEquivalenceClass(X x) {
        Set<X> equivalents = new HashSet<X>();
        EquivalenceClass equiv = getEquivalenceRepresentative(x);
        for (X y : equivalenceMap.keySet()) {
            EquivalenceClass other = getEquivalenceRepresentative(y);
            if (equiv.equals(other)) {
                equivalents.add(y);
            }
        }
        return equivalents;
    }
    
    public void clear() {
        equivalenceMap.clear();
    }
    
    public void logEquivalences(Logger log, Level level) {
        if (log.isEnabledFor(level)) {
            try {
                for (Map.Entry<X, EquivalenceClass> entry : equivalenceMap.entrySet()) {
                    log.log(level, "" + entry.getKey() + " is in equivalence class " + entry.getValue());
                }
            }
            catch (Throwable t) {
                log.info("Could not log equivalence relations", t);
            }
        }
    }
    
    
    private EquivalenceClass getEquivalenceRepresentative(X x) {
        EquivalenceClass equiv = equivalenceMap.get(x);
        if (equiv == null) {
            equiv = new EquivalenceClass();
            equivalenceMap.put(x, equiv);
            return equiv;
        }
        EquivalenceClass mostMergedEquiv = equiv.getMostMerged();
        if (!mostMergedEquiv.equals(equiv)) {
            equivalenceMap.put(x, mostMergedEquiv);
        }
        return mostMergedEquiv;
    }
    
    
    
    private static class EquivalenceClass {
        private static int equivalenceClassCounter = 0;
        private int equivalenceClass;
        private EquivalenceClass mergedInto;
        
        public EquivalenceClass() {
            equivalenceClass = equivalenceClassCounter++;
        }
        
        public EquivalenceClass getMergedInto() {
            return mergedInto;
        }
        
        public EquivalenceClass getMostMerged() {
            EquivalenceClass mostMerged = this;
            while (mostMerged.getMergedInto() != null) {
                mostMerged = mostMerged.getMergedInto();
            }
            return mostMerged;
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof EquivalenceClass)) return false;
            EquivalenceClass me = getMostMerged();
            EquivalenceClass other = (EquivalenceClass) o;
            other = other.getMostMerged();
            return me.equivalenceClass == other.equivalenceClass;
        }
        
        public String toString() {
            return "EqClass[" + equivalenceClass + "]";
        }
        
    }

}

