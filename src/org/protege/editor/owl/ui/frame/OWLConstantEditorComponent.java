package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.vocab.XSDVocabulary;
import uk.ac.manchester.cs.owl.OWLDataTypeImpl;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.TreeSet;
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
 * Date: 20-May-2007<br><br>
 */
public class OWLConstantEditorComponent extends JPanel {

    private OWLEditorKit owlEditorKit;

    private JTextField editorField;

    private JComboBox datatypeCombo;


    public OWLConstantEditorComponent(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new BorderLayout(7, 7));
        editorField = new JTextField(20);
        JPanel editorFieldHolder = new JPanel(new BorderLayout());
        editorFieldHolder.add(editorField, BorderLayout.NORTH);
        editorFieldHolder.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(editorFieldHolder, BorderLayout.NORTH);
        JPanel datatypeComboHolder = new JPanel(new BorderLayout());
        fillDataTypeCombo();
        JPanel datatypeBorderPanel = new JPanel(new BorderLayout());
        datatypeBorderPanel.add(datatypeCombo, BorderLayout.NORTH);
        datatypeBorderPanel.setBorder(ComponentFactory.createTitledBorder("Datatype"));
        datatypeComboHolder.add(datatypeBorderPanel, BorderLayout.NORTH);
        add(datatypeComboHolder);
    }


    private void fillDataTypeCombo() {
        TreeSet<URI> ts = new TreeSet<URI>();
        ts.addAll(XSDVocabulary.ALL_DATATYPES);
        ArrayList<OWLDataType> datatypes = new ArrayList<OWLDataType>();
        datatypes.add(null);
        for (URI datatypeURI : ts) {
            datatypes.add(new OWLDataTypeImpl(null, datatypeURI));
        }
        datatypeCombo = new JComboBox(datatypes.toArray());
        datatypeCombo.setRenderer(new OWLCellRenderer(owlEditorKit));
    }


    public void setOWLConstant(OWLConstant con) {
        editorField.setText("");
        datatypeCombo.setSelectedItem(null);
        if (con != null) {
            editorField.setText(con.getLiteral());
            if (con.isTyped()) {
                datatypeCombo.setSelectedItem(((OWLTypedConstant) con).getDataType());
            }
        }
    }


    public OWLConstant getOWLConstant() {
        OWLDataType dataType = (OWLDataType) datatypeCombo.getSelectedItem();
        if (dataType == null) {
            return owlEditorKit.getOWLModelManager().getOWLDataFactory().getOWLUntypedConstant(editorField.getText().trim());
        }
        else {
            return owlEditorKit.getOWLModelManager().getOWLDataFactory().getOWLTypedConstant(editorField.getText().trim(),
                                                                                             dataType);
        }
    }
}
