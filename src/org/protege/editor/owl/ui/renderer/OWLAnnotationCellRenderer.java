package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Feb-2007<br><br>
 */
public class OWLAnnotationCellRenderer extends JPanel implements ListCellRenderer {

    private JLabel annotationURILabel;

    private JTextArea annotationContentArea;

    private JLabel iconLabel;

    private OWLEditorKit owlEditorKit;

    private static final Color LABEL_COLOR = Color.BLUE.darker();


    public OWLAnnotationCellRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        annotationURILabel = new JLabel();
        annotationURILabel.setForeground(LABEL_COLOR);
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
            String val = owlEditorKit.getOWLModelManager().getRendering(anno.getAnnotationValue());
            annotationContentArea.setText(val);
            if (isSelected) {
                annotationContentArea.setForeground(list.getSelectionForeground());
                annotationURILabel.setForeground(list.getSelectionForeground());
            }
            else {
                annotationContentArea.setForeground(list.getForeground());
                annotationURILabel.setForeground(LABEL_COLOR);
            }
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