package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
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
 * Date: 29-Nov-2007<br><br>
 *
 * A simple renderer for trees and lists (where syntax highlighting etc. are
 * not required).
 */
public class OWLCellRendererSimple implements TreeCellRenderer, ListCellRenderer {

    private OWLEditorKit owlEditorKit;

    private DefaultTreeCellRenderer treeCellRendererDelegate;

    private DefaultListCellRenderer listCellRenderDelegate;

    public OWLCellRendererSimple(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        treeCellRendererDelegate = new DefaultTreeCellRenderer();
        listCellRenderDelegate = new DefaultListCellRenderer();
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) treeCellRendererDelegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        prepareRenderer(label, value);
        return label;
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) listCellRenderDelegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        prepareRenderer(label, value);
        return label;
    }


    private void prepareRenderer(JLabel label, Object value) {
        final Font font = OWLRendererPreferences.getInstance().getFont();
        label.setFont(font);
        setText(value, label);
        setIcon(value, label);
        boldIfNecessary(value, label);
    }


    private void setText(Object value, JLabel renderer) {
        if(value instanceof OWLObject) {
            OWLObject obj = (OWLObject) value;
            String rendering = owlEditorKit.getModelManager().getRendering(obj);
            renderer.setText(rendering);
        }
    }

    private void setIcon(Object value, JLabel renderer) {
        if(value instanceof OWLObject) {
            OWLObject obj = (OWLObject) value;
            Icon icon = owlEditorKit.getWorkspace().getOWLIconProvider().getIcon(obj);
            renderer.setIcon(icon);
        }
    }


    private void boldIfNecessary(Object value, JLabel renderer) {
        if(value instanceof OWLEntity) {
            OWLEntity ent = (OWLEntity) value;
            if(ent instanceof OWLClass) {
                if(!owlEditorKit.getModelManager().getActiveOntology().getAxioms((OWLClass) ent).isEmpty()) {
                    makeBold(renderer);
                }
            }
            else if(ent instanceof OWLObjectProperty) {
                if(!owlEditorKit.getModelManager().getActiveOntology().getAxioms((OWLObjectProperty) ent).isEmpty()) {
                    makeBold(renderer);
                }
            }
            else if(ent instanceof OWLDataProperty) {
                if(!owlEditorKit.getModelManager().getActiveOntology().getAxioms((OWLDataProperty) ent).isEmpty()) {
                    makeBold(renderer);
                }
            }
            else if(ent instanceof OWLIndividual) {
                if(!owlEditorKit.getModelManager().getActiveOntology().getAxioms((OWLIndividual) ent).isEmpty()) {
                    makeBold(renderer);
                }
            }
        }
    }


    private static void makeBold(JLabel label) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><b>");
        sb.append(label.getText());
        sb.append("</b></body></html>");
        label.setText(sb.toString());
    }
}
