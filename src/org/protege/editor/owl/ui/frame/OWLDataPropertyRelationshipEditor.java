package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLPropertyAssertionAxiom;

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
 * Bio-Health Informatics Group<br>
 * Date: 20-May-2007<br><br>
 */
public class OWLDataPropertyRelationshipEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDataPropertyConstantPair> {

    private OWLEditorKit owlEditorKit;

    private OWLConstantEditorComponent constantEditorComponent;

    private OWLDataPropertySelectorPanel dataPropertySelectorPanel;

    private JPanel componentHolder;


    public OWLDataPropertyRelationshipEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        this.dataPropertySelectorPanel = new OWLDataPropertySelectorPanel(owlEditorKit);
        constantEditorComponent = new OWLConstantEditorComponent(owlEditorKit);
        componentHolder = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(dataPropertySelectorPanel);
        splitPane.setRightComponent(constantEditorComponent);
        componentHolder.add(splitPane);
        constantEditorComponent.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                                                             BorderFactory.createEmptyBorder(5,
                                                                                                             5,
                                                                                                             5,
                                                                                                             5)));
    }

    public void setDataPropertyAxiom(OWLPropertyAssertionAxiom<OWLDataPropertyExpression, OWLConstant> ax) {
        OWLDataPropertyExpression p = ax.getProperty();
        if (p instanceof OWLDataProperty){
            dataPropertySelectorPanel.setSelection((OWLDataProperty)p);
        }
        constantEditorComponent.setOWLConstant(ax.getObject());
    }


    public JComponent getEditorComponent() {
        return componentHolder;
    }


    public OWLDataPropertyConstantPair getEditedObject() {
        OWLDataProperty prop = dataPropertySelectorPanel.getSelectedObject();
        if (prop == null) {
            return null;
        }
        OWLConstant con = constantEditorComponent.getOWLConstant();
        if (con == null) {
            return null;
        }
        return new OWLDataPropertyConstantPair(prop, con);
    }


    public void clear() {
        constantEditorComponent.setOWLConstant(null);
    }


    public void dispose() {
        dataPropertySelectorPanel.dispose();
    }
}
