package org.protege.editor.owl.ui.library.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class UriEntryPanel extends JDialog {
	private static final long serialVersionUID = -6222499916124012217L;
	
	public static Logger LOGGER = Logger.getLogger(UriEntryPanel.class);
	private Frame parent;
	private JTextField physicalLocationField;
	private JComboBox importDeclarationComboBox;
	
	public UriEntryPanel(Frame parent) {
		super(parent);
		this.parent = parent;
		
		setLayout(new BorderLayout());
		add(createCenterPanel(), BorderLayout.CENTER);
		add(createButtons(), BorderLayout.SOUTH);
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
				File f =  UIUtil.openFile(parent, "Choose OWL source", "OWL File",UIHelper.OWL_EXTENSIONS);
				if (f != null) {
					physicalLocationField.setText(f.getPath());
				}
			}
		});
		physicalLocationPanel.add(browse);
		panel.add(physicalLocationPanel);
		
		JButton generate = new JButton("Generate Import Suggestions");
		generate.setAlignmentX(CENTER_ALIGNMENT);
		generate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
					pack();
					repaint();
				}
			}
		});
		panel.add(generate);
		
		JPanel importDeclarationPanel = new JPanel();
		importDeclarationPanel.setLayout(new FlowLayout());
		importDeclarationPanel.add(new JLabel("Import Declaration: "));
		importDeclarationComboBox = new JComboBox();
		importDeclarationComboBox.setEditable(true);
		importDeclarationPanel.add(importDeclarationComboBox);
		panel.add(importDeclarationPanel);
		
		return panel;
	}
	
	private JPanel createButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton ok = new JButton("Ok");
		ok.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(cancel);
		return panel;
	}
	
	public URI getPhysicalLocation() {
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
	
	public static void main(String[] args) {
		UriEntryPanel fgp = new UriEntryPanel(null);
		fgp.pack();
		fgp.repaint();
		fgp.setVisible(true);
	}

}
