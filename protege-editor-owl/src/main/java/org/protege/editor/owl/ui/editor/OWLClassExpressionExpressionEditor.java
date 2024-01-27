package org.protege.editor.owl.ui.editor;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.cache.OWLExpressionUserCache;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 26, 2009<br><br>
 */
public class OWLClassExpressionExpressionEditor extends AbstractOWLClassExpressionEditor{

    private static final Logger logger = LoggerFactory.getLogger(OWLClassExpressionExpressionEditor.class);

    private static final String CLASS_EXPRESSION_SYNTAX_URL = "http://protegeproject.github.io/protege/class-expression-syntax";

    private ExpressionEditor<OWLClassExpression> editor;

    private JPanel editorPanel = new JPanel(new BorderLayout(3, 3));

    public void initialise() throws Exception {
        final OWLEditorKit eKit = getOWLEditorKit();
        final OWLExpressionChecker<OWLClassExpression> checker = eKit.getModelManager().getOWLExpressionCheckerFactory().getOWLClassExpressionChecker();
        editor = new ExpressionEditor<>(eKit, checker);
        JScrollPane sp = new JScrollPane(editor);
        editorPanel.add(sp);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editorPanel.add(buttonPanel, BorderLayout.SOUTH);
        JButton helpButton = new JButton("Help...");
        buttonPanel.add(helpButton);
        helpButton.addActionListener(e -> showClassExpressionSyntaxHelp());
    }

    private void showClassExpressionSyntaxHelp() {
        try {
            Desktop.getDesktop().browse(URI.create(CLASS_EXPRESSION_SYNTAX_URL));
        } catch (IOException ex) {
            logger.warn("An error occurred when attempting to display the Class Expression documentation: {}",
                        ex.getMessage(),
                        ex);
        }
    }


    public JComponent getComponent() {
        return editorPanel;
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
                OWLExpressionUserCache.getInstance(getOWLEditorKit().getModelManager()).add(owlDescription, editor.getText().trim());
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
