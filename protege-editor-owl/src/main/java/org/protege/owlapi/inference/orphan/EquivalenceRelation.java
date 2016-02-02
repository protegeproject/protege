package org.protege.owlapi.inference.orphan;

import org.protege.editor.core.log.LogBanner;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * Lightweight implementation of an equivalence relation which is optimized for
 * answering whether two elements are equivalent but not for traversal of an equivalence
 * class.
 *
 * @author tredmond
 */
public class EquivalenceRelation<X extends Comparable<? super X>> {

    private Map<X, Set<X>> equivalenceMap = new HashMap<>();

    public boolean equivalent(X x, X y) {
        Set<X> equivalentToX = equivalenceMap.get(x);
        return x.equals(y) || (equivalentToX != null && equivalentToX.contains(y));
    }


    public void merge(Collection<X> toBeMerged) {
        if (toBeMerged.size() <= 1) {
            return;
        }
        Set<X> equivalenceClass = new TreeSet<>();
        for (X x : toBeMerged) {
            Set<X> existingEquivalences = equivalenceMap.put(x, equivalenceClass);
            if (existingEquivalences != null) {
                equivalenceClass.addAll(existingEquivalences);
            }
            else {
                equivalenceClass.add(x);
            }
        }
    }

    public Set<X> getEquivalenceClass(X x) {
        Set<X> equivalents = equivalenceMap.get(x);
        return equivalents != null ? new TreeSet<>(equivalents) : Collections.singleton(x);
    }

    public void logEquivalences(Logger log) {
        if (log.isDebugEnabled()) {
            log.debug(LogBanner.start("Logging equivalences"));
            Set<X> displayed = new HashSet<>();
            for (Entry<X, Set<X>> entry : equivalenceMap.entrySet()) {
                X x = entry.getKey();
                Set<X> equivalences = entry.getValue();
                if (!displayed.contains(x)) {
                    log.debug("The following are equivalent: {}", equivalences);
                    displayed.addAll(equivalences);
                }

            }
            log.debug(LogBanner.end());
        }
    }

    public void clear() {
        equivalenceMap.clear();
    }

}

