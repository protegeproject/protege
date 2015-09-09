package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-May-2007<br><br>
 */
public class OWLDataPropertyRelationshipEditor extends AbstractOWLObjectEditor<OWLDataPropertyConstantPair> implements VerifiedInputEditor {


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


    public String getEditorTypeName() {
        return "Data property assertion";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLDataPropertyConstantPair;
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


    public boolean setEditedObject(OWLDataPropertyConstantPair editedObject) {
        if (editedObject == null){
            constantEditorComponent.setEditedObject(null);
        }
        else{
            dataPropertySelectorPanel.setSelection(editedObject.getProperty());
            constantEditorComponent.setEditedObject(editedObject.getConstant());
        }
        return true;
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
