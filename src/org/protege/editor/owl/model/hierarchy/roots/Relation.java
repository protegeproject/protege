package org.protege.editor.owl.model.hierarchy.roots;

import java.util.Collection;

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
