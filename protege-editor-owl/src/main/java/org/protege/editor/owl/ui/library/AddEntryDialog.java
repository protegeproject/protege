package org.protege.editor.owl.ui.library;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.model.library.CatalogEntryManager;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.slf4j.LoggerFactory;

public class AddEntryDialog extends JPanel {

    private List<CatalogEntryManager> entryManagers;

    private XMLCatalog catalog;

    private JTabbedPane tabs;

    @Nullable
    public static Entry askUserForRepository(JComponent parent,
                                             XMLCatalog catalog,
                                             List<CatalogEntryManager> entryManagers) {
        AddEntryDialog dialog = new AddEntryDialog(entryManagers, catalog);
        int ret = JOptionPaneEx.showConfirmDialog(parent, "Add catalog entry", dialog, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        if(ret != JOptionPane.OK_OPTION) {
            return null;
        }
        Entry e = dialog.getEntry();
        if(e == null) {
            return e;
        }
        catalog.addEntry(0, e);
        for(CatalogEntryManager entryManager : entryManagers) {
            if(entryManager.isSuitable(e)) {
                try {
                    entryManager.update(e);
                } catch(IOException ex) {
                    LoggerFactory.getLogger(AddEntryDialog.class)
                            .error("An error occurred whilst adding a catalog entry: ", ex);
                }
            }
        }
        return e;
    }

    private AddEntryDialog(List<CatalogEntryManager> entryManagers,
                           XMLCatalog catalog) {
        this.entryManagers = entryManagers;
        this.catalog = catalog;
        setLayout(new BorderLayout());
        add(createTabbedPane(), BorderLayout.CENTER);
        repaint();
    }

    private JTabbedPane createTabbedPane() {
        tabs = new JTabbedPane();
        for(final CatalogEntryManager entryManager : entryManagers) {
            NewEntryPanel panel = entryManager.newEntryPanel(catalog);
            if(panel != null) {
                panel.setAlignmentY(CENTER_ALIGNMENT);
                tabs.addTab(entryManager.getDescription(), panel);
            }
        }
        return tabs;
    }

    public Entry getEntry() {
        NewEntryPanel panel = (NewEntryPanel) tabs.getSelectedComponent();
        return panel.getEntry();
    }
}
