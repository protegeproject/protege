package org.protege.editor.owl.model.hierarchy.roots;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This interface simply represents a relation
 *     R: X -> X.
 * 
 * @author tredmond
 *
 */

public interface Relation<X> {
    
    Collection<X> getR(X x);
}
