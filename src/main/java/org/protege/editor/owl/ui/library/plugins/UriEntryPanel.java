package org.protege.editor.owl.ui.library.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.library.NewEntryPanel;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class UriEntryPanel extends NewEntryPanel {
	private static final long serialVersionUID = -6222499916124012217L;
	
	public static Logger LOGGER = Logger.getLogger(UriEntryPanel.class);
    private XMLCatalog catalog;

	private JTextField physicalLocationField;
	private JComboBox importDeclarationComboBox;
	
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
				File f =  UIUtil.openFile(UriEntryPanel.this, "Choose OWL source", "OWL File",UIHelper.OWL_EXTENSIONS);
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
		generate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    regenerateImportSuggestions();
			}
		});
		panel.add(generate);
		
		JPanel importDeclarationPanel = new JPanel();
		importDeclarationPanel.setLayout(new FlowLayout());
		importDeclarationPanel.add(new JLabel("Import Declaration: "));
		importDeclarationComboBox = new JComboBox();
		importDeclarationComboBox.setEditable(true);
		importDeclarationComboBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        fireListeners();
		    }
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
            extractor.setPhysicalAddress(u);
            OWLOntologyID id = extractor.getOntologyId();
            if (!id.isAnonymous()) {
                preferred  = id.getOntologyIRI();
                locations.add(preferred);
                if (id.getVersionIRI() != null) {
                    preferred  = id.getVersionIRI();
                    locations.add(preferred);
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
		}
		catch (URISyntaxException murle) {
			LOGGER.warn("Could not parse url " + text);
		}
		return null;
	}
	
	@Override
	public UriEntry getEntry() {
	    URI  physicalLocation = getPhysicalLocation();
	    if (physicalLocation == null || importDeclarationComboBox.getSelectedItem() == null) {
	        return null;
	    }
	    String importDeclarationString = null;
	    Object importDeclarationObject  = importDeclarationComboBox.getSelectedItem();
	    if (importDeclarationObject instanceof String) {
	    	importDeclarationString = (String) importDeclarationObject;
	    }
	    else if (importDeclarationObject instanceof IRI) {
	    	importDeclarationString = ((IRI) importDeclarationObject).toString();
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
