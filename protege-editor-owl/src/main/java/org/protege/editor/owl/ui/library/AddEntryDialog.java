package org.protege.editor.owl.ui.library;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.library.CatalogEntryManager;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.slf4j.LoggerFactory;

public class AddEntryDialog extends JDialog {
    private static final long serialVersionUID = 8162358678767968590L;
    
    private List<CatalogEntryManager> entryManagers;
    private XMLCatalog catalog;
    private JTabbedPane tabs;
    private JButton ok;
    private boolean cancelled = false;

    public static Entry askUserForRepository(JComponent parent, XMLCatalog catalog, List<CatalogEntryManager> entryManagers) {
        AddEntryDialog dialog = new AddEntryDialog((JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, parent), 
                                                   entryManagers,
                                                   catalog);
        dialog.setVisible(true);
        Entry e = dialog.getEntry();
        if (e != null) {
            catalog.addEntry(0, e);
            for (CatalogEntryManager entryManager : entryManagers) {
                if (entryManager.isSuitable(e)) {
                    try {
                        entryManager.update(e);
                    }
                    catch (IOException ex) {
                        LoggerFactory.getLogger(AddEntryDialog.class)
                                .error("An error occurred whilst adding a catalog entry: ", ex);
                    }
                }
            }
        }
        return e;
    }
    
    
    public AddEntryDialog(JDialog parent, List<CatalogEntryManager> entryManagers, XMLCatalog catalog) {
        super(parent, true);
        this.entryManagers = entryManagers;
        this.catalog = catalog;
        setLayout(new BorderLayout());
        add(createTabbedPane(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        pack();
        repaint();
    }
    
    private JTabbedPane createTabbedPane() {
    	tabs = new JTabbedPane();
        for (final CatalogEntryManager entryManager : entryManagers) {
        	NewEntryPanel panel = entryManager.newEntryPanel(catalog);
        	if (panel != null) {
        		panel.setAlignmentY(CENTER_ALIGNMENT);
        		tabs.addTab(entryManager.getDescription(), panel);
        		panel.addListener(new Runnable() {

        			public void run() {
        				updateOkButtonState();
        			}
        		});
        	}
        }
        tabs.addChangeListener(new ChangeListener() {
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
