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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

    private JTextArea editorField;

    private JComboBox datatypeCombo;

    private JSplitPane splitter;

    private java.util.List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public OWLConstantEditorComponent(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new BorderLayout(7, 7));
        editorField = new JTextArea();
        editorField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent event) {
                notifyChanges();
            }

            public void removeUpdate(DocumentEvent event) {
                notifyChanges();
            }

            public void changedUpdate(DocumentEvent event) {
                notifyChanges();
            }
        });
        editorField.setColumns(20);
        JPanel editorFieldHolder = new JPanel(new BorderLayout());
        editorFieldHolder.add(new JScrollPane(editorField), BorderLayout.CENTER);
        editorFieldHolder.setBorder(ComponentFactory.createTitledBorder("Value"));

        datatypeCombo = createDataTypeCombo();
        JPanel datatypeBorderPanel = new JPanel(new BorderLayout());
        datatypeBorderPanel.add(datatypeCombo, BorderLayout.NORTH);
        datatypeBorderPanel.setBorder(ComponentFactory.createTitledBorder("Datatype"));

        splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, editorFieldHolder, datatypeBorderPanel);
        splitter.setBorder(BorderFactory.createEmptyBorder());
        add(splitter, BorderLayout.CENTER);
    }


    private JComboBox createDataTypeCombo() {
        TreeSet<URI> ts = new TreeSet<URI>();
        ts.addAll(XSDVocabulary.ALL_DATATYPES);
        ArrayList<OWLDataType> datatypes = new ArrayList<OWLDataType>();
        datatypes.add(null);
        for (URI datatypeURI : ts) {
            datatypes.add(new OWLDataTypeImpl(null, datatypeURI));
        }
        JComboBox c = new JComboBox(datatypes.toArray());
        c.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent event) {
                notifyChanges();
            }
        });
        c.setRenderer(new OWLCellRenderer(owlEditorKit));
        return c;
    }


    private void notifyChanges() {
        for (ChangeListener l : listeners){
            l.stateChanged(new ChangeEvent(this));
        }
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
        final String valueText = editorField.getText();
        OWLDataType dataType = (OWLDataType) datatypeCombo.getSelectedItem();
        if (dataType == null) {
            return owlEditorKit.getModelManager().getOWLDataFactory().getOWLUntypedConstant(valueText.trim());
        }
        else {
            return owlEditorKit.getModelManager().getOWLDataFactory().getOWLTypedConstant(valueText.trim(),
                                                                                             dataType);
        }
    }


    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
}
