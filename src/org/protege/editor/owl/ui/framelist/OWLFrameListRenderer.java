package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.renderer.OWLAnnotationRenderer;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
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
 * Date: 29-Jan-2007<br><br>
 */
public class OWLFrameListRenderer implements ListCellRenderer {

    private OWLEditorKit owlEditorKit;

    private OWLCellRenderer owlCellRenderer;

    private ListCellRenderer separatorRenderer;

    private OWLAnnotationRenderer annotationRenderer;

    private boolean highlightKeywords;


    public OWLFrameListRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        owlCellRenderer = new OWLCellRenderer(owlEditorKit);
        separatorRenderer = new DefaultListCellRenderer();
        annotationRenderer = new OWLAnnotationRenderer();
        highlightKeywords = true;
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void setHighlightKeywords(boolean highlightKeywords) {
        this.highlightKeywords = highlightKeywords;
    }


    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     * @param list         The JList we're painting.
     * @param value        The value returned by list.getModel().getElementAt(index).
     * @param index        The cells index.
     * @param isSelected   True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     * @see javax.swing.JList
     * @see javax.swing.ListSelectionModel
     * @see javax.swing.ListModel
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {


        if (value instanceof OWLFrameSection) {
            JLabel label = (JLabel) separatorRenderer.getListCellRendererComponent(list,
                                                                                   " ",
                                                                                   index,
                                                                                   isSelected,
                                                                                   cellHasFocus);
            label.setVerticalAlignment(JLabel.TOP);
            return label;
        }
        else {

            if (((AbstractOWLFrameSectionRow) value).getAxiom() instanceof OWLAnnotationAxiom) {
                OWLAnnotationAxiom ax = (OWLAnnotationAxiom) ((AbstractOWLFrameSectionRow) value).getAxiom();
                return (JComponent) annotationRenderer.getListCellRendererComponent(list,
                                                                                    ax.getAnnotation(),
                                                                                    index,
                                                                                    isSelected,
                                                                                    cellHasFocus);
            }
            boolean commentedOut = false;
            OWLFrameSectionRow row = ((OWLFrameSectionRow) value);
            if (row.getOntology() != null && row.getAxiom() != null) {
                commentedOut = getOWLEditorKit().getOWLModelManager().isCommentedOut(row.getOntology(), row.getAxiom());
            }
            owlCellRenderer.setCommentedOut(commentedOut);
            Object valueToRender = getValueToRender(list, value, index, isSelected, cellHasFocus);
            owlCellRenderer.setIconObject(getIconObject(list, value, index, isSelected, cellHasFocus));
            owlCellRenderer.setOntology(((OWLFrameSectionRow) value).getOntology());
            owlCellRenderer.setTransparent();
            owlCellRenderer.setInferred(((OWLFrameSectionRow) value).isInferred());
            owlCellRenderer.setHighlightKeywords(highlightKeywords);
            return (JComponent) owlCellRenderer.getListCellRendererComponent(list,
                                                                             valueToRender,
                                                                             index,
                                                                             isSelected,
                                                                             cellHasFocus);
        }
    }


    protected OWLObject getIconObject(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof AbstractOWLFrameSectionRow) {
            AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
            if (!row.getManipulatableObjects().isEmpty()) {
                Object firstObject = row.getManipulatableObjects().iterator().next();
                if (firstObject instanceof OWLObject) {
                    return (OWLObject) firstObject;
                }
            }
        }
        return null;
    }


    protected Object getValueToRender(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof AbstractOWLFrameSectionRow) {
            AbstractOWLFrameSectionRow row = (AbstractOWLFrameSectionRow) value;
            StringBuilder sb = new StringBuilder();
            sb.append(row.getPrefix());
            for (Iterator<? extends OWLObject> it = row.getManipulatableObjects().iterator(); it.hasNext();) {
                Object curObj = it.next();
                OWLObjectRenderer ren = getOWLEditorKit().getOWLModelManager().getOWLObjectRenderer();
                OWLEntityRenderer entRen = getOWLEditorKit().getOWLModelManager().getOWLEntityRenderer();
                sb.append(ren.render(((OWLObject) curObj), entRen));
                if (it.hasNext()) {
                    sb.append(row.getDelimeter());
                }
            }
            sb.append(row.getSuffix());
            return sb.toString();
        }
        return value;
    }
}
