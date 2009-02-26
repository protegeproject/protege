package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLDescription;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
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

    private java.util.List<OWLDescriptionEditor> activeEditors = new ArrayList<OWLDescriptionEditor>();

    private Set<OWLDescriptionEditor> editors = new HashSet<OWLDescriptionEditor>();

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

        editingComponent.add(tabbedPane);

        editingComponent.setPreferredSize(new Dimension(600, 400));

        tabbedPane.addChangeListener(changeListener);
    }


    public void addPanel(OWLDescriptionEditor editorPanel){
        editors.add(editorPanel);

        if (editorPanel.setDescription(description)){
            activeEditors.add(editorPanel);
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
        return activeEditors.get(tabbedPane.getSelectedIndex());
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
        for (OWLDescriptionEditor editor : activeEditors){
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
        for (OWLDescriptionEditor editor : activeEditors){
            editor.addStatusChangedListener(l);
        }
        l.verifiedStatusChanged(isValidated());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.remove(l);
        for (OWLDescriptionEditor editor : activeEditors){
            editor.removeStatusChangedListener(l);
        }
    }
}
