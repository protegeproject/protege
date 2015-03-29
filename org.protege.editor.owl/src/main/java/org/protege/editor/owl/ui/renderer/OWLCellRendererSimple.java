package org.protege.editor.owl.ui.renderer;

import java.awt.Component;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;


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
        if (value == null){
            // so that null does not render with no height
            label.setPreferredSize(new Dimension(label.getPreferredSize().width, label.getFontMetrics(font).getHeight()));
        }
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
            OWLOntology o = owlEditorKit.getModelManager().getActiveOntology();
            if(ent instanceof OWLClass) {
                if(!o.getAxioms((OWLClass) ent,Imports.EXCLUDED).isEmpty()) {
                    makeBold(renderer);
                }
            }
            else if(ent instanceof OWLObjectProperty) {
                if(!o.getAxioms((OWLObjectProperty) ent,Imports.EXCLUDED).isEmpty()) {
                    makeBold(renderer);
                }
            }
            else if(ent instanceof OWLDataProperty) {
                if(!o.getAxioms((OWLDataProperty) ent,Imports.EXCLUDED).isEmpty()) {
                    makeBold(renderer);
                }
            }
            else if(ent instanceof OWLIndividual) {
                if(!o.getAxioms((OWLIndividual) ent,Imports.EXCLUDED).isEmpty()) {
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
