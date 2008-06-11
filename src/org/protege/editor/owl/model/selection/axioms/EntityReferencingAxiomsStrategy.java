package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.OWLEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public abstract class EntityReferencingAxiomsStrategy<O extends OWLEntity> extends AbstractAxiomSelectionStrategy {

    private Set<O> entities = new HashSet<O>();

    public static final String ENTITIES_CHANGED = "change.entities";


    public void setEntities(Set<O> entities){
        this.entities = new HashSet<O>(entities);
        notifyPropertyChange(ENTITIES_CHANGED);
    }

    protected Set<O> getEntities(){
        return Collections.unmodifiableSet(entities);
    }

    public abstract Class<O> getType();
}
