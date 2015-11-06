package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLObjectPropertySetEditor extends AbstractOWLObjectEditor<Set<OWLObjectPropertyExpression>> implements VerifiedInputEditor {

    private ExpressionEditor<Set<OWLObjectPropertyExpression>> editor;


    public OWLObjectPropertySetEditor(OWLEditorKit eKit) {
        OWLExpressionChecker<Set<OWLObjectPropertyExpression>> checker = eKit.getModelManager().getOWLExpressionCheckerFactory().getObjectPropertySetChecker();
        editor = new ExpressionEditor<Set<OWLObjectPropertyExpression>>(eKit, checker);
        editor.setPreferredSize(new Dimension(300, 200));
    }


    public String getEditorTypeName() {
        return "Set of properties";
    }


    public boolean canEdit(Object object) {
        return checkSet(object, OWLPropertyExpression.class);
    }


    public JComponent getEditorComponent() {
        return editor;
    }


    public Set<OWLObjectPropertyExpression> getEditedObject() {
        try {
            return editor.createObject();
        }
        catch (OWLException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public boolean setEditedObject(Set<OWLObjectPropertyExpression> editedObject) {
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
