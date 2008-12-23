package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.cache.OWLExpressionUserCache;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
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
 * Date: 15-Feb-2007<br><br>
 */
public class OWLClassDescriptionEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDescription>
        implements VerifiedInputEditor {

    private OWLEditorKit editorKit;

    private JComponent editingComponent;

    private JTabbedPane tabbedPane;

    private java.util.List<OWLDescriptionEditor> editors = new ArrayList<OWLDescriptionEditor>();

    private boolean currentStatus = false;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

    private ChangeListener changeListener = new ChangeListener(){
        public void stateChanged(ChangeEvent changeEvent) {
            handleVerifyEditorContents();
        }
    };

    private OWLDescription description;


    public OWLClassDescriptionEditor(OWLEditorKit editorKit, OWLDescription description) {

        this.editorKit = editorKit;

        this.description = description;

        editingComponent = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        addPanel(new OWLDescriptionExpressionEditor(editorKit, description));
        addPanel(new OWLClassSelectorPanel(editorKit));
        addPanel(new OWLObjectRestrictionCreatorPanel(editorKit));
        addPanel(new OWLDataRestrictionCreatorPanel(editorKit));

        editingComponent.add(tabbedPane);

        editingComponent.setPreferredSize(new Dimension(600, 400));

        tabbedPane.addChangeListener(changeListener);
    }


    public void addPanel(OWLDescriptionEditor editorPanel){
        if (editorPanel.setDescription(description)){
            editors.add(editorPanel);
            tabbedPane.add(editorPanel.getEditorName(), editorPanel.getComponent());
            for (InputVerificationStatusChangedListener l : listeners){
                editorPanel.addStatusChangedListener(l);
            }
        }
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
            OWLDescriptionEditor editor = getSelectedEditor();
            return editor.isValidInput();
    }


    private OWLDescriptionEditor getSelectedEditor() {
        return editors.get(tabbedPane.getSelectedIndex());
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
        for (OWLDescriptionEditor editor : editors){
            editor.setDescription(null);
        }
    }


    public Set<OWLDescription> getEditedObjects() {
        return getSelectedEditor().getDescriptions();
    }


    public OWLDescription getEditedObject() {
        Set<OWLDescription> sel = getSelectedEditor().getDescriptions();
        if (sel.isEmpty()){
            return null;
        }
        else{
            return sel.iterator().next();
        }
    }


    public void dispose() {
        for (OWLDescriptionEditor editor : editors){
            try {
                editor.dispose();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.add(l);
        for (OWLDescriptionEditor editor : editors){
            editor.addStatusChangedListener(l);
        }
        l.verifiedStatusChanged(isValidated());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.remove(l);
        for (OWLDescriptionEditor editor : editors){
            editor.removeStatusChangedListener(l);
        }
    }


    class OWLDescriptionExpressionEditor implements OWLDescriptionEditor{

        private static final String CLASS_EXPRESSION_EDITOR_LABEL = "Class expression editor";

        private ExpressionEditor<OWLDescription> editor;

        private JScrollPane scroller;


        OWLDescriptionExpressionEditor(OWLEditorKit eKit, OWLDescription description) {
            editor = new ExpressionEditor<OWLDescription>(eKit, eKit.getModelManager().getOWLExpressionCheckerFactory().getOWLDescriptionChecker());
            editor.setExpressionObject(description);

            scroller = new JScrollPane(editor);
        }


        public String getEditorName() {
            return CLASS_EXPRESSION_EDITOR_LABEL;
        }


        public JComponent getComponent() {
            return scroller;
        }


        public boolean isValidInput() {
            return editor.isWellFormed();
        }


        public boolean setDescription(OWLDescription description) {
            editor.setExpressionObject(description);
            return true;
        }


        public Set<OWLDescription> getDescriptions() {
            try {
                if (editor.isWellFormed()) {
                    OWLDescription owlDescription = editor.createObject();
                    OWLExpressionUserCache.getInstance(editorKit.getModelManager()).add(owlDescription, editor.getText());
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
}
