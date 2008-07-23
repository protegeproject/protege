package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Feb-2007<br><br>
 */
public class OWLClassDescriptionSetEditor extends AbstractOWLFrameSectionRowObjectEditor<Set<OWLDescription>> {

    private static final Logger logger = Logger.getLogger(OWLClassDescriptionSetEditor.class);


    private OWLEditorKit owlEditorKit;

    private OWLClassSelectorPanel classSelectorPanel;

    private JComponent editorComponent;

    private ExpressionEditor<OWLDescription> expressionEditor;

    private JTabbedPane tabbedPane;

    private Set<OWLClass> initialSelection;


    public OWLClassDescriptionSetEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }

    public OWLClassDescriptionSetEditor(OWLEditorKit owlEditorKit, Set<OWLClass> selectedClasses) {
        this.owlEditorKit = owlEditorKit;
        this.initialSelection = selectedClasses;
    }

    private void createEditor() {
        editorComponent = new JPanel(new BorderLayout());
        classSelectorPanel = new OWLClassSelectorPanel(owlEditorKit);
        if (initialSelection != null){
            editorComponent.add(classSelectorPanel);
            classSelectorPanel.setSelection(initialSelection);
        }
        else{
            tabbedPane = new JTabbedPane();
            tabbedPane.add("Class hierarchy", classSelectorPanel);
            expressionEditor = new ExpressionEditor<OWLDescription>(owlEditorKit, owlEditorKit.getModelManager().getOWLExpressionCheckerFactory().getOWLDescriptionChecker());
            JPanel holderPanel = new JPanel(new BorderLayout());
            holderPanel.add(expressionEditor);
            holderPanel.setPreferredSize(new Dimension(500, 400));
            holderPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            tabbedPane.add("Expression editor", holderPanel);
            editorComponent.add(tabbedPane);
        }
    }


    public JComponent getEditorComponent() {
        if (editorComponent == null) {
            createEditor();
        }
//        classSelectorPanel.setSelection(owlEditorKit.getWorkspace().getOWLSelectionModel().getLastSelectedClass());
        return editorComponent;
    }


    public void clear() {
        if (editorComponent != null) {
            expressionEditor.setText("");
        }
    }


    public Set<OWLDescription> getEditedObject() {
        if (tabbedPane == null || tabbedPane.getSelectedComponent().equals(classSelectorPanel)) {
            return new HashSet<OWLDescription>(classSelectorPanel.getSelectedObjects());
        }
        else {
            try {
                if (!expressionEditor.isWellFormed()) {
                    return null;
                }
                Set<OWLDescription> clses = new HashSet<OWLDescription>();
                clses.add(expressionEditor.createObject());
                return clses;
            }
            catch (OWLException e) {
                logger.error(e);
            }
        }
        return null;
    }


    public void dispose() {
        classSelectorPanel.dispose();
    }
}
