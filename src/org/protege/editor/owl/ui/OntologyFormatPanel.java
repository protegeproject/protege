package org.protege.editor.owl.ui;

import de.uulm.ecs.ai.owl.krssparser.KRSS2OntologyFormat;
import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owl.latex.LatexOntologyFormat;
import org.coode.owl.rdf.turtle.TurtleOntologyFormat;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLOntologyFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;


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
        formats.add(new ManchesterOWLSyntaxOntologyFormat());
        
        formats.add(new KRSS2OntologyFormat());       
        formats.add(new LatexOntologyFormat());
        formats.add(new TurtleOntologyFormat());
        
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


    public OWLOntologyFormat getSelectedFormat(){
        return (OWLOntologyFormat)formatComboBox.getSelectedItem();
    }


    public void addItemListener(ItemListener l){
        formatComboBox.addItemListener(l);
    }


    public void removeItemListener(ItemListener l){
        formatComboBox.removeItemListener(l);
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

        return panel.getSelectedFormat();
    }
}
