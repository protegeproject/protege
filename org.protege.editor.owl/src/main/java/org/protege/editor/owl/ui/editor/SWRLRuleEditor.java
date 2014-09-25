package org.protege.editor.owl.ui.editor;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionCheckerFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.SWRLRule;


/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 06-Jul-2007<br><br>
 */
public class SWRLRuleEditor extends AbstractOWLObjectEditor<SWRLRule> implements VerifiedInputEditor {

    private static final String PREFIX = "Rule: ";

    private OWLModelManager mngr;

    private ExpressionEditor<SWRLRule> editor;

    private JScrollPane scrollpane;
    
    public SWRLRuleEditor(OWLEditorKit editorKit) {
        final OWLExpressionCheckerFactory fac = editorKit.getModelManager().getOWLExpressionCheckerFactory();
        editor = new ExpressionEditor<>(editorKit, fac.getSWRLChecker());

        scrollpane = new JScrollPane(editor);
        scrollpane.setPreferredSize(new Dimension(500, 200));
        
        mngr = editorKit.getModelManager();
    }


    public String getEditorTypeName() {
        return "SWRL Rule";
    }


    public boolean canEdit(Object object) {
        return object instanceof SWRLRule;
    }


    public JComponent getEditorComponent() {
        return scrollpane;
    }


    public SWRLRule getEditedObject() {
        try {
            return editor.createObject();
        }
        catch (OWLException e) {
            throw new OWLRuntimeException(e);
        }
    }

  /*
   * Workaround for owlapi feature request 2896097.  Remove this fix when 
   * the simple rule renderer and parser is implemented.  Svn at time of 
   * commit is approximately 16831
   */
    public boolean setEditedObject(SWRLRule rule) {
        if (rule == null){
            editor.setText(PREFIX);
        }
        else{
            editor.setText(PREFIX + mngr.getRendering(rule));
        }
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
