package org.protege.editor.owl.ui.library;

import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.RewriteUriEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by vblagodarov on 09-08-17.
 */
public class RewriteUriEntryEditorPanel implements ILibraryEntryEditor<RewriteUriEntry> {

    private JPanel mainComponent;
    private XmlBaseContext baseContext;
    private JTextField startPrefix;
    private JTextField rewriteByURI;
    private final Logger logger = LoggerFactory.getLogger(RewriteUriEntryEditorPanel.class);

    public RewriteUriEntryEditorPanel(XmlBaseContext context) {
        baseContext = context;
        initMainComponent("", "");
    }

    public RewriteUriEntryEditorPanel(RewriteUriEntry entry) {
        baseContext = entry.getXmlBaseContext();
        if (entry != null) {
            initMainComponent(entry.getUriStartString(), entry.getRewritePrefix().toString());
        } else {
            initMainComponent("", "");
        }
    }

    public RewriteUriEntryEditorPanel(XmlBaseContext context, DocumentListener changeStartPrefixListener) {
        this(context);
        addDocumentListener(changeStartPrefixListener);
    }

    public RewriteUriEntry getEntry(String id) {
        if (!isInputValid()) {
            return null;
        }
        String rewrite = rewriteByURI.getText().trim();
        try {
            return new RewriteUriEntry(id, baseContext, startPrefix.getText().trim(), rewrite == null ? new URI("") : new URI(rewrite));
        } catch (URISyntaxException e) {
            logger.error("Rewrite prefix is not a valid URI: {}.", rewrite == null ? "<null>" : rewrite, e);
        }
        return null;
    }

    @Override
    public RewriteUriEntry getEntry(RewriteUriEntry oldEntry) {
        return isInputValid() ? getEntry(oldEntry.getId()) : null;
    }

    @Override
    public boolean isInputValid() {
        String prefix = startPrefix.getText();
        if (prefix == null) {
            return false;
        }
        return !(prefix.trim().isEmpty());
    }

    @Override
    public void addDocumentListener(DocumentListener documentListener) {
        startPrefix.getDocument().addDocumentListener(documentListener);
    }

    public Component getComponent() {
        return mainComponent;
    }

    private void initMainComponent(String defaultPrefix, String defaultRewrite) {
        mainComponent = new JPanel();
        mainComponent.setLayout(new BoxLayout(mainComponent, BoxLayout.Y_AXIS));

        JPanel start = new JPanel();
        start.setLayout(new FlowLayout(FlowLayout.LEADING));
        start.add(new JLabel("Start prefix: "));
        startPrefix = new JTextField();
        startPrefix.setText(defaultPrefix);
        startPrefix.setPreferredSize(NewEntryPanel.getDefaultTextFieldDimension());
        rewriteByURI = new JTextField();
        rewriteByURI.setText(defaultRewrite);
        rewriteByURI.setPreferredSize(NewEntryPanel.getDefaultTextFieldDimension());
        start.add(startPrefix);

        JPanel rewrite = new JPanel();
        rewrite.setLayout(new FlowLayout(FlowLayout.LEADING));
        rewrite.add(new JLabel("Rewrite by: "));
        rewrite.add(rewriteByURI);

        mainComponent.add(start);
        mainComponent.add(rewrite);
    }
}
