package org.protege.editor.owl.ui.library;

import org.protege.editor.owl.ui.library.plugins.NextCatalogPanel;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.NextCatalogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by vblagodarov on 09-08-17.
 */
public class NextCatalogEntryEditorPanel implements ILibraryEntryEditor<NextCatalogEntry> {

    private JPanel mainComponent;
    private XmlBaseContext baseContext;
    private JTextField physicalLocation;
    private final Logger logger = LoggerFactory.getLogger(NextCatalogEntryEditorPanel.class);

    public NextCatalogEntryEditorPanel(XmlBaseContext context) {
        baseContext = context;
        initMainComponent("");
    }

    public NextCatalogEntryEditorPanel(NextCatalogEntry entry) {
        baseContext = entry.getXmlBaseContext();
        if (entry != null && entry.getCatalog() != null) {
            initMainComponent(entry.getCatalog().toString());
        } else {
            initMainComponent("");
        }
    }

    public NextCatalogEntryEditorPanel(XmlBaseContext context, DocumentListener changeLocationListener) {
        this(context);
        addDocumentListener(changeLocationListener);
    }

    private void initMainComponent(String defaultValue) {
        mainComponent = new JPanel(new FlowLayout(FlowLayout.LEADING));
        physicalLocation = new JTextField();
        physicalLocation.setText(defaultValue);

        physicalLocation.setPreferredSize(NextCatalogPanel.getDefaultTextFieldDimension());
        mainComponent.add(physicalLocation);

        JButton fileButton = new JButton("Browse");
        fileButton.addActionListener(new ChooseFileAction(mainComponent, baseContext, physicalLocation));
        mainComponent.add(fileButton);
    }

    public void addDocumentListener(DocumentListener documentListener) {
        physicalLocation.getDocument().addDocumentListener(documentListener);
    }

    public Component getComponent() {
        return mainComponent;
    }

    public NextCatalogEntry getEntry(String id, URI baseUri) {
        if (!isInputValid()) {
            return null;
        }
        URI physicalLocationUri = getPhysicalLocation();
        URI base = CatalogUtilities.resolveXmlBase(baseContext);
        return new NextCatalogEntry(id,
                baseContext,
                base == null ? physicalLocationUri : base.relativize(physicalLocationUri),
                baseUri);
    }

    public NextCatalogEntry getEntry(NextCatalogEntry oldEntry) {
        return isInputValid() ? getEntry(oldEntry.getId(), oldEntry.getXmlBase()) : null;
    }

    @Override
    public boolean isInputValid() {
        return getPhysicalLocation() != null;
    }

    private URI getPhysicalLocation() {
        String text = physicalLocation.getText();
        if (text == null) {
            return null;
        }
        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            if (new File(text).exists()) {
                return new File(text).toURI();
            } else {
                return new URI(text);
            }
        } catch (URISyntaxException ex) {
            logger.error("Could not parse URL: {}.", text, ex);
        }
        return null;
    }
}
