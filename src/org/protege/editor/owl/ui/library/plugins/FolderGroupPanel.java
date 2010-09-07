package org.protege.editor.owl.ui.library.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.GroupEntry;

public class FolderGroupPanel extends NewEntryPanel {
    private static final long serialVersionUID = 3602861945631171635L;
    private XmlBaseContext xmlBase;
    private JTextField physicalLocationField;
    private JCheckBox recursive;

    public FolderGroupPanel(XmlBaseContext xmlBase) {
        setLayout(new BorderLayout());
        this.xmlBase = xmlBase;
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
            return FolderGroupManager.createGroupEntry(new File(physicalLocationField.getText()), recursive.isSelected(), xmlBase);
        }
        catch (IOException ioe) {
            ProtegeApplication.getErrorLog().logError(ioe);
            return null;
        }
    }
}
