package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public class OWLClassDescriptionEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDescription>
        implements VerifiedInputEditor {

    private static final String CLASS_EXPRESSION_EDITOR_LABEL = "Class expression editor";
    private static final String CLASS_TREE_LABEL = "Class tree";
    private static final String OBJECT_RESTRICTION_CREATOR_LABEL = "Object restriction creator";
    private static final String DATA_RESTRICTION_CREATOR_LABEL = "Data restriction creator";

    private ExpressionEditor<OWLDescription> editor;

    private JComponent editingComponent;

    private JTabbedPane tabbedPane;

    private OWLClassSelectorPanel classSelectorPanel;

    private OWLObjectRestrictionCreatorPanel objectRestrictionCreatorPanel;
    private OWLDataRestrictionCreatorPanel dataRestrictionCreatorPanel;

    private boolean currentStatus = false;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

    private ChangeListener changeListener = new ChangeListener(){
        public void stateChanged(ChangeEvent changeEvent) {
            handleVerifyEditorContents();
        }
    };


    public OWLClassDescriptionEditor(OWLEditorKit editorKit, OWLDescription description) {

        editor = new ExpressionEditor<OWLDescription>(editorKit, editorKit.getModelManager().getOWLExpressionCheckerFactory().getOWLDescriptionChecker());
        editor.setExpressionObject(description);

        editingComponent = new JPanel(new BorderLayout());

        if (description == null || !description.isAnonymous()) {
            tabbedPane = new JTabbedPane();
            tabbedPane.setFocusable(false);
            tabbedPane.add(CLASS_EXPRESSION_EDITOR_LABEL, new JScrollPane(editor));

            classSelectorPanel = new OWLClassSelectorPanel(editorKit);
            tabbedPane.add(CLASS_TREE_LABEL, classSelectorPanel);
            if (description != null) {
                classSelectorPanel.setSelection(description.asOWLClass());
            }
            classSelectorPanel.addSelectionListener(changeListener);

            objectRestrictionCreatorPanel = new OWLObjectRestrictionCreatorPanel(editorKit);
            tabbedPane.add(OBJECT_RESTRICTION_CREATOR_LABEL, objectRestrictionCreatorPanel);

            dataRestrictionCreatorPanel = new OWLDataRestrictionCreatorPanel(editorKit);
            tabbedPane.add(DATA_RESTRICTION_CREATOR_LABEL, dataRestrictionCreatorPanel);

            tabbedPane.addChangeListener(changeListener);

            editingComponent.add(tabbedPane);
        }
        else{
            editingComponent.add(editor);
        }

        editingComponent.setPreferredSize(new Dimension(600, 400));
    }


    private void handleVerifyEditorContents() {
        boolean newStatus = isValidated();
        if (currentStatus != newStatus){
            currentStatus = newStatus;
            for (InputVerificationStatusChangedListener l : listeners){
                l.verifiedStatusChanged(newStatus);
            }
        }
    }


    private boolean isValidated() {
        if (tabbedPane != null){
            Component c = tabbedPane.getSelectedComponent();
            if (c.equals(classSelectorPanel)){
                return classSelectorPanel.getSelectedObject() != null;
            }
            else if (c.equals(objectRestrictionCreatorPanel)){
                return objectRestrictionCreatorPanel.isValidInput();
            }
            else if (c.equals(dataRestrictionCreatorPanel)){
                return dataRestrictionCreatorPanel.isValidInput();
            }
        }
        return editor.isWellFormed();
    }


    /**
     * Gets a component that will be used to edit the specified
     * object.
     * @return The component that will be used to edit the object
     */
    public JComponent getEditorComponent() {
        return editingComponent;
    }


    public void clear() {
        editor.setText("");
    }


    public Set<OWLDescription> getEditedObjects() {
        if (tabbedPane != null){
            final Component c = tabbedPane.getSelectedComponent();
            if (c.equals(classSelectorPanel)) {
                return new HashSet<OWLDescription>(classSelectorPanel.getSelectedObjects());
            }
            else if (c.equals(objectRestrictionCreatorPanel)) {
                return objectRestrictionCreatorPanel.createRestrictions();
            }
            else if (c.equals(dataRestrictionCreatorPanel)) {
                return dataRestrictionCreatorPanel.createRestrictions();
            }
        }
        return super.getEditedObjects();
    }


    public OWLDescription getEditedObject() {
        try {
            if (editor.isWellFormed()) {
                return editor.createObject();
            }
            else {
                return null;
            }
        }
        catch (OWLException e) {
            return null;
        }
    }


    public void dispose() {
        if (classSelectorPanel != null) {
            classSelectorPanel.dispose();
        }
        if (objectRestrictionCreatorPanel != null) {
            objectRestrictionCreatorPanel.dispose();
        }
        if (dataRestrictionCreatorPanel != null){
            dataRestrictionCreatorPanel.dispose();
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.add(l);
        editor.addStatusChangedListener(l);
        if (objectRestrictionCreatorPanel != null){
            objectRestrictionCreatorPanel.addStatusChangedListener(l);
        }
        if (dataRestrictionCreatorPanel != null){
            dataRestrictionCreatorPanel.addStatusChangedListener(l);
        }
        l.verifiedStatusChanged(isValidated());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.remove(l);
        editor.removeStatusChangedListener(l);
        if (objectRestrictionCreatorPanel != null){
            objectRestrictionCreatorPanel.removeStatusChangedListener(l);
        }
        if (dataRestrictionCreatorPanel != null){
            dataRestrictionCreatorPanel.removeStatusChangedListener(l);
        }
    }
}
