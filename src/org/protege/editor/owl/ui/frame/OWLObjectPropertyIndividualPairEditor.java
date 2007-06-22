package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;

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
 * Date: 22-Feb-2007<br><br>
 */
public class OWLObjectPropertyIndividualPairEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLObjectPropertyIndividualPair> {

    private JPanel editorPanel;

    private OWLObjectPropertySelectorPanel objectPropertyPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;


    public OWLObjectPropertyIndividualPairEditor(OWLEditorKit owlEditorKit) {
        editorPanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        objectPropertyPanel = new OWLObjectPropertySelectorPanel(owlEditorKit);
        splitPane.setLeftComponent(objectPropertyPanel);
        individualSelectorPanel = new OWLIndividualSelectorPanel(owlEditorKit);
        splitPane.setRightComponent(individualSelectorPanel);
        editorPanel.add(splitPane);
    }


    public void setObjectPropertyIndividual(OWLObjectPropertyAssertionAxiom ax) {

    }


    public void clear() {
    }


    public OWLObjectPropertyIndividualPair getEditedObject() {
        return new OWLObjectPropertyIndividualPair(objectPropertyPanel.getSelectedOWLObjectProperty(),
                                                   individualSelectorPanel.getSelectedIndividual());
    }


    public Set<OWLObjectPropertyIndividualPair> getEditedObjects() {
        Set<OWLObjectPropertyIndividualPair> pairs = new HashSet<OWLObjectPropertyIndividualPair>();
        for (OWLObjectProperty prop : objectPropertyPanel.getSelectedOWLObjectProperties()) {
            for (OWLIndividual ind : individualSelectorPanel.getSelectedIndividuals()) {
                pairs.add(new OWLObjectPropertyIndividualPair(prop, ind));
            }
        }
        return pairs;
    }


    public JComponent getEditorComponent() {
        return editorPanel;
    }


    public void dispose() {
        objectPropertyPanel.dispose();
        individualSelectorPanel.dispose();
    }
}
