package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
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
 * Date: Apr 3, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AddSubClassAction extends AbstractOWLClassTreeAction {

    private OWLEditorKit owlEditorKit;

    private OWLObjectTree<OWLClass> tree;


    public AddSubClassAction(OWLEditorKit owlEditorKit, OWLObjectTree<OWLClass> tree) {
        super("Add subclass", OWLIcons.getIcon("class.add.sub.png"), tree.getSelectionModel());
        this.owlEditorKit = owlEditorKit;
        this.tree = tree;
        setEnabled(false);
        putValue(AbstractAction.ACCELERATOR_KEY, OWLWorkspace.getCreateSubKeyStroke());
    }


    protected boolean canPerform(OWLClass cls) {
        return cls != null;
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        OWLClass cls = getSelectedOWLClass();
        if (cls == null) {
            return;
        }
        OWLEntityCreationSet<OWLClass> creationSet = owlEditorKit.getOWLWorkspace().createOWLClass();
        if (creationSet == null) {
            return;
        }
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(creationSet.getOntologyChanges());
        OWLDataFactory df = owlEditorKit.getOWLModelManager().getOWLDataFactory();
        OWLSubClassAxiom ax = df.getOWLSubClassAxiom(creationSet.getOWLEntity(), cls);
        changes.add(new AddAxiom(owlEditorKit.getOWLModelManager().getActiveOntology(), ax));
        owlEditorKit.getOWLModelManager().applyChanges(changes);
        tree.setSelectedOWLObject(creationSet.getOWLEntity());
    }
}
