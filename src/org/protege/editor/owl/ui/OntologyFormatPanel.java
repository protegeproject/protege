package org.protege.editor.owl.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLOntologyFormat;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyFormatPanel extends JPanel {

    private JComboBox formatComboBox;


    public OntologyFormatPanel() {
        List<Object> formats = new ArrayList<Object>();
        formats.add(new RDFXMLOntologyFormat());
        formats.add(new OWLXMLOntologyFormat());
        formats.add(new OWLFunctionalSyntaxOntologyFormat());
        formatComboBox = new JComboBox(formats.toArray());
        setLayout(new BorderLayout());
        add(formatComboBox, BorderLayout.NORTH);
        formatComboBox.setSelectedItem(formats.get(0));
    }


    public void setSelectedFormat(OWLOntologyFormat format) {
        if (format == null) {
            formatComboBox.setSelectedIndex(0);
        }
        for (int i = 0; i < formatComboBox.getModel().getSize(); i++) {
            if (formatComboBox.getModel().getElementAt(i).equals(format)) {
                formatComboBox.setSelectedIndex(i);
                return;
            }
        }
    }


    public static OWLOntologyFormat showDialog(OWLEditorKit editorKit, OWLOntologyFormat defaultFormat) {
        OntologyFormatPanel panel = new OntologyFormatPanel();
        panel.setSelectedFormat(defaultFormat);
        int ret = JOptionPaneEx.showConfirmDialog(editorKit.getWorkspace(),
                                                  "Select an ontology format",
                                                  panel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  panel.formatComboBox);
        if (ret != JOptionPane.OK_OPTION) {
            return null;
        }
        Object selObj = panel.formatComboBox.getSelectedItem();
        return (OWLOntologyFormat) selObj;
    }
}
