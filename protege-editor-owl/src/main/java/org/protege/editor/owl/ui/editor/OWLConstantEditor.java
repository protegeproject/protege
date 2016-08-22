/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.OWLLiteralParser;
import org.protege.editor.owl.model.util.LiteralChecker;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class OWLConstantEditor extends JPanel implements OWLObjectEditor<OWLLiteral> {

    private static final long serialVersionUID = 3199534896795886986L;

    private JTextArea annotationContent;

    private JComboBox langComboBox;

    private JLabel langLabel = new JLabel("Lang");

    private JComboBox datatypeComboBox;

    private OWLDataFactory dataFactory;

    private String lastLanguage;

    private OWLDatatype lastDatatype;

    private final OWLEditorKit editorKit;

    private final JLabel messageLabel = new JLabel();


    public OWLConstantEditor(OWLEditorKit owlEditorKit) {
        this.editorKit = owlEditorKit;
        dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
        annotationContent = new JTextArea(8, 40);
        annotationContent.setWrapStyleWord(true);
        annotationContent.setLineWrap(true);

        final UIHelper uiHelper = new UIHelper(owlEditorKit);
        langComboBox = uiHelper.getLanguageSelector();

        datatypeComboBox = uiHelper.getDatatypeSelector();
        datatypeComboBox.addActionListener(e -> {
            OWLDatatype owlDatatype = getSelectedDatatype();
            boolean langEnabled = owlDatatype == null || owlDatatype.isRDFPlainLiteral();
            validateContent();
            setLangEnabled(langEnabled);
        });

        annotationContent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateContent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateContent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        removeNonSelectableDatatypes(datatypeComboBox);

        setupAutoCompleter(owlEditorKit);
        layoutComponents();
    }

    private void validateContent() {
        clearErrorMessage();
        if(getLexicalValue().isEmpty()) {
            return;
        }
        Optional<OWLDatatype> datatype = Optional.ofNullable(getSelectedDatatype());
        datatype.ifPresent(d -> {
            if(!LiteralChecker.isLiteralIsInLexicalSpace(getEditedObject())) {
                    annotationContent.setForeground(Color.RED);
                String message = String.format(
                        "The entered value is not valid for the specified datatype (%s)",
                        editorKit.getOWLModelManager().getRendering(d));
                displayErrorMessage(message);
            }
        });
    }

    private void displayErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(Color.RED);
        annotationContent.setToolTipText(message);
    }

    private void clearErrorMessage() {
        messageLabel.setText("Value");
        messageLabel.setForeground(null);
        annotationContent.setToolTipText(null);
        annotationContent.setForeground(null);
    }

    /**
     * Removes rdf:PlainLiteral.  Also removes datatypes that should not be the concrete types of constants e.g.
     * owl:real.
     * @param datatypeComboBox The combobox from which the datatypes should be removed.  Not {@code null}.
     */
    private static void removeNonSelectableDatatypes(JComboBox datatypeComboBox) {
        for (int i = 0; i < datatypeComboBox.getItemCount(); i++) {
            OWLDatatype datatype = (OWLDatatype) datatypeComboBox.getItemAt(i);
            if (datatype != null) {
                if (datatype.isRDFPlainLiteral()) {
                    datatypeComboBox.removeItemAt(i);
                }
                else if(datatype.isBuiltIn() && datatype.getBuiltInDatatype().equals(OWL2Datatype.OWL_REAL)) {
                    datatypeComboBox.removeItemAt(i);
                }
            }
        }
    }

    private void setLangEnabled(boolean b) {
        langLabel.setEnabled(b);
        langComboBox.setEnabled(b);
    }

    public boolean canEdit(Object object) {
        return object instanceof OWLLiteral;
    }

    public boolean isPreferred(Object object) {
        return object instanceof OWLLiteral;
    }

    public JComponent getEditorComponent() {
        return this;
    }

    public OWLLiteral getEditedObject() {
        lastDatatype = null;
        lastLanguage = null;
        String value = getLexicalValue();
        if (isLangSelected()) {
            lastLanguage = getSelectedLang();
            return dataFactory.getOWLLiteral(value, getSelectedLang());
        }
        if (isDatatypeSelected()) {
            lastDatatype = getSelectedDatatype();
            return dataFactory.getOWLLiteral(value, getSelectedDatatype());
        }

        OWLLiteralParser parser = new OWLLiteralParser(dataFactory);

        return parser.parseLiteral(value);
    }

    private String getLexicalValue() {
        return annotationContent.getText().trim();
    }

    public Set<OWLLiteral> getEditedObjects() {
        return Collections.singleton(getEditedObject());
    }

    public boolean setEditedObject(OWLLiteral constant) {
        clear();
        if (constant != null) {
            annotationContent.setText(constant.getLiteral());
            if (!constant.isRDFPlainLiteral()) {
                datatypeComboBox.setSelectedItem(constant.getDatatype());
            } else {
                langComboBox.setSelectedItem(constant.getLang());
            }
        }
        return true;
    }

    public boolean isMultiEditSupported() {
        return false;
    }

    public String getEditorTypeName() {
        return "Literal";
    }

    public void clear() {
        annotationContent.setText("");
        datatypeComboBox.setSelectedItem(lastDatatype);
        langComboBox.setSelectedItem(lastLanguage);
    }

    private boolean isLangSelected() {
        return langComboBox.getSelectedItem() != null && !langComboBox.getSelectedItem().equals("");
    }

    private boolean isDatatypeSelected() {
        return datatypeComboBox.getSelectedItem() != null;
    }

    private String getSelectedLang() {
        return (String) langComboBox.getSelectedItem();
    }

    /**
     * Gets the selected datatype
     *
     * @return The selected datatype, or <code>null</code>
     * if no datatype is selected.
     */
    private OWLDatatype getSelectedDatatype() {
        return (OWLDatatype) datatypeComboBox.getSelectedItem();
    }

    private void setupAutoCompleter(OWLEditorKit owlEditorKit) {
        new OWLAutoCompleter(owlEditorKit, annotationContent, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException {
                throw new OWLExpressionParserException(text, 0, text.length(), true, true, true, true, true, true, new HashSet<>());
            }

            public Object createObject(String text)
                    throws OWLExpressionParserException {
                return null;
            }
        });
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());

        add(new JScrollPane(annotationContent),
                new GridBagConstraints(1,
                        1,
                        5,
                        1,
                        100.0,
                        100.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.BOTH,
                        new Insets(7, 7, 7, 7),
                        0,
                        0));

        add(messageLabel,
                new GridBagConstraints(1,
                        0,
                        5,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new Insets(7, 7, 0, 7),
                        0,
                        0));

        add(new JLabel("Type"),
                new GridBagConstraints(1,
                        3,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets(0, 7, 0, 7),
                        0,
                        0));


        add(datatypeComboBox,
                new GridBagConstraints(2,
                        3,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5),
                        40,
                        0));

        add(langLabel,
                new GridBagConstraints(3,
                        3,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets(0, 20, 0, 0),
                        0,
                        0));
        langLabel.setEnabled(true);

        add(langComboBox,
                new GridBagConstraints(4,
                        3,
                        1,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5),
                        40,
                        0));
    }

    public void dispose() {
    }

    public void setHandler(OWLObjectEditorHandler<OWLLiteral> owlLiteralOWLObjectEditorHandler) {
    }

    public OWLObjectEditorHandler<OWLLiteral> getHandler() {
        return null;
    }
}
