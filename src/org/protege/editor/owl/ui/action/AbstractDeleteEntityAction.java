package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.util.OWLEntityRemover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
 * Date: 20-Apr-2007<br><br>
 */
public abstract class AbstractDeleteEntityAction<E extends OWLEntity> extends OWLSelectionViewAction {

    private OWLEditorKit owlEditorKit;


    protected AbstractDeleteEntityAction(String name, Icon icon, OWLEditorKit owlEditorKit) {
        super(name, icon);
        this.owlEditorKit = owlEditorKit;
    }


    public void updateState() {
        setEnabled(!getSelectedEntities().isEmpty());
    }


    public void dispose() {
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void actionPerformed(ActionEvent e) {
        Set<E> selents = getSelectedEntities();
        String name;
        if (selents.size() == 1) {
            name = owlEditorKit.getOWLModelManager().getOWLEntityRenderer().render(selents.iterator().next());
        }
        else {
            name = getPluralDescription();
        }
        JRadioButton onlySelectedEntityRadioButton = new JRadioButton("Delete " + name + " only");
        JRadioButton decendantsRadioButton = new JRadioButton("Delete " + name + " and asserted descendant " + getPluralDescription());
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
                ents.addAll(getHierarchyProvider().getDescendants(ent));
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


    protected abstract Set<E> getSelectedEntities();


    protected abstract OWLObjectHierarchyProvider<E> getHierarchyProvider();


    protected void notifySelectionChange() {
        updateState();
    }


    /**
     * Returns the plural name of a set of entities e.g.
     * classes, properties, individuals. This is used in
     * the UI e.g. "Delete selected classes"
     */
    protected abstract String getPluralDescription();
}
