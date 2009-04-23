package org.protege.editor.owl.ui.frame.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLLiteral;
import org.semanticweb.owl.model.OWLPropertyAssertionAxiom;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

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
public class OWLDataPropertyRelationshipEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDataPropertyConstantPair> implements VerifiedInputEditor {


    private OWLConstantEditor constantEditorComponent;

    private OWLDataPropertySelectorPanel dataPropertySelectorPanel;

    private JPanel componentHolder;

    private java.util.List<InputVerificationStatusChangedListener> listeners = new ArrayList<InputVerificationStatusChangedListener>();

    private boolean currentStatus = false;

    private ChangeListener changeListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event) {
            checkStatus();
        }
    };


    public OWLDataPropertyRelationshipEditor(OWLEditorKit owlEditorKit) {

        final Border paddingBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

        dataPropertySelectorPanel = new OWLDataPropertySelectorPanel(owlEditorKit);
        dataPropertySelectorPanel.setBorder(BorderFactory.createCompoundBorder(paddingBorder,
                                                                               BorderFactory.createTitledBorder("Data Property")));
        dataPropertySelectorPanel.addSelectionListener(changeListener);

        constantEditorComponent = new OWLConstantEditor(owlEditorKit);
        constantEditorComponent.setBorder(paddingBorder);

        componentHolder = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(dataPropertySelectorPanel);
        splitPane.setRightComponent(constantEditorComponent);
        componentHolder.add(splitPane);
    }


    public void setDataPropertyAxiom(OWLPropertyAssertionAxiom<OWLDataPropertyExpression, OWLLiteral> ax) {
        OWLDataPropertyExpression p = ax.getProperty();
        if (p instanceof OWLDataProperty){
            dataPropertySelectorPanel.setSelection((OWLDataProperty)p);
        }
        constantEditorComponent.setEditedObject(ax.getObject());
    }


    public JComponent getEditorComponent() {
        return componentHolder;
    }


    public OWLDataPropertyConstantPair getEditedObject() {
        OWLDataProperty prop = dataPropertySelectorPanel.getSelectedObject();
        if (prop == null) {
            return null;
        }
        OWLLiteral con = constantEditorComponent.getEditedObject();
        if (con == null) {
            return null;
        }
        return new OWLDataPropertyConstantPair(prop, con);
    }


    public void clear() {
        constantEditorComponent.setEditedObject(null);
    }


    public void dispose() {
        dataPropertySelectorPanel.dispose();
        listeners.clear();
    }


    private void checkStatus(){
        boolean status = dataPropertySelectorPanel.getSelectedObject() != null &&
                         constantEditorComponent.getEditedObject() != null;
        if (status != currentStatus){
            currentStatus = status;
            for (InputVerificationStatusChangedListener l : listeners){
                l.verifiedStatusChanged(currentStatus);
            }
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        listener.verifiedStatusChanged(currentStatus);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }
}
