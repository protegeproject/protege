package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.OWLEntitySetProvider;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
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
 * Date: 02-May-2007<br><br>
 */
public class OWLObjectHierarchyDeleter<E extends OWLEntity> {

    private OWLEditorKit owlEditorKit;

    private OWLEntitySetProvider<E> entitySetProvider;

    private OWLObjectHierarchyProvider<E> hierarchyProvider;

    private String pluralName;


    public OWLObjectHierarchyDeleter(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<E> hierarchyProvider,
                                     OWLEntitySetProvider<E> entitySetProvider, String pluralName) {
        this.owlEditorKit = owlEditorKit;
        this.hierarchyProvider = hierarchyProvider;
        this.entitySetProvider = entitySetProvider;
        this.pluralName = pluralName;
    }


    public void dispose() {
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void performDeletion() {
        Set<E> selents = entitySetProvider.getEntities();
        String name;
        if (selents.size() == 1) {
            name = owlEditorKit.getOWLModelManager().getOWLEntityRenderer().render(selents.iterator().next());
        }
        else {
            name = pluralName;
        }
        JRadioButton onlySelectedEntityRadioButton = new JRadioButton("Delete " + name + " only");
        JRadioButton decendantsRadioButton = new JRadioButton("Delete " + name + " and asserted descendant " + pluralName);
        JPanel panel = new JPanel(new BorderLayout(2, 2));
        panel.add(onlySelectedEntityRadioButton, BorderLayout.NORTH);
        panel.add(decendantsRadioButton, BorderLayout.SOUTH);
        ButtonGroup bg = new ButtonGroup();
        bg.add(onlySelectedEntityRadioButton);
        bg.add(decendantsRadioButton);
        onlySelectedEntityRadioButton.setSelected(true);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        int ret = JOptionPane.showConfirmDialog(owlEditorKit.getOWLWorkspace(),
                                                panel,
                                                "Delete",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (ret != JOptionPane.OK_OPTION) {
            return;
        }
        if (onlySelectedEntityRadioButton.isSelected()) {
            delete(selents);
        }
        else {
            Set<E> ents = new HashSet<E>();
            for (E ent : selents) {
                ents.add(ent);
                ents.addAll(hierarchyProvider.getDescendants(ent));
            }
            delete(ents);
        }
    }


    private void delete(Set<E> ents) {
        OWLEntityRemover remover = new OWLEntityRemover(owlEditorKit.getOWLModelManager().getOWLOntologyManager(),
                                                        owlEditorKit.getOWLModelManager().getOntologies());
        for (E ent : ents) {
            ent.accept(remover);
        }
        owlEditorKit.getOWLModelManager().applyChanges(remover.getChanges());
    }
}
