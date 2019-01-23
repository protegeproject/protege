package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by vblagodarov on 09-08-17.
 */
public abstract class EditLibraryEntryAction<T extends Entry> extends AbstractAction {

    private JTree tree;
    private OntologyLibraryPanel.JTreeRefresh refreshAction;
    private DefaultMutableTreeNode node;
    private XMLCatalog catalog;
    private String title;

    public EditLibraryEntryAction(String windowTitle, JTree parent, DefaultMutableTreeNode selectedNode, XMLCatalog catalog, OntologyLibraryPanel.JTreeRefresh action) {
        super("Edit Library Entry");
        tree = parent;
        node = selectedNode;
        this.catalog = catalog;
        refreshAction = action;
        title = windowTitle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        T entry = (T) node.getUserObject();
        ILibraryEntryEditor<T> entryEditor = getNewEditorPanel(entry);
        final JButton ok = new JButton("OK");
        ok.setEnabled(true);
        ok.addActionListener(e1 -> {
            Window w = SwingUtilities.getWindowAncestor(ok);
            if (w != null) {
                w.setVisible(false);
                T edited = entryEditor.getEntry(entry);
                if (edited == null) {
                    return;
                }
                try {
                    catalog.replaceEntry(entry, edited);
                    node.setUserObject(edited);
                    refreshAction.refreshView();
                } catch (Exception ex) {
                    LoggerFactory.getLogger(EditUriAction.class)
                            .error("An error occurred while updating the catalog entry", ex);
                }
            }
        });
        entryEditor.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ok.setEnabled(entryEditor.isInputValid());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ok.setEnabled(entryEditor.isInputValid());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ok.setEnabled(entryEditor.isInputValid());
            }
        });

        JOptionPane.showOptionDialog(tree, entryEditor.getComponent(), title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new JButton[]{ok}, ok);
    }

    protected abstract ILibraryEntryEditor<T> getNewEditorPanel(T entry);
}
