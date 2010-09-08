package org.protege.editor.owl.ui.library;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.library.CatalogEntryManager;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.XmlBaseContext;
import org.protege.xmlcatalog.entry.Entry;

public class AddEntryDialog extends JDialog {
    private static final long serialVersionUID = 8162358678767968590L;
    
    private List<CatalogEntryManager> entryManagers;
    private XmlBaseContext xmlBase;
    private JTabbedPane tabs;
    private JButton ok;
    private boolean cancelled = false;

    public static Entry askUserForRepository(JComponent parent, XMLCatalog catalog, List<CatalogEntryManager> entryManagers) {
        AddEntryDialog dialog = new AddEntryDialog((JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, parent), 
                                                   entryManagers,
                                                   catalog.getXmlBaseContext());
        dialog.setVisible(true);
        Entry e = dialog.getEntry();
        if (e != null) {
            catalog.addEntry(0, e);
            for (CatalogEntryManager entryManager : entryManagers) {
                if (entryManager.isSuitable(e)) {
                    try {
                        entryManager.update(e);
                    }
                    catch (IOException ioe) {
                        ProtegeApplication.getErrorLog().logError(ioe);
                    }
                }
            }
        }
        return e;
    }
    
    
    public AddEntryDialog(JDialog parent, List<CatalogEntryManager> entryManagers, XmlBaseContext xmlBase) {
        super(parent, true);
        this.entryManagers = entryManagers;
        this.xmlBase = xmlBase;
        setLayout(new BorderLayout());
        add(createTabbedPane(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        pack();
        repaint();
    }
    
    private JTabbedPane createTabbedPane() {
    	tabs = new JTabbedPane();
        for (final CatalogEntryManager entryManager : entryManagers) {
        	NewEntryPanel panel = entryManager.newEntryPanel(xmlBase);
        	panel.setAlignmentY(CENTER_ALIGNMENT);
        	tabs.addTab(entryManager.getDescription(), panel);
        	panel.addListener(new Runnable() {
        		
        		public void run() {
        			updateOkButtonState();
        		}
        	});
        }
        tabs.addChangeListener(new ChangeListener() {
        	@Override
        	public void stateChanged(ChangeEvent e) {
        		updateOkButtonState();
        	}
        });
        return tabs;
    }

    private JComponent createButtons() {
        JPanel bottom = new JPanel();
        ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                AddEntryDialog.this.setVisible(false);
            }
        });
        ok.setAlignmentX(CENTER_ALIGNMENT);
        updateOkButtonState();
        bottom.add(ok);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                AddEntryDialog.this.setVisible(false);
                cancelled = true;
            }
        });
        cancel.setAlignmentX(CENTER_ALIGNMENT);
        bottom.add(cancel);
        return bottom;
    }
    
    public Entry getEntry() {
		NewEntryPanel panel = (NewEntryPanel) tabs.getSelectedComponent();
        return !cancelled ? panel.getEntry() : null;
    }

    
    private void updateOkButtonState() {
		NewEntryPanel panel = (NewEntryPanel) tabs.getSelectedComponent();
		ok.setEnabled(panel.getEntry() != null);
    }
    
    
}
