package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.semanticweb.owlapi.model.OWLClassExpression;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 26, 2009<br><br>
 */
public class OWLClassSelectorWrapper extends AbstractOWLClassExpressionEditor {


    private OWLClassSelectorPanel component;


    public void initialise()  {
        component = new OWLClassSelectorPanel(getOWLEditorKit());
    }


    public JComponent getComponent() {
        return component;
    }


    public boolean isValidInput() {
        return component.getSelectedObjects() != null && !component.getSelectedObjects().isEmpty();
    }


    public boolean setDescription(OWLClassExpression description) {
        if (description == null){
            return true;
        }
        if (!description.isAnonymous()){
            component.setSelection(description.asOWLClass());
            return true;
        }
        return false;
    }


    public Set<OWLClassExpression> getClassExpressions() {
        return new HashSet<OWLClassExpression>(component.getSelectedObjects());
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener l) {
        component.addStatusChangedListener(l);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        component.removeStatusChangedListener(l);
    }


    public void dispose() {
        component.dispose();
    }
}
