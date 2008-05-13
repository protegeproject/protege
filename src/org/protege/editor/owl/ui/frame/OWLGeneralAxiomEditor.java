package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLClassAxiomChecker;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralAxiomEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLClassAxiom> implements VerifiedInputEditor {

    private OWLEditorKit editorKit;

    private OWLClassAxiomChecker checker;

    private ExpressionEditor<OWLClassAxiom> editor;

    private JComponent editingComponent;

    private List<InputVerificationStatusChangedListener> listeners = new ArrayList<InputVerificationStatusChangedListener>();

    private DocumentListener docListener = new DocumentListener(){

        public void insertUpdate(DocumentEvent event) {
            handleEditorChange();
        }

        public void removeUpdate(DocumentEvent event) {
            handleEditorChange();
        }

        public void changedUpdate(DocumentEvent event) {
            handleEditorChange();
        }
    };

    public OWLGeneralAxiomEditor(OWLEditorKit editorKit) {
        this.editorKit = editorKit;

        checker = new OWLClassAxiomChecker(editorKit);
        editor = new ExpressionEditor<OWLClassAxiom>(editorKit, checker);
        editor.getDocument().addDocumentListener(docListener);

        editingComponent = new JPanel(new BorderLayout());
        editingComponent.add(editor);
        editingComponent.setPreferredSize(new Dimension(400, 200));
    }

    public void setEditedObject(OWLClassAxiom axiom) {
        editor.setText(editorKit.getOWLModelManager().getRendering(axiom));
    }


    public JComponent getInlineEditorComponent() {
        // Same as general editor component
        return editingComponent;
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


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLClassAxiom getEditedObject() {
        try {
            if (editor.isWellFormed()) {
                String expression = editor.getText();
                return editor.getExpressionChecker().createObject(expression);
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
        editor.getDocument().removeDocumentListener(docListener);
    }

    private void handleEditorChange() {
        // @@TODO push this into the editor (so we use its timeout etc)
        for (InputVerificationStatusChangedListener l : listeners){
            l.verifiedStatusChanged(editor.isWellFormed());
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }
}
