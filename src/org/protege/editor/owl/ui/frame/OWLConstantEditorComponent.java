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
            return owlEditorKit.getModelManager().getOWLDataFactory().getOWLUntypedConstant(editorField.getText().trim());
        }
        else {
            return owlEditorKit.getModelManager().getOWLDataFactory().getOWLTypedConstant(editorField.getText().trim(),
                                                                                             dataType);
        }
    }
}
