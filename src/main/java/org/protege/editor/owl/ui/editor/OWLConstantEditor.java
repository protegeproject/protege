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

package org.protege.editor.owl.ui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

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

    public OWLConstantEditor(OWLEditorKit owlEditorKit) {
        dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
        annotationContent = new JTextArea(8, 40);
        annotationContent.setWrapStyleWord(true);
        annotationContent.setLineWrap(true);

        final UIHelper uiHelper = new UIHelper(owlEditorKit);
        langComboBox = uiHelper.getLanguageSelector();
        
        datatypeComboBox = uiHelper.getDatatypeSelector();
        datatypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OWLDatatype owlDatatype = (OWLDatatype) getSelectedDatatype();
				boolean b = owlDatatype == null ? true : false;
				toggleLanguage(b);
			}
		});
        
        int numItems = datatypeComboBox.getItemCount();
        for (int i=0; i<numItems; i++) {
        	OWLDatatype owlDatatype = (OWLDatatype) datatypeComboBox.getItemAt(i);

        	if (owlDatatype == null) continue;
        	
        	if (owlDatatype.isRDFPlainLiteral()) {
        		datatypeComboBox.removeItemAt(i);
        		break;
        	}
        }

        setupAutoCompleter(owlEditorKit);
        layoutComponents();
    }
    
    private void toggleLanguage(boolean b) {
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
        String value = annotationContent.getText();
        OWLLiteral constant;
        if (isDatatypeSelected()) {
        	constant = dataFactory.getOWLLiteral(value, getSelectedDatatype());
            lastDatatype = getSelectedDatatype();
        }
        else {
            if (isLangSelected()) {
            	constant = dataFactory.getOWLLiteral(value, getSelectedLang());
                lastLanguage = getSelectedLang();
            }
            else {
                constant = dataFactory.getOWLStringLiteral(value, null);
            }
        }
        return constant;
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
            }
            else {
                langComboBox.setSelectedItem(constant.getLang());
            }
        }
        return true;
    }

    public boolean isMultiEditSupported() {
        return false;
    }

    public String getEditorTypeName() {
        return "Constant";
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
     * @return The selected datatype, or <code>null</code>
     *         if no datatype is selected.
     */
    private OWLDatatype getSelectedDatatype() {
        return (OWLDatatype) datatypeComboBox.getSelectedItem();
    }

	private void setupAutoCompleter(OWLEditorKit owlEditorKit) {
		new OWLAutoCompleter(owlEditorKit, annotationContent, new OWLExpressionChecker() {
			public void check(String text) throws OWLExpressionParserException {
				throw new OWLExpressionParserException(text, 0, text.length(), true, true, true, true, true, true, new HashSet<String>());
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
