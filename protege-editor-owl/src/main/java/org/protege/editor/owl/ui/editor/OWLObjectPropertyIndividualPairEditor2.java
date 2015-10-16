package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22/09/15
 */
public class OWLObjectPropertyIndividualPairEditor2 extends AbstractOWLObjectEditor<OWLObjectPropertyIndividualPair> {

    private JTextField objectPropertyField;

    private JTextField individualField;

    private JPanel panel;

    private OWLModelManager modelManager;

    public OWLObjectPropertyIndividualPairEditor2(OWLEditorKit editorKit) {
        this.modelManager = editorKit.getOWLModelManager();
        panel = new JPanel(new GridBagLayout());
        objectPropertyField = new AugmentedJTextField("", 20, "Enter object property name");
        individualField = new AugmentedJTextField("", 20, "Enter individual name");
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
        JLabel tipLabel = new JLabel("(Tip: Use CTRL+Space to auto-complete names)");
        tipLabel.setForeground(Color.GRAY);
        tipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(tipLabel,
                new GridBagConstraints(
                        0, 1,
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
    }

    @Override
    public String getEditorTypeName() {
        return "Object property assertion";
    }

    @Override
    public boolean canEdit(Object object) {
        return object instanceof OWLObjectPropertyIndividualPair;
    }

    @Override
    public JComponent getEditorComponent() {
        return panel;
    }

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
}
