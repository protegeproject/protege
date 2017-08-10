package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.NextCatalogEntry;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by vblagodarov on 08-08-17.
 */
public class EditNextCatalogAction extends EditLibraryEntryAction<NextCatalogEntry> {

    public EditNextCatalogAction(JTree parent, DefaultMutableTreeNode selectedNode, XMLCatalog catalog, OntologyLibraryPanel.JTreeRefresh action) {
        super("Next catalog physical location", parent, selectedNode, catalog, action);
    }

    @Override
    protected ILibraryEntryEditor<NextCatalogEntry> getNewEditorPanel(NextCatalogEntry entry) {
        return new NextCatalogEntryEditorPanel(entry);
    }
}
