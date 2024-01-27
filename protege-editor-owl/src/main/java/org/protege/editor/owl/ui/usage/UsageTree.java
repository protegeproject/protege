package org.protege.editor.owl.ui.usage;

import static java.util.stream.Collectors.toList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.tree.OWLLinkedObjectTree;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.Copyable;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class UsageTree extends OWLLinkedObjectTree implements Copyable {


    private OWLEditorKit owlEditorKit;

    private OWLEntity entity;

    private final ChangeListenerMediator changeListenerMediator = new ChangeListenerMediator();

    public UsageTree(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        this.owlEditorKit = owlEditorKit;
        setCellRenderer(new UsageTreeCellRenderer(owlEditorKit));
        getSelectionModel().addTreeSelectionListener(e -> changeListenerMediator.fireStateChanged(UsageTree.this));
    }

    /**
     * Determines whether or not at least one <code>OWLObject</code>
     * can be copied.
     *
     * @return <code>true</code> if at least one object can be copied, or
     * <code>false</code> if no objects can't be copied.
     */
    @Override
    public boolean canCopy() {
        TreePath[] selectionPaths = getSelectionPaths();
        if(selectionPaths == null) {
            return false;
        }
        return Arrays.stream(selectionPaths)
                .map(TreePath::getLastPathComponent)
                .filter(node -> node instanceof UsageByEntityTreeModel.UsageTreeNode)
                .findFirst()
                .isPresent();
    }

    /**
     * Gets the objects that will be copied.
     *
     * @return A <code>List</code> of <code>OWLObjects</code> that will be
     * copied to the clip board
     */
    @Override
    public List<OWLObject> getObjectsToCopy() {
        TreePath[] selectionPaths = getSelectionPaths();
        if(selectionPaths == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(selectionPaths)
                .map(TreePath::getLastPathComponent)
                .filter(node -> node instanceof UsageByEntityTreeModel.UsageTreeNode)
                .map(node -> ((UsageByEntityTreeModel.UsageTreeNode) node).getAxiom())
                .collect(toList());
    }

    /**
     * Adds a change listener.  If the ability to copy OWL objects changes, the
     * copyable component should notify listeners through a change of state event.
     *
     * @param changeListener The listener to be added.
     * @see ChangeListenerMediator Components may want to use the <code>ChangeListenerMediator</code>
     * class to manage event notifcation and addition/removal of listeners
     */
    @Override
    public void addChangeListener(ChangeListener changeListener) {
        changeListenerMediator.addChangeListener(changeListener);
    }

    /**
     * Removes a previously added change listener.
     *
     * @param changeListener The ChangeListener to be removed.
     */
    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        changeListenerMediator.removeChangeListener(changeListener);
    }

    public void setOWLEntity(OWLEntity entity) {
        this.entity = entity;

        final UsagePreferences p = UsagePreferences.getInstance();
        final UsageTreeModel model = new UsageByEntityTreeModel(owlEditorKit);
        model.addFilters(p.getActiveFilters());
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
