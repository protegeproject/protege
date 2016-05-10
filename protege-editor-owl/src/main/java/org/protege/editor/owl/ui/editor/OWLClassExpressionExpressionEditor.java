package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.cache.OWLExpressionUserCache;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;

import javax.swing.*;
import java.util.Collections;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 26, 2009<br><br>
 */
public class OWLClassExpressionExpressionEditor extends AbstractOWLClassExpressionEditor{

    private ExpressionEditor<OWLClassExpression> editor;

    private JScrollPane scroller;

    public void initialise() throws Exception {
        final OWLEditorKit eKit = getOWLEditorKit();
        final OWLExpressionChecker<OWLClassExpression> checker = eKit.getModelManager().getOWLExpressionCheckerFactory().getOWLClassExpressionChecker();
        editor = new ExpressionEditor<>(eKit, checker);

        scroller = new JScrollPane(editor);
    }


    public JComponent getComponent() {
        return scroller;
    }


    public boolean isValidInput() {
        return editor.isWellFormed();
    }


    public boolean setDescription(OWLClassExpression description) {
        editor.setExpressionObject(description);
        return true;
    }


    public Set<OWLClassExpression> getClassExpressions() {
        try {
            if (editor.isWellFormed()) {
                OWLClassExpression owlDescription = editor.createObject();
                OWLExpressionUserCache.getInstance(getOWLEditorKit().getModelManager()).add(owlDescription, editor.getText());
                return Collections.singleton(owlDescription);
            }
            else {
                return null;
            }
        }
        catch (OWLException e) {
            return null;
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener l) {
        editor.addStatusChangedListener(l);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        editor.removeStatusChangedListener(l);
    }


    public void dispose() throws Exception {
        // surely ExpressionEditor should be disposable?
    }
}
