package org.protege.editor.owl.ui.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 */
public abstract class OWLObjectHierarchyCreator<E extends OWLEntity> {

    private OWLEditorKit editorKit;

    private OWLEntitySetProvider<E> entitySetProvider;

    private OWLObjectHierarchyProvider<E> hierarchyProvider;


    protected OWLObjectHierarchyCreator(OWLEditorKit editorKit, OWLEntitySetProvider<E> entitySetProvider,
                                        OWLObjectHierarchyProvider<E> hierarchyProvider) {
        this.editorKit = editorKit;
        this.entitySetProvider = entitySetProvider;
        this.hierarchyProvider = hierarchyProvider;
    }


    public abstract OWLEntityCreationSet<E> createEntity();


    public void createNewEntity() {
        OWLEntityCreationSet<E> creationSet = createEntity();
        editorKit.getOWLModelManager().applyChanges(creationSet.getOntologyChanges());
    }


    public void createChildEntity() {
        OWLEntityCreationSet<E> creationSet = createEntity();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(changes);
        changes.addAll(createChild(entitySetProvider.getEntities(), creationSet.getOWLEntity()));
        editorKit.getOWLModelManager().applyChanges(changes);
    }


    protected abstract List<OWLOntologyChange> createChild(Set<E> parents, E child);


    protected abstract List<OWLOntologyChange> createSibling(Set<E> siblings, E sibling);
}
