package org.protege.editor.owl.ui.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLEntity;

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

    private JLabel iconLabel;

    private OWLEditorKit owlEditorKit;


    public OWLAnnotationRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        annotationURILabel = new JLabel();
        annotationURILabel.setForeground(Color.BLUE.darker());
        annotationContentArea = new JTextArea();
        annotationContentArea.setFont(new Font("lucida grande", Font.PLAIN, 12));
        annotationContentArea.setLineWrap(true);
        annotationContentArea.setWrapStyleWord(true);
        add(annotationURILabel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(3, 3));
        contentPanel.add(annotationContentArea, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
        annotationContentArea.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
        contentPanel.setOpaque(false);
        iconLabel = new JLabel();
        contentPanel.add(iconLabel, BorderLayout.WEST);
        iconLabel.setIcon(OWLIcons.getIcon("individual.png"));
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
            String val = owlEditorKit.getOWLModelManager().getOWLObjectRenderer().render(anno.getAnnotationValue(),
                                                                                         owlEditorKit.getOWLModelManager().getOWLEntityRenderer());
            annotationContentArea.setText(val);
            iconLabel.setVisible(anno.getAnnotationValue() instanceof OWLEntity);
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
