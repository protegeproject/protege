/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.FormLabel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.lang.LangCode;
import org.protege.editor.owl.model.lang.LangCodeRegistry;
import org.protege.editor.owl.model.parser.OWLLiteralParser;
import org.protege.editor.owl.model.util.LiteralChecker;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.lang.LangTagEditor;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.annotation.Nonnull;
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

    private final OWLEditorKit editorKit;

    private final JTextArea lexicalValueField = new JTextArea(8, 40);

    private final LangTagEditor langTagField;

    private final JComboBox<OWLDatatype> datatypeField;

    private final OWLDataFactory dataFactory;

    private LangCode lastLanguage;

    private OWLDatatype lastDatatype;

    private LangCodeRegistry langCodeRegistry = LangCodeRegistry.get();


    private final JLabel messageLabel = new JLabel();


    public OWLConstantEditor(OWLEditorKit owlEditorKit) {
        this.editorKit = owlEditorKit;
        dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
        lexicalValueField.setWrapStyleWord(true);
        lexicalValueField.setLineWrap(true);
        lexicalValueField.setBorder(null);

        final UIHelper uiHelper = new UIHelper(owlEditorKit);
        langTagField = new LangTagEditor(LangCodeRegistry.get());

        datatypeField = uiHelper.getDatatypeSelector();
        datatypeField.addActionListener(e -> validateContent());

        lexicalValueField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleLexicalValueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleLexicalValueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        addHierarchyListener(e -> {
            if(isShowing()) {
                lexicalValueField.requestFocus();
            }
        });

        removeNonSelectableDatatypes(datatypeField);

        setupAutoCompleter(owlEditorKit);
        layoutComponents();
    }

    private void handleLexicalValueChanged() {
        if(isLangSelected()) {
            datatypeField.setSelectedItem(null);
        }
        else {
            OWLLiteralParser parser = new OWLLiteralParser(dataFactory);
            OWLLiteral parsedLiteral = parser.parseLiteral(lexicalValueField.getText().trim());
            datatypeField.setSelectedItem(parsedLiteral.getDatatype());
        }
        validateContent();
    }

    private void validateContent() {
        clearErrorMessage();
        if(getLexicalValue().isEmpty()) {
            return;
        }
        Optional<OWLDatatype> datatype = Optional.ofNullable(getSelectedDatatype());
        datatype.ifPresent(d -> {
            if(!LiteralChecker.isLiteralIsInLexicalSpace(getEditedObject())) {
                    lexicalValueField.setForeground(Color.RED);
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
        lexicalValueField.setToolTipText(message);
    }

    private void clearErrorMessage() {
        messageLabel.setText("");
        messageLabel.setForeground(null);
        lexicalValueField.setToolTipText(null);
        lexicalValueField.setForeground(null);
    }

    /**
     * Removes rdf:PlainLiteral.  Also removes datatypes that should not be the concrete types of constants e.g.
     * owl:real.
     * @param datatypeComboBox The combobox from which the datatypes should be removed.  Not {@code null}.
     */
    private static void removeNonSelectableDatatypes(JComboBox<OWLDatatype> datatypeComboBox) {
        for (int i = 0; i < datatypeComboBox.getItemCount(); i++) {
            OWLDatatype datatype = datatypeComboBox.getItemAt(i);
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

    public boolean canEdit(Object object) {
        return object instanceof OWLLiteral;
    }

    @Nonnull
    public JComponent getEditorComponent() {
        return this;
    }

    @Nonnull
    public OWLLiteral getEditedObject() {
        lastDatatype = null;
        lastLanguage = null;
        String value = getLexicalValue();
        // Specifying a language tag overrides the datatype
        if (isLangSelected()) {
            lastLanguage = getSelectedLang().orElse(null);
            return dataFactory.getOWLLiteral(value, getSelectedLang().map(LangCode::getLangCode).orElse(null));
        }
        if (isDatatypeSelected()) {
            lastDatatype = getSelectedDatatype();
            if(lastDatatype.isBoolean()) {
                if(OWL2Datatype.XSD_BOOLEAN.getPattern().matcher(value).matches()) {
                    dataFactory.getOWLLiteral(value, OWL2Datatype.XSD_BOOLEAN);
                }
                else {
                    dataFactory.getOWLLiteral(value, OWL2Datatype.XSD_STRING);
                }
            }
            else {
                return dataFactory.getOWLLiteral(value, getSelectedDatatype());
            }

        }

        OWLLiteralParser parser = new OWLLiteralParser(dataFactory);

        return parser.parseLiteral(value);
    }

    private String getLexicalValue() {
        return lexicalValueField.getText().trim();
    }

    public Set<OWLLiteral> getEditedObjects() {
        return Collections.singleton(getEditedObject());
    }

    public boolean setEditedObject(OWLLiteral literal) {
        clear();
        if(literal == null) {
            return true;
        }
        lexicalValueField.setText(literal.getLiteral());
        if(literal.isRDFPlainLiteral()) {
            langCodeRegistry.getLangCode(literal.getLang()).ifPresent(langTagField::setLangCode);
        }
        else {
            datatypeField.setSelectedItem(literal.getDatatype());
        }
        return true;
    }

    public boolean isMultiEditSupported() {
        return false;
    }

    @Nonnull
    public String getEditorTypeName() {
        return "Literal";
    }

    public void clear() {
        lexicalValueField.setText("");
        datatypeField.setSelectedItem(lastDatatype);
        langTagField.clear();
    }

    private boolean isLangSelected() {
        return langTagField.getLangCode().isPresent();
    }

    private boolean isDatatypeSelected() {
        return datatypeField.getSelectedItem() != null;
    }

    private Optional<LangCode> getSelectedLang() {
        return langTagField.getLangCode();
    }

    /**
     * Gets the selected datatype
     *
     * @return The selected datatype, or <code>null</code>
     * if no datatype is selected.
     */
    private OWLDatatype getSelectedDatatype() {
        return (OWLDatatype) datatypeField.getSelectedItem();
    }

    private void setupAutoCompleter(OWLEditorKit owlEditorKit) {
        new OWLAutoCompleter(owlEditorKit, lexicalValueField, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException {
                throw new OWLExpressionParserException(text, 0, text.length(), true, true, true, true, true, true, new HashSet<>());
            }

            public Object createObject(String text) {
                return null;
            }
        });
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        Insets formLabelInsets = new Insets(7, 0, 0, 0);

        add(new FormLabel("Value"),
            new GridBagConstraints(1, 0,
                                   1,
                                   1,
                                   0.0,
                                   0.0,
                                   GridBagConstraints.BASELINE_LEADING,
                                   GridBagConstraints.NONE,
                                   formLabelInsets,
                                   0, 0
            ));

        add(new JScrollPane(lexicalValueField),
                new GridBagConstraints(1, 1,
                        1,
                        1,
                        100.0,
                        100.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));

        add(messageLabel,
            new GridBagConstraints(1, 2,
                                   1,
                                   1,
                                   0.0,
                                   0.0,
                                   GridBagConstraints.NORTHWEST,
                                   GridBagConstraints.NONE,
                                   new Insets(0, 0, 0, 0),
                                   0,
                                   0));

        add(new FormLabel("Language Tag"),
            new GridBagConstraints(1, 3,
                                   1,
                                   1,
                                   0.0,
                                   0.0,
                                   GridBagConstraints.BASELINE_LEADING,
                                   GridBagConstraints.NONE,
                                   formLabelInsets,
                                   0,
                                   0));

        add(langTagField,
            new GridBagConstraints(1, 4,
                                   1,
                                   1,
                                   100.0,
                                   0.0,
                                   GridBagConstraints.BASELINE_LEADING,
                                   GridBagConstraints.HORIZONTAL,
                                   new Insets(0, 0, 0, 0),
                                   40,
                                   0));

        add(new FormLabel("Datatype"),
                new GridBagConstraints(1, 5,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.BASELINE_LEADING,
                        GridBagConstraints.NONE,
                        formLabelInsets,
                        0,
                        0));


        add(datatypeField,
            new GridBagConstraints(1, 6,
                        1,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.BASELINE,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
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
