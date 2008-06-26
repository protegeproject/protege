package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.tree.OWLLinkedObjectTree;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class UsageTree extends OWLLinkedObjectTree {

    private OWLEditorKit owlEditorKit;

    private OWLEntity entity;


    public UsageTree(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        this.owlEditorKit = owlEditorKit;
        setCellRenderer(new UsageTreeCellRenderer(owlEditorKit));
    }


    public void setOWLEntity(OWLEntity entity) {
        this.entity = entity;

        final UsagePreferences p = UsagePreferences.getInstance();
        final UsageTreeModel model = new UsageTreeModel(owlEditorKit);
        model.setFilterSimpleSubclassAxioms(p.getFilterSimpleSubclassAxioms());
        model.setFilterDisjointAxioms(p.getFilterDisjointAxioms());
        model.setOWLEntity(entity);
        setModel(model);

        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
            if (i > 100) {
                break;
            }
        }
    }


    private class UsageTreeCellRenderer extends OWLCellRenderer {


        public UsageTreeCellRenderer(OWLEditorKit owlEditorKit) {
            super(owlEditorKit);
        }


        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            setFocusedEntity(entity);
            JComponent c = (JComponent) super.getTreeCellRendererComponent(tree,
                                                                           node.getUserObject(),
                                                                           sel,
                                                                           expanded,
                                                                           leaf,
                                                                           row,
                                                                           hasFocus);
            if (node.getUserObject() instanceof OWLAxiom) {
                if (node.getParent().getIndex(node) == node.getParent().getChildCount() - 1) {
                    c.setBorder(BorderFactory.createMatteBorder(1, 20, 20, 0, tree.getBackground()));
                }
                else {
                    c.setBorder(BorderFactory.createMatteBorder(1, 20, 0, 0, tree.getBackground()));
                }
            }
            else {
                c.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
            }
            setHighlightKeywords(true);
            return c;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color oldColor = g.getColor();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < getRowCount(); i++) {
            Rectangle rowBounds = getRowBounds(i);
            if (g.getClipBounds().intersects(rowBounds)) {
                if (getPathForRow(i).getPathCount() == 2) {
                    g.drawLine(0, rowBounds.y, getWidth(), rowBounds.y);
                }
            }
        }
        g.setColor(oldColor);
    }
}
