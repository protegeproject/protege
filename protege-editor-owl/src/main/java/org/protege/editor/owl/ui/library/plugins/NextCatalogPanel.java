package org.protege.editor.owl.ui.library.plugins;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.editor.owl.ui.library.NextCatalogEntryEditorPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.NextCatalogEntry;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by vblagodarov on 02-08-17.
 */
public class NextCatalogPanel extends NewEntryPanel {

    private static final long serialVersionUID = 3696300927123324406L;
    private XMLCatalog catalog;
    NextCatalogEntryEditorPanel catalogEntryPanel;

    public NextCatalogPanel(XMLCatalog xmlCatalog){
        catalog = xmlCatalog;
        setLayout(new BorderLayout());
        catalogEntryPanel = new NextCatalogEntryEditorPanel(catalog, new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireListeners();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireListeners();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireListeners();
            }
        });
        add(catalogEntryPanel.getComponent());
        fireListeners();
    }

    @Override
    public NextCatalogEntry getEntry() {
        return catalogEntryPanel.getEntry(getUniqueId() , null);
    }
}
