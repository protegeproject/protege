package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

public class DeleteRedirectAction extends AbstractAction {
    private JTree tree;
    private TreePath selectionPath;
    
    public DeleteRedirectAction(JTree tree, TreePath selectionPath) {
        super("Delete Library Entry");
        this.selectionPath = selectionPath;
        this.tree = tree;
    }
    
    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode nodeToDelete = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        boolean deleteFromTree = false;
        Entry toDelete = (Entry) nodeToDelete.getUserObject();
        Object o = ((DefaultMutableTreeNode) selectionPath.getParentPath().getLastPathComponent()).getUserObject();
        
        if (o instanceof GroupEntry) {
            GroupEntry group = (GroupEntry) o;
            group.removeEntry(toDelete);
            deleteFromTree = true;
        }
        else if (o instanceof NextCatalogEntry) {
            NextCatalogEntry subCatalogEntry = (NextCatalogEntry) o;
            throw new UnsupportedOperationException("Not implemented yet");
        }
        if (deleteFromTree)  {
            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(nodeToDelete);
            tree.repaint();
        }
    }
}
