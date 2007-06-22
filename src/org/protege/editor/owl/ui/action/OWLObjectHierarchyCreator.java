package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.util.OWLEntitySetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
