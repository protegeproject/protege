package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Feb-2007<br><br>
 */
public class OWLAnnotationCellRenderer extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = 324857847551294897L;

    private JLabel annotationURILabel;

    private JTextArea annotationContentArea;

    private JLabel iconLabel;

    private OWLEditorKit owlEditorKit;

    private static final Color LABEL_COLOR = Color.BLUE.darker();

    private OWLOntology ontology;

    private Font normalFont;
    private Font activeOntologyFont;


    public OWLAnnotationCellRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));

        annotationURILabel = new JLabel();
        annotationURILabel.setForeground(LABEL_COLOR);

        normalFont = annotationURILabel.getFont();
        activeOntologyFont = normalFont.deriveFont(Font.BOLD);

        annotationContentArea = new JTextArea();
        annotationContentArea.setFont(new Font("lucida grande", Font.PLAIN, 12));
        annotationContentArea.setLineWrap(true);
        annotationContentArea.setWrapStyleWord(true);
        annotationContentArea.setOpaque(false);

        iconLabel = new JLabel();
        iconLabel.setIcon(OWLIcons.getIcon("individual.png"));

        JPanel contentPanel = new JPanel(new BorderLayout(3, 3));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
        contentPanel.setOpaque(false);
        contentPanel.add(annotationContentArea, BorderLayout.CENTER);
        contentPanel.add(iconLabel, BorderLayout.WEST);

        add(annotationURILabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.SOUTH);
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value instanceof OWLAnnotation) {
            OWLAnnotation anno = (OWLAnnotation) value;
            String ren = owlEditorKit.getModelManager().getRendering(anno.getProperty());
            String val = owlEditorKit.getModelManager().getRendering(anno.getValue());

            annotationURILabel.setText(ren);
            annotationContentArea.setText(val);

            if (OWLRendererPreferences.getInstance().isHighlightActiveOntologyStatements() &&
                owlEditorKit.getOWLModelManager().getActiveOntology().equals(ontology)) {
                annotationURILabel.setFont(activeOntologyFont);
            }
            else {
                annotationURILabel.setFont(normalFont);
            }

            if (isSelected) {
                annotationContentArea.setForeground(list.getSelectionForeground());
                annotationURILabel.setForeground(list.getSelectionForeground());
            }
            else {
                annotationContentArea.setForeground(list.getForeground());
                annotationURILabel.setForeground(LABEL_COLOR);
            }
            iconLabel.setVisible(anno.getValue() instanceof IRI);
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


    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }
}