package org.protege.editor.owl.ui.frame.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDatatype;
import org.semanticweb.owl.model.OWLLiteral;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class OWLConstantEditor extends JPanel implements OWLAnnotationValueEditor<OWLLiteral> {

    private JTextArea annotationContent;

    private JComboBox langComboBox;

    private JComboBox datatypeComboBox;

    private OWLDataFactory dataFactory;

    private String lastLanguage;

    private OWLDatatype lastDatatype;

    public OWLConstantEditor(OWLEditorKit owlEditorKit) {
        dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
        annotationContent = new JTextArea(8, 40);
        annotationContent.setWrapStyleWord(true);
        annotationContent.setLineWrap(true);

        final UIHelper uiHelper = new UIHelper(owlEditorKit);
        langComboBox = uiHelper.getLanguageSelector();
        datatypeComboBox = uiHelper.getDatatypeSelector();

        setupAutoCompleter(owlEditorKit);
        layoutComponents();
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLLiteral;
    }


    public boolean isPreferred(Object object) {
        return object instanceof OWLLiteral;
    }


    public OWLLiteral getEditedObject() {
        return getValue();
    }


    public void setEditedObject(OWLLiteral object) {
        setValue(object);
    }


    public JComponent getComponent() {
        return this;
    }


    public String getEditorTypeName() {
        return "Constant";
    }


    public void clear() {
        annotationContent.setText("");
        datatypeComboBox.setSelectedItem(lastDatatype);
        langComboBox.setSelectedItem(lastLanguage);
    }


    public void setValue(OWLLiteral constant) {
        clear();
        if (constant != null) {
            annotationContent.setText(constant.getLiteral());
            if (constant.isTyped()) {
                datatypeComboBox.setSelectedItem(constant.asOWLTypedLiteral().getDatatype());
            }
            else {
                langComboBox.setSelectedItem(constant.asRDFTextLiteral().getLang());
            }
        }
    }


    public OWLLiteral getValue() {
        lastDatatype = null;
        lastLanguage = null;
        String value = annotationContent.getText();
        OWLLiteral constant;
        if (isDatatypeSelected()) {
            constant = dataFactory.getOWLTypedLiteral(value, getSelectedDatatype());
            lastDatatype = getSelectedDatatype();
        }
        else {
            if (isLangSelected()) {
                constant = dataFactory.getOWLStringLiteral(value, getSelectedLang());
                lastLanguage = getSelectedLang();
            }
            else {
                constant = dataFactory.getOWLStringLiteral(value, null);
            }
        }
        return constant;
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
     * @return The selected datatype, or <code>null</code>
     *         if no datatype is selected.
     */
    private OWLDatatype getSelectedDatatype() {
        return (OWLDatatype) datatypeComboBox.getSelectedItem();
    }


    private void setupAutoCompleter(OWLEditorKit owlEditorKit) {
        new OWLDescriptionAutoCompleter(owlEditorKit, annotationContent, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException {
                throw new OWLExpressionParserException(text,
                                                       0,
                                                       text.length(),
                                                       true,
                                                       true,
                                                       true,
                                                       true,
                                                       true,
                                                       new HashSet<String>());
            }


            public Object createObject(String text) throws OWLExpressionParserException {
                return null;
            }
        });
    }


    private void layoutComponents() {

//        literalEditingPanel = new JPanel(new GridBagLayout()) {
//            public boolean requestFocusInWindow() {
//                return annotationContent.requestFocusInWindow();
//            }
//        };
//        literalEditingPanel.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));

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

        add(new JLabel("Value"),
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

        add(new JLabel("Lang"),
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
}
