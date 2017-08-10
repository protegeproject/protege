package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;

public class DeleteRedirectAction extends AbstractAction {
    private DefaultMutableTreeNode node;
    private XMLCatalog catalog;
    private OntologyLibraryPanel.JTreeRefresh refreshTree;
    
    public DeleteRedirectAction(XMLCatalog catalog, DefaultMutableTreeNode nodeToDelete, OntologyLibraryPanel.JTreeRefresh action) {
        super("Delete Library Entry");
        node = nodeToDelete;
        this.catalog = catalog;
        refreshTree = action;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(node.getUserObject() !=null){
            catalog.removeEntry((Entry) node.getUserObject());
            refreshTree.refreshView();
        }
    }
}
