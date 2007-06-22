package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
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
 * Medical Informatics Group<br>
 * Date: Apr 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AddSiblingClassAction extends OWLSelectionViewAction {

    private OWLObjectTree<OWLClass> tree;

    private OWLEditorKit owlEditorKit;


    public void dispose() {
    }


    public AddSiblingClassAction(OWLEditorKit owlEditorKit, OWLObjectTree<OWLClass> tree) {
        super("Add sibling class", OWLIcons.getIcon("class.add.sib.png"));
        putValue(AbstractAction.ACCELERATOR_KEY, OWLWorkspace.getCreateSibKeyStroke());
        this.owlEditorKit = owlEditorKit;
        this.tree = tree;
    }


    public void updateState() {
        if (tree.getSelectedOWLObject() == null) {
            setEnabled(false);
            return;
        }
        OWLClass thing = owlEditorKit.getOWLModelManager().getOWLDataFactory().getOWLThing();
        setEnabled(!tree.getSelectedOWLObject().equals(thing));
        return;
    }


    protected boolean canPerform(OWLClass cls) {
        return cls != null;
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        OWLClass cls = tree.getSelectedOWLObject();
        if (cls == null) {
            // Shouldn't really get here, because the
            // action should be disabled
            return;
        }
        // We need to apply the changes in the active ontology
        OWLEntityCreationSet<OWLClass> creationSet = owlEditorKit.getOWLWorkspace().createOWLClass();
        if (creationSet == null) {
            return;
        }
        // Combine the changes that are required to create the OWLClass, with the
        // changes that are required to make it a sibling class.
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(creationSet.getOntologyChanges());
        OWLModelManager owlModelManager = owlEditorKit.getOWLModelManager();
        for (OWLClass par : owlModelManager.getOWLClassHierarchyProvider().getParents(cls)) {
            OWLDataFactory df = owlModelManager.getOWLDataFactory();
            OWLAxiom ax = df.getOWLSubClassAxiom(creationSet.getOWLEntity(), par);
            changes.add(new AddAxiom(owlModelManager.getActiveOntology(), ax));
        }
        owlModelManager.applyChanges(changes);
        // Select the new class
        tree.setSelectedOWLObject(creationSet.getOWLEntity());
    }
}
