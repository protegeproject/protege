package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
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
