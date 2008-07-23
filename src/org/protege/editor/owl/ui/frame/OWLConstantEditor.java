package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.vocab.XSDVocabulary;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.*;
import java.util.List;
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
public class OWLConstantEditor extends JPanel implements OWLAnnotationValueEditor<OWLConstant> {

    private JTextArea annotationContent;

    private JComboBox langComboBox;

    private JComboBox datatypeComboBox;

    private OWLDescriptionAutoCompleter autoCompleter;

    private OWLDataFactory dataFactory;

    private String lastLanguage;

    private OWLDataType lastDatatype;

    public OWLConstantEditor(OWLEditorKit owlEditorKit) {
        dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
        annotationContent = new JTextArea(8, 40);
        annotationContent.setWrapStyleWord(true);
        annotationContent.setLineWrap(true);
        langComboBox = new JComboBox();
        langComboBox.setEditable(true);
        datatypeComboBox = new JComboBox();
        setupAutoCompleter(owlEditorKit);
        fillLangComboBox();
        fillDatatypeComboBox();
        layoutComponents();
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLConstant;
    }


    public boolean isPreferred(Object object) {
        return object instanceof OWLConstant;
    }


    public OWLConstant getEditedObject() {
        return getValue();
    }


    public void setEditedObject(OWLConstant object) {
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


    public void setValue(OWLConstant constant) {
        clear();
        if (constant != null) {
            annotationContent.setText(constant.getLiteral());
            if (constant.isTyped()) {
                datatypeComboBox.setSelectedItem(((OWLTypedConstant) constant).getDataType());
            }
            else {
                langComboBox.setSelectedItem(((OWLUntypedConstant) constant).getLang());
            }
        }
    }


    public OWLConstant getValue() {
        lastDatatype = null;
        lastLanguage = null;
        String value = annotationContent.getText();
        OWLConstant constant;
        if (isDataTypeSelected()) {
            constant = dataFactory.getOWLTypedConstant(value, getSelectedDataType());
            lastDatatype = getSelectedDataType();
        }
        else {
            if (isLangSelected()) {
                constant = dataFactory.getOWLUntypedConstant(value, getSelectedLang());
                lastLanguage = getSelectedLang();
            }
            else {
                constant = dataFactory.getOWLUntypedConstant(value);
            }
        }
        return constant;
    }


    private boolean isLangSelected() {
        return langComboBox.getSelectedItem() != null;
    }


    private boolean isDataTypeSelected() {
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
    private OWLDataType getSelectedDataType() {
        return (OWLDataType) datatypeComboBox.getSelectedItem();
    }


    private void fillLangComboBox() {
        langComboBox.setModel(new DefaultComboBoxModel(new String[]{null, "en", "de", "es", "fr", "pt"}));
    }


    private void fillDatatypeComboBox() {
        List<OWLDataType> datatypeList = new ArrayList<OWLDataType>();
        for (URI uri : XSDVocabulary.ALL_DATATYPES) {
            datatypeList.add(dataFactory.getOWLDataType(uri));
        }

        Collections.sort(datatypeList, new Comparator<OWLDataType>() {
            public int compare(OWLDataType o1, OWLDataType o2) {
                return o1.getURI().compareTo(o2.getURI());
            }
        });
        datatypeList.add(0, null);
        datatypeComboBox.setModel(new DefaultComboBoxModel(datatypeList.toArray()));
    }


    private void setupAutoCompleter(OWLEditorKit owlEditorKit) {
        autoCompleter = new OWLDescriptionAutoCompleter(owlEditorKit, annotationContent, new OWLExpressionChecker() {
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
