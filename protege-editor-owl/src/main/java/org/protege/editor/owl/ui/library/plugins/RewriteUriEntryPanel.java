package org.protege.editor.owl.ui.library.plugins;

import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.editor.owl.ui.library.RewriteUriEntryEditorPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.RewriteUriEntry;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by vblagodarov on 03-08-17.
 */
public class RewriteUriEntryPanel extends NewEntryPanel {
    private static final long serialVersionUID = -1338724727207898549L;
    private XMLCatalog catalog;
    private RewriteUriEntryEditorPanel rewriteEditorPanel;

    public RewriteUriEntryPanel(XMLCatalog xmlCatalog){
        catalog = xmlCatalog;
        setLayout(new BorderLayout());
        rewriteEditorPanel = new RewriteUriEntryEditorPanel(catalog, new DocumentListener() {
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
        add(rewriteEditorPanel.getComponent());
    }

    @Override
    public RewriteUriEntry getEntry() {
        return rewriteEditorPanel.getEntry(getUniqueId());
    }
}
