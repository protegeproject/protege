package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralAxiomEditor extends AbstractOWLObjectEditor<OWLClassAxiom> implements VerifiedInputEditor {

    private OWLEditorKit editorKit;

    private ExpressionEditor<OWLClassAxiom> editor;

    private JComponent editingComponent;


    public OWLGeneralAxiomEditor(OWLEditorKit editorKit) {
        this.editorKit = editorKit;

        editor = new ExpressionEditor<OWLClassAxiom>(editorKit, editorKit.getModelManager().getOWLExpressionCheckerFactory().getClassAxiomChecker());

        editingComponent = new JPanel(new BorderLayout());
        editingComponent.add(editor);
        editingComponent.setPreferredSize(new Dimension(400, 200));
    }


    public boolean setEditedObject(OWLClassAxiom axiom) {
        if (axiom == null){
            editor.setText("");
        }
        else{
            editor.setText(editorKit.getModelManager().getRendering(axiom));
        }
        return true;
    }


    public JComponent getInlineEditorComponent() {
        // Same as general editor component
        return editingComponent;
    }


    public String getEditorTypeName() {
        return "Class Axiom";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLClassAxiom;
    }


    /**
     * Gets a component that will be used to edit the specified
     * object.
     * @return The component that will be used to edit the object
     */
    public JComponent getEditorComponent() {
        return editingComponent;
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLClassAxiom getEditedObject() {
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
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.addStatusChangedListener(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        editor.removeStatusChangedListener(listener);
    }
}
