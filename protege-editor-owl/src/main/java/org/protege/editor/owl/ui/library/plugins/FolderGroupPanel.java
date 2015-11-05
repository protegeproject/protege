package org.protege.editor.owl.ui.library.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.editor.owl.model.library.folder.ImportByNameManager;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.slf4j.LoggerFactory;

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
        physicalLocationField.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                fireListeners();
            }
        });
        physicalLocationPanel.add(physicalLocationField);
        JButton browse = new JButton("Browse");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {       
                File f =  UIUtil.chooseFolder(FolderGroupPanel.this, "Folder for Ontology Repository");
                if (f != null) {
                    physicalLocationField.setText(f.getPath());
                    fireListeners();
                }
            }
        });
        physicalLocationPanel.add(browse);
        centerPanel.add(physicalLocationPanel);
        
        recursive = new JCheckBox("Recursively search subdirectories");
        recursive.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(recursive);
        centerPanel.add(Box.createVerticalGlue());
        importByName = new JCheckBox("Import By Name (requires manual updates)");
        importByName.setAlignmentX(CENTER_ALIGNMENT);
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
        	URI folderUri = CatalogUtilities.relativize(new File(physicalLocationField.getText()).toURI(), catalog);
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
