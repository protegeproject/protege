package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.RewriteUriEntry;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by vblagodarov on 09-08-17.
 */
public class EditRewriteUriAction extends EditLibraryEntryAction<RewriteUriEntry> {
    public EditRewriteUriAction(JTree parent, DefaultMutableTreeNode selectedNode, XMLCatalog catalog, OntologyLibraryPanel.JTreeRefresh action) {
        super("Rewrite uri entry", parent, selectedNode, catalog, action);
    }

    @Override
    protected ILibraryEntryEditor<RewriteUriEntry> getNewEditorPanel(RewriteUriEntry entry) {
        return new RewriteUriEntryEditorPanel(entry);
    }
}
