package org.protege.editor.owl.ui.framelist;

import org.protege.editor.core.ui.split.ViewSplitPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.OWLAxiomAnnotationFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
 * Date: 02-Jul-2007<br><br>
 */
public class OWLFrameListComponent<R extends OWLObject> extends JPanel {

    private OWLEditorKit editorKit;

    private OWLFrameList2<R> mainList;

    private OWLFrameList2<OWLAxiom> annotationFrameList;

    private JComponent annotationComponent;

    private LeftPointingBorder leftPointingBorder;


    public OWLFrameListComponent(OWLEditorKit editorKit, OWLFrame<R> frame) {
        setLayout(new BorderLayout());
        mainList = new OWLFrameList2<R>(editorKit, frame);
        this.editorKit = editorKit;
        annotationFrameList = new OWLFrameList2<OWLAxiom>(editorKit,
                                                          new AnnotationAxiomsFrame(editorKit.getOWLModelManager().getOWLOntologyManager()));
        JSplitPane sp = new ViewSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp.setBorder(null);
        sp.setResizeWeight(0.5);
        setLayout(new BorderLayout());
        add(sp);
        JScrollPane mainListScrollPane = new JScrollPane(mainList);
//        mainListScrollPane.setBorder(null);
        sp.setLeftComponent(mainListScrollPane);


        annotationComponent = new JScrollPane(annotationFrameList);

        Border b = annotationComponent.getBorder();

        annotationComponent.setBorder(BorderFactory.createCompoundBorder(b,
                                                                         BorderFactory.createCompoundBorder(
                                                                                 leftPointingBorder = new LeftPointingBorder(),
                                                                                 BorderFactory.createEmptyBorder(1,
                                                                                                                 2,
                                                                                                                 1,
                                                                                                                 1))));
        sp.setRightComponent(annotationComponent);
        mainList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                sync();
            }
        });
        mainList.setMinimumSize(new Dimension(10, 10));
        annotationFrameList.setMinimumSize(new Dimension(10, 10));
        mainListScrollPane.getViewport().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                sync();
            }
        });
    }


    private void sync() {
        Object selVal = mainList.getSelectedValue();
        if (selVal instanceof OWLFrameSectionRow) {
            OWLAxiom ax = ((OWLFrameSectionRow) selVal).getAxiom();
            if (ax != null) {
                annotationFrameList.setRootObject(ax);
            }
            int selIndex = mainList.getSelectedIndex();
            Rectangle cellBounds = mainList.getCellBounds(selIndex, selIndex);
            cellBounds.y = cellBounds.y + (cellBounds.height / 2);
            cellBounds = SwingUtilities.convertRectangle(mainList, cellBounds, annotationFrameList);
            leftPointingBorder.setPointY(cellBounds.y);
        }
        else {
            annotationFrameList.setRootObject(null);
            leftPointingBorder.setPointY(-100);
        }
        annotationComponent.repaint();
    }


    public void setOWLObject(R obj) {
        mainList.setRootObject(obj);
    }


    private class AnnotationAxiomsFrame extends AbstractOWLFrame<OWLAxiom> {


        public AnnotationAxiomsFrame(OWLOntologyManager owlOntologyManager) {
            super(owlOntologyManager);
            addSection(new OWLAxiomAnnotationFrameSection(editorKit, this));
        }
    }


    private class LeftPointingBorder implements Border {

        private Insets i;

        private int pointY = 0;

        private int left = 20;

        private Stroke s;


        public LeftPointingBorder() {
            i = new Insets(2, left + 2, 2, 2);
            s = new BasicStroke(2.0f);
        }


        public void setPointY(int pointY) {
            this.pointY = pointY;
        }


        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            Color oldColor = g2.getColor();
            g2.setColor(Color.LIGHT_GRAY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawArc(x - left, pointY - left * 2, left * 2, left * 2, 0, -90);
            g2.drawArc(x - left, pointY, left * 2, left * 2, 0, 90);
            g2.drawLine(x + left, 1, x + left, pointY - left);
            g2.drawLine(x + left, pointY + left, x + left, y + height);
//            g2.drawRoundRect(x + left, y, width - left - 2, height - 2, 3, 3);
//            g2.setStroke(oldStroke);
            g2.setColor(oldColor);
        }


        public Insets getBorderInsets(Component c) {
            return i;
        }


        public boolean isBorderOpaque() {
            return false;
        }
    }
}
