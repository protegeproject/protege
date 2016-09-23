package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Collections;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22/09/15
 */
public class OWLObjectPropertyIndividualPairEditor2 extends AbstractOWLObjectEditor<OWLObjectPropertyIndividualPair> implements VerifiedInputEditor {

    private JTextField objectPropertyField;

    private JTextField individualField;

    private JPanel panelHolder;

    private JPanel panel;

    private OWLModelManager modelManager;

    private JLabel errorLabel = new JLabel(" ");

    private InputVerificationStatusChangedListener verificationStatusChangedListener = newState -> {};

    public OWLObjectPropertyIndividualPairEditor2(OWLEditorKit editorKit) {
        this.modelManager = editorKit.getOWLModelManager();
        panelHolder = new JPanel(new BorderLayout());
        panel = new JPanel(new GridBagLayout());
        panelHolder.add(panel, BorderLayout.NORTH);
        objectPropertyField = new AugmentedJTextField("", 20, "Enter object property name");
        objectPropertyField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        individualField = new AugmentedJTextField("", 20, "Enter individual name");
        individualField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        Insets insets = new Insets(2, 2, 2, 2);
        panel.add(objectPropertyField,
                new GridBagConstraints(
                        0, 0,
                        1, 1,
                        100, 0,
                        GridBagConstraints.BASELINE,
                        GridBagConstraints.HORIZONTAL,
                        insets,
                        0, 0));
        panel.add(individualField,
                new GridBagConstraints(
                        1, 0,
                        1, 1,
                        100, 0,
                        GridBagConstraints.BASELINE,
                        GridBagConstraints.HORIZONTAL,
                        insets,
                        0, 0));

        errorLabel.setForeground(Color.RED);
        panel.add(errorLabel,
                new GridBagConstraints(
                        0, 1,
                        2, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_TRAILING,
                        GridBagConstraints.HORIZONTAL,
                        insets,
                        0, 0
                ));

        JLabel tipLabel = new JLabel("(Tip: Use CTRL+Space to auto-complete names)");
        tipLabel.setForeground(Color.GRAY);
        tipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(tipLabel,
                new GridBagConstraints(
                        0, 2,
                        2, 1,
                        100, 0,
                        GridBagConstraints.BASELINE_TRAILING,
                        GridBagConstraints.HORIZONTAL,
                        insets,
                        0, 0
                ));
        new OWLAutoCompleter(editorKit, objectPropertyField, new OWLExpressionChecker() {
            @Override
            public void check(String text) throws OWLExpressionParserException {
                if(modelManager.getOWLEntityFinder().getOWLObjectProperty(text) == null) {
                    throw new OWLExpressionParserException(text, 0, text.length(), false, true, false, false, false, false, Collections.<String>emptySet());
                }
            }

            @Override
            public Object createObject(String text) throws OWLExpressionParserException {
                return modelManager.getOWLEntityFinder().getOWLObjectProperty(text);
            }
        });
        new OWLAutoCompleter(editorKit, individualField, new OWLExpressionChecker() {
            @Override
            public void check(String text) throws OWLExpressionParserException {
                if(modelManager.getOWLEntityFinder().getOWLIndividual(text) == null) {
                    throw new OWLExpressionParserException(text, 0, text.length(), false, false, false, true, false, false, Collections.<String>emptySet());
                }
            }

            @Override
            public Object createObject(String text) throws OWLExpressionParserException {
                return modelManager.getOWLEntityFinder().getOWLIndividual(text);
            }
        });
        validateInput();
    }

    private void validateInput() {
        objectPropertyField.setToolTipText(null);
        individualField.setToolTipText(null);
        errorLabel.setText(" ");

        String objectPropertyName = objectPropertyField.getText().trim();
        if(!objectPropertyName.isEmpty() && modelManager.getOWLEntityFinder().getOWLObjectProperty(objectPropertyName) == null) {
            objectPropertyField.setToolTipText("Invalid object property");
            errorLabel.setText("Invalid property name");
            verificationStatusChangedListener.verifiedStatusChanged(false);
            return;
        }

        String individualName = individualField.getText().trim();
        if(!individualName.isEmpty() && modelManager.getOWLEntityFinder().getOWLIndividual(individualName) == null) {
            individualField.setToolTipText("Invalid individual name");
            errorLabel.setText("Invalid individual name");
            verificationStatusChangedListener.verifiedStatusChanged(false);
            return;
        }
        if (objectPropertyName.isEmpty() || individualName.isEmpty()) {
            verificationStatusChangedListener.verifiedStatusChanged(false);
            return;
        }
        verificationStatusChangedListener.verifiedStatusChanged(true);


    }

    @Nonnull
    @Override
    public String getEditorTypeName() {
        return "Object property assertion";
    }

    @Override
    public boolean canEdit(Object object) {
        return object instanceof OWLObjectPropertyIndividualPair;
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return panelHolder;
    }

    @Nullable
    @Override
    public OWLObjectPropertyIndividualPair getEditedObject() {
        OWLObjectProperty property = modelManager.getOWLEntityFinder().getOWLObjectProperty(objectPropertyField.getText());
        if(property == null) {
            return null;
        }
        OWLNamedIndividual individual = modelManager.getOWLEntityFinder().getOWLIndividual(individualField.getText());
        if(individual == null) {
            return null;
        }
        return new OWLObjectPropertyIndividualPair(property, individual);
    }

    @Override
    public boolean setEditedObject(OWLObjectPropertyIndividualPair editedObject) {
        if(editedObject == null) {
            objectPropertyField.setText("");
            individualField.setText("");
        }
        else {
            objectPropertyField.setText(modelManager.getRendering(editedObject.getProperty()));
            individualField.setText(modelManager.getRendering(editedObject.getIndividual()));
        }
        return true;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        if (listener != null) {
            this.verificationStatusChangedListener = listener;
        }
        else {
            verificationStatusChangedListener = (newState) -> {};
        }
        validateInput();
    }

    @Override
    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        this.verificationStatusChangedListener = (newState) -> {};
    }
}
