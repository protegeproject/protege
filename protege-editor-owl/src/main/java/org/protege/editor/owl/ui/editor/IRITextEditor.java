package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.FormLabel;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class IRITextEditor implements OWLObjectEditor<IRI>, VerifiedInputEditor {

    private OWLObjectEditorHandler<IRI> handler;

    private JPanel editor;

    private JTextField iriTextField;

    private List<InputVerificationStatusChangedListener> inputVerificationListeners = new ArrayList<>();

    public IRITextEditor(OWLEditorKit editorKit) {
        createGui();
    }

    private void createGui() {
        editor = new JPanel();
        editor.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        editor.setLayout(new BorderLayout());

        JPanel holder = new JPanel(new BorderLayout());
        editor.add(holder, BorderLayout.NORTH);

        FormLabel iriLabel = new FormLabel("IRI");
        iriLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));
        holder.add(iriLabel, BorderLayout.NORTH);

        iriTextField = new JTextField();
        iriTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                for(InputVerificationStatusChangedListener listener : inputVerificationListeners) {
                    listener.verifiedStatusChanged(getEditedObject() != null);
                }
            }
        });

        holder.add(iriTextField, BorderLayout.SOUTH);

        editor.addHierarchyListener(e -> {
            if(editor.isShowing()) {
                iriTextField.requestFocus();
            }
        });
    }

    @Nullable
    public IRI getEditedObject() {
        IRI editedObject = null;
        try {
            editedObject = IRI.create(iriTextField.getText());
        } catch(RuntimeException e) {
        }
        return editedObject;
    }

    @Nonnull
    public String getEditorTypeName() {
        return "IRI Editor";
    }

    public boolean canEdit(Object object) {
        return object instanceof IRI;
    }

    public boolean isMultiEditSupported() {
        return false;
    }

    public boolean isPreferred() {
        return false;
    }

    @Nonnull
    public JComponent getEditorComponent() {
        return editor;
    }

    public boolean setEditedObject(IRI editedObject) {
        if(editedObject != null) {
            iriTextField.setText(editedObject.toString());
        }
        return editedObject != null;
    }

    public Set<IRI> getEditedObjects() {
        IRI editedObject = getEditedObject();
        if(editedObject != null) {
            return Collections.singleton(editedObject);
        }
        else {
            return Collections.emptySet();
        }
    }

    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        inputVerificationListeners.add(listener);
    }

    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        inputVerificationListeners.remove(listener);
    }

    public OWLObjectEditorHandler<IRI> getHandler() {
        return handler;
    }

    public void setHandler(OWLObjectEditorHandler<IRI> handler) {
        this.handler = handler;
    }

    public void clear() {
        iriTextField.setText("");
    }

    public void dispose() {
    }
}
