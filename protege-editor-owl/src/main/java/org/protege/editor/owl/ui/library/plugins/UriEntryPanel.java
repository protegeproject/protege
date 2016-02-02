package org.protege.editor.owl.ui.library.plugins;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

public class UriEntryPanel extends NewEntryPanel {

    private static final long serialVersionUID = -6222499916124012217L;

    private final Logger logger = LoggerFactory.getLogger(UriEntryPanel.class);

    private XMLCatalog catalog;

    private JTextField physicalLocationField;

    private JComboBox<IRI> importDeclarationComboBox;

    public UriEntryPanel(XMLCatalog catalog) {
        setLayout(new BorderLayout());
        this.catalog = catalog;
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel physicalLocationPanel = new JPanel();
        physicalLocationPanel.setLayout(new FlowLayout());
        physicalLocationPanel.add(new JLabel("Physical Location: "));
        physicalLocationField = new JTextField();
        physicalLocationField.setPreferredSize(new JTextField("/home/tredmond/Shared/ontologies/simple/pizza-good.owl").getPreferredSize());
        physicalLocationPanel.add(physicalLocationField);
        JButton browse = new JButton("Browse");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = UIUtil.openFile(UriEntryPanel.this, "Choose OWL source", "OWL File", UIHelper.OWL_EXTENSIONS);
                if (f != null) {
                    physicalLocationField.setText(f.getPath());
                    regenerateImportSuggestions();
                }
            }
        });
        physicalLocationPanel.add(browse);
        panel.add(physicalLocationPanel);

        JButton generate = new JButton("Generate Import Suggestions");
        generate.setAlignmentX(CENTER_ALIGNMENT);
        generate.addActionListener(e -> {
            regenerateImportSuggestions();
        });
        panel.add(generate);

        JPanel importDeclarationPanel = new JPanel();
        importDeclarationPanel.setLayout(new FlowLayout());
        importDeclarationPanel.add(new JLabel("Import Declaration: "));
        importDeclarationComboBox = new JComboBox<>();
        importDeclarationComboBox.setEditable(true);
        importDeclarationComboBox.addActionListener(e -> {
            fireListeners();
        });
        importDeclarationPanel.add(importDeclarationComboBox);
        panel.add(importDeclarationPanel);

        return panel;
    }

    private void regenerateImportSuggestions() {
        importDeclarationComboBox.removeAllItems();
        IRI preferred = null;
        URI u = getPhysicalLocation();
        if (u != null) {
            Set<IRI> locations = new TreeSet<IRI>();
            if (!"file".equals(u.getScheme())) {
                preferred = IRI.create(u);
                locations.add(preferred);
            }
            MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
            Optional<OWLOntologyID> id = extractor.getOntologyId(u);
            if (id.isPresent()) {
                if (!id.get().isAnonymous()) {
                    preferred = id.get().getOntologyIRI().get();
                    locations.add(preferred);
                    if (id.get().getVersionIRI().isPresent()) {
                        preferred = id.get().getVersionIRI().get();
                        locations.add(preferred);
                    }
                }
            }
            for (IRI location : locations) {
                importDeclarationComboBox.addItem(location);
            }
            if (preferred != null) {
                importDeclarationComboBox.setSelectedItem(preferred);
            }
            fireListeners();
            repaint();
        }
    }

    private URI getPhysicalLocation() {
        String text = physicalLocationField.getText();
        if (text == null) {
            return null;
        }
        try {
            if (new File(text).exists()) {
                return new File(text).toURI();
            }
            else {
                return new URI(text);
            }
        } catch (URISyntaxException ex) {
            logger.error("Could not parse URL: {}.", text, ex);
        }
        return null;
    }

    @Override
    public UriEntry getEntry() {
        URI physicalLocation = getPhysicalLocation();
        if (physicalLocation == null || importDeclarationComboBox.getSelectedItem() == null) {
            return null;
        }
        String importDeclarationString = null;
        Object importDeclarationObject = importDeclarationComboBox.getSelectedItem();
        if (importDeclarationObject instanceof String) {
            importDeclarationString = (String) importDeclarationObject;
        }
        else if (importDeclarationObject instanceof IRI) {
            importDeclarationString = importDeclarationObject.toString();
        }
        if (importDeclarationString == null) {
            return null;
        }
        physicalLocation = CatalogUtilities.relativize(physicalLocation, catalog);
        return new UriEntry("User Edited Redirect",
                catalog,
                importDeclarationString,
                physicalLocation,
                null);
    }
}
