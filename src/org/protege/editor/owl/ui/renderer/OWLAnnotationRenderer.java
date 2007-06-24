package org.protege.editor.owl.ui.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import org.semanticweb.owl.model.OWLAnnotation;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Feb-2007<br><br>
 */
public class OWLAnnotationRenderer extends JPanel implements ListCellRenderer {

    private JLabel annotationURILabel;

    private JTextArea annotationContentArea;


    public OWLAnnotationRenderer() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        annotationURILabel = new JLabel();
        annotationURILabel.setForeground(Color.BLUE.darker());
        annotationContentArea = new JTextArea();
        annotationContentArea.setFont(new Font("lucida grande", Font.PLAIN, 12));
        annotationContentArea.setLineWrap(true);
        annotationContentArea.setWrapStyleWord(true);
        add(annotationURILabel, BorderLayout.NORTH);
        add(annotationContentArea, BorderLayout.SOUTH);
        annotationContentArea.setOpaque(false);
        annotationContentArea.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value instanceof OWLAnnotation) {
            OWLAnnotation anno = (OWLAnnotation) value;
            String ren = anno.getAnnotationURI().getFragment();
            String uri = anno.getAnnotationURI().toString();
            if (ren == null) {
                ren = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
            }
            annotationURILabel.setText(ren);
            annotationContentArea.setText(anno.getAnnotationValue().toString());
        }
        else {
            annotationURILabel.setText("WARNING!");
            annotationContentArea.setText(
                    "Value being rendered is NOT an annotation.  Probable flow control logic error.\n" + "Class: " + value.getClass().getName());
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
        }
        else {
            setBackground(list.getBackground());
        }
        return this;
    }
}
