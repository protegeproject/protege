package org.protege.editor.owl.ui.library.plugins;

import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.editor.owl.model.library.folder.ImportByNameManager;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FolderGroupPanel extends NewEntryPanel {
    private static final long serialVersionUID = 3602861945631171635L;
    private XMLCatalog catalog;
    private JTextField physicalLocationField;
    private JCheckBox recursive;
    private JCheckBox importByName;

    public FolderGroupPanel(XMLCatalog catalog) {
        setLayout(new BorderLayout());
        this.catalog = catalog;
        add(createCenterComponent(), BorderLayout.CENTER);
    }
    
    private JComponent createCenterComponent() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        JPanel physicalLocationPanel = new JPanel();
        physicalLocationPanel.setLayout(new FlowLayout());
        physicalLocationPanel.add(new JLabel("Directory: "));
        physicalLocationField = new JTextField();
        physicalLocationField.setPreferredSize(new JTextField("/home/tredmond/Shared/ontologies/simple/pizza-good.owl").getPreferredSize());
        physicalLocationField.addActionListener(e -> {
            fireListeners();
        });
        physicalLocationPanel.add(physicalLocationField);
        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> {
            File f =  UIUtil.chooseFolder(FolderGroupPanel.this, "Folder for Ontology Repository");
            if (f != null) {
                physicalLocationField.setText(f.getPath());
                fireListeners();
            }
        });
        physicalLocationPanel.add(browse);
        centerPanel.add(physicalLocationPanel);
        
        recursive = new JCheckBox("Search all sub-folders");
        recursive.setAlignmentX(LEFT_ALIGNMENT);
        centerPanel.add(recursive);
        centerPanel.add(Box.createVerticalGlue());
        importByName = new JCheckBox("Import By Name (requires manual updates)");
        importByName.setAlignmentX(LEFT_ALIGNMENT);
        centerPanel.add(importByName);
        centerPanel.add(Box.createVerticalGlue());
        return centerPanel;
    }

    public GroupEntry getEntry() {
        String text = physicalLocationField.getText();
        if (text == null || text.length() == 0) {
            return null;
        }
        File dir = new File(text);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        try {
            // There seems to be a bug in relativize and resolve so don't relativize here
        	URI folderUri = new File(physicalLocationField.getText()).toURI();
        	if (importByName.isSelected()) {
        		return ImportByNameManager.createGroupEntry(folderUri, recursive.isSelected(), true, catalog);
        	}
        	else {
        		return FolderGroupManager.createGroupEntry(folderUri, recursive.isSelected(), true, catalog);
        	}
        }
        catch (IOException ioe) {
            LoggerFactory.getLogger(FolderGroupPanel.class)
                    .error("An error occurred whilst creating a group entry: {}", ioe);
            return null;
        }
    }
}
