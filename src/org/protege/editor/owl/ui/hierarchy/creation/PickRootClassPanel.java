package org.protege.editor.owl.ui.hierarchy.creation;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.OWLClass;

import javax.swing.*;
import java.awt.*;
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
 * Date: 17-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PickRootClassPanel extends AbstractOWLWizardPanel {

    public static final String ID = "PickRootClassPanel";

    private OWLModelManagerTree<OWLClass> tree;


    public PickRootClassPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Pick root class", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please select the root class");
        tree = new OWLModelManagerTree<OWLClass>(getOWLEditorKit(),
                                                 getOWLModelManager().getOWLClassHierarchyProvider());
        tree.setSelectedOWLObject(getOWLEditorKit().getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
        parent.add(ComponentFactory.createScrollPane(tree));
    }


    public void displayingPanel() {
        tree.requestFocus();
    }


    public OWLClass getRootClass() {
        OWLClass cls = tree.getSelectedOWLObject();
        if (cls == null) {
            return getOWLModelManager().getOWLDataFactory().getOWLThing();
        }
        else {
            return tree.getSelectedOWLObject();
        }
    }


    public Object getNextPanelDescriptor() {
        return TabIndentedHierarchyPanel.ID;
    }


    public void dispose() {
        tree.dispose();
    }
}
