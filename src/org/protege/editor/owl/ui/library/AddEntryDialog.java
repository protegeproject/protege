package org.protege.editor.owl.ui.library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
    private JPanel cardedPanel;
    private CardLayout cards;
    private Map<String, NewEntryPanel> panels;
    private String currentId;
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
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        pack();
        repaint();
    }
    
    private JComponent createCenterPanel() {
        JPanel centre = new JPanel();
        centre.setLayout(new FlowLayout());
        centre.add(createRadioButtons());
        centre.add(new JSeparator(SwingConstants.VERTICAL));
        centre.add(createCardedPanel());
        return centre;
    }
    
    private JComponent createRadioButtons() {
        JPanel radioButtons = new JPanel();
        radioButtons.setLayout(new BoxLayout(radioButtons, BoxLayout.Y_AXIS));
        ButtonGroup group = new ButtonGroup();
        for (CatalogEntryManager entryManager : entryManagers) {
            final JCheckBox b = new JCheckBox(entryManager.getDescription());
            group.add(b);
            radioButtons.add(b);
            final String id = entryManager.getId();
            b.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    if (b.isSelected()) {
                        currentId = id;
                        cards.show(cardedPanel, id);
                        ok.setSelected(panels.get(currentId).getEntry() != null);
                    }
                }
            });
            if (entryManager instanceof FolderGroupManager) {
                b.setSelected(true);
            }
        }
        radioButtons.setBorder(BorderFactory.createEtchedBorder());
        return radioButtons;
    }
    
    private JComponent createCardedPanel() {
        cardedPanel = new JPanel();
        cards = new CardLayout();
        cardedPanel.setLayout(cards);
        panels = new HashMap<String, NewEntryPanel>();
        for (final CatalogEntryManager entryManager : entryManagers) {
            String id = entryManager.getId();
            final NewEntryPanel panel = entryManager.newEntryPanel(xmlBase);
            panel.setAlignmentY(CENTER_ALIGNMENT);
            panel.addListener(new Runnable() {
                public void run() {
                    if (entryManager.getId().equals(currentId)) {
                        ok.setEnabled(panel.getEntry() != null);
                    }
                }
            });
            panels.put(id, panel);
            cardedPanel.add(panel, id);
            if (entryManager instanceof FolderGroupManager) {
                currentId = id;
            }
        }
        cards.layoutContainer(cardedPanel);
        cards.show(cardedPanel, currentId);
        return cardedPanel;
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
        ok.setEnabled(panels.get(currentId).getEntry() != null);
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
        return !cancelled ? panels.get(currentId).getEntry() : null;
    }

}
