package org.protege.editor.owl.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.KRSS2DocumentFormat;
import org.semanticweb.owlapi.formats.LatexDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OBODocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;


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

    /**
     * 
     */
    private static final long serialVersionUID = 1207904671457290130L;

    private JComboBox formatComboBox;

    private JLabel messageLabel;


    public OntologyFormatPanel() {
        List<Object> formats = new ArrayList<Object>();
        formats.add(new RDFXMLDocumentFormat());
        formats.add(new OWLXMLDocumentFormat());
        formats.add(new FunctionalSyntaxDocumentFormat());
        formats.add(new ManchesterSyntaxDocumentFormat());
        
        formats.add(new OBODocumentFormat());
        formats.add(new KRSS2DocumentFormat());
        formats.add(new LatexDocumentFormat());
        formats.add(new TurtleDocumentFormat());

        formatComboBox = new JComboBox(formats.toArray());
        setLayout(new BorderLayout(12, 12));
        add(formatComboBox, BorderLayout.SOUTH);
        formatComboBox.setSelectedItem(formats.get(0));
    }


    public void setSelectedFormat(OWLDocumentFormat format) {
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


    public void setMessage(String message) {
        if (messageLabel == null){
            messageLabel = new JLabel(message);
            add(messageLabel, BorderLayout.NORTH);
        }
        else{
            messageLabel.setText(message);
        }
        revalidate();
    }


    public OWLDocumentFormat getSelectedFormat(){
        return (OWLDocumentFormat)formatComboBox.getSelectedItem();
    }


    public void addItemListener(ItemListener l){
        formatComboBox.addItemListener(l);
    }


    public void removeItemListener(ItemListener l){
        formatComboBox.removeItemListener(l);
    }


    /**
     * @deprecated Use the other showDialog instead.  This one doesn't explain itself.
     */
    @Deprecated
    public static OWLDocumentFormat showDialog(OWLEditorKit editorKit, OWLDocumentFormat defaultFormat) {
        return showDialog(editorKit, defaultFormat, null);
    }

    
    public static OWLDocumentFormat showDialog(OWLEditorKit editorKit, OWLDocumentFormat defaultFormat, String message) {
    	OntologyFormatPanel panel = new OntologyFormatPanel();
    	if (message != null){
    		panel.setMessage(message);
    	}
    	panel.setSelectedFormat(defaultFormat);
    	OWLDocumentFormat selectedFormat = null;
    	do {
    		int ret = JOptionPaneEx.showConfirmDialog(editorKit.getWorkspace(),
    				"Select an ontology format",
    				panel,
    				JOptionPane.PLAIN_MESSAGE,
    				JOptionPane.OK_CANCEL_OPTION,
    				panel.formatComboBox);
    		if (ret != JOptionPane.OK_OPTION) {
    			return null;
    		}

    		selectedFormat = panel.getSelectedFormat();
    	}
    	while (!isFormatOk(editorKit, selectedFormat));
    	return selectedFormat;
    }
    
    private static boolean isFormatOk(OWLEditorKit editorKit, OWLDocumentFormat format) {
    	if (!(format instanceof ManchesterSyntaxDocumentFormat)) {
    		return true;
    	}
    	int userSaysOk = JOptionPane.showConfirmDialog(editorKit.getOWLWorkspace(), 
    			                              "The Manchester OWL Syntax can lose information such as GCI's and annotations of undeclared entities.  Continue?", 
    			                              "Warning",
    			                              JOptionPane.YES_NO_OPTION);
    	return userSaysOk == JOptionPane.YES_OPTION;
    }
}
