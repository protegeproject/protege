package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.OWLEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public abstract class EntityReferencingAxiomsStrategy<O extends OWLEntity> implements AxiomSelectionStrategy {

    private Set<O> entities = new HashSet<O>();

    public void setEntities(Set<O> entities){
        this.entities = new HashSet<O>(entities);
    }

    protected Set<O> getEntities(){
        return Collections.unmodifiableSet(entities);
    }

    public abstract Class<O> getType();
}
