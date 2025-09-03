package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Feb-2007<br><br>
 */
public class OWLClassExpressionSetEditor extends AbstractOWLObjectEditor<Set<OWLClassExpression>> implements VerifiedInputEditor {

    private final Logger logger = LoggerFactory.getLogger(OWLClassExpressionSetEditor.class);


    private OWLEditorKit owlEditorKit;

    private OWLClassSelectorPanel classSelectorPanel;

    private JComponent editorComponent;

    private ExpressionEditor<Set<OWLClassExpression>> expressionEditor;

    private JTabbedPane tabbedPane;

    private java.util.List<OWLClassExpression> initialSelection;

    private java.util.List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();

    private ChangeListener changeListener = event -> {
        for (InputVerificationStatusChangedListener l : listeners){
            l.verifiedStatusChanged(isValid());
        }
    };


    public OWLClassExpressionSetEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }

    public OWLClassExpressionSetEditor(OWLEditorKit owlEditorKit, java.util.List<OWLClassExpression> selectedClasses) {
        this.owlEditorKit = owlEditorKit;
        this.initialSelection = selectedClasses;
    }

    private void createEditor() {
        editorComponent = new JPanel(new BorderLayout());

        final OWLExpressionChecker<Set<OWLClassExpression>> checker = owlEditorKit.getModelManager().getOWLExpressionCheckerFactory().getOWLClassExpressionSetChecker();
        expressionEditor = new ExpressionEditor<>(owlEditorKit, checker);
        JPanel holderPanel = new JPanel(new BorderLayout());
        holderPanel.add(expressionEditor);
        holderPanel.setPreferredSize(new Dimension(500, 400));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        if (initialSelection == null){
            classSelectorPanel = new OWLClassSelectorPanel(owlEditorKit);
        }
        else{
            Set<OWLClass> clses = getNamedClassesFromInitialSelection();
            if (clses.size() == initialSelection.size()){ // only show and initialise the tree if all are named
                classSelectorPanel = new OWLClassSelectorPanel(owlEditorKit);
                classSelectorPanel.setSelection(clses);
            }
            expressionEditor.setText(generateListText());
        }

        if (classSelectorPanel != null){
            classSelectorPanel.addSelectionListener(changeListener);

            tabbedPane = new JTabbedPane();
            tabbedPane.add("Class hierarchy", classSelectorPanel);
            tabbedPane.add("Expression editor", holderPanel);
            tabbedPane.addChangeListener(changeListener);
            editorComponent.add(tabbedPane, BorderLayout.CENTER);
        }
        else{
            editorComponent.add(holderPanel, BorderLayout.CENTER);
        }
    }


    private String generateListText() {
        StringBuilder sb = new StringBuilder();
        for (OWLClassExpression c : initialSelection){
            if (sb.length() > 0){
                sb.append(", ");
            }
            sb.append(owlEditorKit.getModelManager().getRendering(c));
        }
        return sb.toString();
    }


    private Set<OWLClass> getNamedClassesFromInitialSelection() {
        Set<OWLClass> clses = new HashSet<>();
        if (initialSelection != null){
            for (OWLClassExpression descr : initialSelection){
                if (!descr.isAnonymous()){
                    clses.add(descr.asOWLClass());
                }
            }
        }
        return clses;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Set of OWL Class Expressions";
    }


    public boolean canEdit(Object object) {
        return checkSet(object, OWLClassExpression.class);
    }


    @Nonnull
    public JComponent getEditorComponent() {
        ensureEditorExists();
//        classSelectorPanel.setSelection(owlEditorKit.getWorkspace().getOWLSelectionModel().getLastSelectedClass());
        return editorComponent;
    }


    private void ensureEditorExists() {
        if (editorComponent == null) {
            createEditor();
        }
    }


    @Nullable
    public Set<OWLClassExpression> getEditedObject() {
        ensureEditorExists();
        if (tabbedPane != null && tabbedPane.getSelectedComponent().equals(classSelectorPanel)) {
            return new HashSet<>(classSelectorPanel.getSelectedObjects());
        }
        else {
            try {
                return expressionEditor.createObject();
            }
            catch (OWLException e) {
                logger.error("An error occurred when trying to create the OWL object corresponding to the " +
                        "entered expression.", e);
            }
        }
        return null;
    }


    public boolean setEditedObject(Set<OWLClassExpression> expressions) {
        if (expressions == null){
            expressions = Collections.emptySet();
        }

        ensureEditorExists();
        expressionEditor.setExpressionObject(expressions);
        if (containsOnlyNamedClasses(expressions)){
            Set<OWLClass> clses = new HashSet<>(expressions.size());
            for (OWLClassExpression expr : expressions){
                clses.add(expr.asOWLClass());
            }
            classSelectorPanel.setSelection(clses);
        }
        // @@TODO should remove the class selector if any of the expressions are anonymous

        return true;
    }


    private boolean containsOnlyNamedClasses(Set<OWLClassExpression> expressions) {
        if (expressions != null){
            for (OWLClassExpression expr : expressions){
                if (expr.isAnonymous()){
                    return false;
                }
            }
        }
        return true;
    }


    public void dispose() {
        if (classSelectorPanel != null){
            classSelectorPanel.dispose();
        }
    }


    private boolean isValid(){
        if (tabbedPane != null && tabbedPane.getSelectedComponent().equals(classSelectorPanel)) {
            return classSelectorPanel.getSelectedObject() != null;
        }
        else{
            return expressionEditor.isWellFormed();
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        expressionEditor.addStatusChangedListener(listener);
        listener.verifiedStatusChanged(isValid());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
        expressionEditor.removeStatusChangedListener(listener);
    }
}
