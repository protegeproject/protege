package org.protege.editor.owl.model.hierarchy.tabbed;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16 Sep 16
 */
public class CreateHierarchyChanges<E extends OWLEntity> {

    private final ImmutableList<OWLOntologyChange> changes;

    private final ImmutableSetMultimap<E, E> parentChildMap;

    public CreateHierarchyChanges(ImmutableList<OWLOntologyChange> changes, ImmutableSetMultimap<E, E> parentChildMap) {
        this.changes = checkNotNull(changes);
        this.parentChildMap = checkNotNull(parentChildMap);
    }

    public ImmutableList<OWLOntologyChange> getChanges() {
        return changes;
    }

    public ImmutableSetMultimap<E, E> getParentChildMap() {
        return parentChildMap;
    }
}
