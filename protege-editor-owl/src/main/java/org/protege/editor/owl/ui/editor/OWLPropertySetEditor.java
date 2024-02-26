package org.protege.editor.owl.ui.editor;

import java.awt.Dimension;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLPropertySetEditor extends AbstractOWLObjectEditor<Set<OWLPropertyExpression>> implements VerifiedInputEditor {

    private ExpressionEditor<Set<OWLPropertyExpression>> editor;


    public OWLPropertySetEditor(OWLEditorKit eKit) {
        OWLExpressionChecker<Set<OWLPropertyExpression>> checker = eKit.getModelManager().getOWLExpressionCheckerFactory().getPropertySetChecker();
        editor = new ExpressionEditor<>(eKit, checker);
        editor.setPreferredSize(new Dimension(300, 200));
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Set of properties";
    }


    public boolean canEdit(Object object) {
        return checkSet(object, OWLPropertyExpression.class);
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return editor;
    }


    @Nullable
    public Set<OWLPropertyExpression> getEditedObject() {
        try {
            return editor.createObject();
        }
        catch (OWLException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public boolean setEditedObject(Set<OWLPropertyExpression> editedObject) {
        editor.setExpressionObject(editedObject);
        return true;
    }


    public void dispose() {
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.addStatusChangedListener(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.removeStatusChangedListener(listener);
    }
}
