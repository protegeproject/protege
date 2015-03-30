package org.protege.editor.owl.ui.library;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.OWLOntologyID;



public class EditUriAction extends AbstractAction {
    private static final Logger logger = Logger.getLogger(EditUriAction.class);
    
    public static String UNKNOWN     = "Unknown";
    public static String CALCULATING = "Calculating...";
    public static String NO_PARSE    = "File didn't parse.";
    public static String NO_VERSION  = "No version present";
    
    private JTree parent;
    private TreePath selectionPath;
    private File catalogFile;
    
    public EditUriAction(JTree parent, TreePath selectionPath, File catalogFile) {
        super("Edit Library Entry");
        this.parent = parent;
        this.selectionPath = selectionPath;
        this.catalogFile = catalogFile;
    }

    public void actionPerformed(ActionEvent e) {
        XMLCatalog catalog = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        Object container = ((DefaultMutableTreeNode) selectionPath.getPathComponent(selectionPath.getPathCount()-2)).getUserObject();

        UriEntry entry = (UriEntry) node.getUserObject();
        EditPanel panel = new EditPanel(entry);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(parent, "Edit URI Redirect");
        dialog.setVisible(true);
        Object value = pane.getValue();
        if (value != null && value.equals(new Integer(JOptionPane.OK_OPTION))) {
            UriEntry editted = panel.getUriEntry();
            if (container instanceof XMLCatalog) {
            	XMLCatalog lib = (XMLCatalog) container;
                lib.replaceEntry(entry, editted);
                node.setUserObject(editted);
                try {
                    CatalogUtilities.save(lib, catalogFile);
                }
                catch (IOException ioe) {
                    ProtegeApplication.getErrorLog().logError(ioe);
                }
            }
        }
    }
    
    private class EditPanel extends JPanel {
        private UriEntry original;
        private JTextField importedUri;
        private JTextField physicalLocation;
        
        public EditPanel(UriEntry entry) {
            original = entry;
            setLayout(new GridLayout(0,2));
            
            add(new JLabel("Imported Declaration: "));
            importedUri = new JTextField(entry.getName());
            add(importedUri);
            
            add(new JLabel());
            JButton updateImportedDeclaration = new JButton("Update Import Declaration Using Ontology Name");
            add(updateImportedDeclaration);
            updateImportedDeclaration.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    JDialog panel = new GetOntologyNamePanel(EditPanel.this, importedUri, URI.create(physicalLocation.getText()));
                    panel.pack();
                    panel.setVisible(true);
                }
            });

            add(new JLabel("Physical Location: "));
            physicalLocation = new JTextField(entry.getAbsoluteURI().toString());
            add(physicalLocation);
            
            add(new JLabel());
            JButton fileButton = new JButton("Browse");
            fileButton.addActionListener(new ChooseFileAction(EditPanel.this, original, physicalLocation));
            add(fileButton);
        }
        

        
        public UriEntry getUriEntry() {
            URI base = CatalogUtilities.resolveXmlBase(original);
            URI physicalUri = URI.create(physicalLocation.getText());
            physicalUri = base.relativize(physicalUri);
            return new UriEntry(original.getId(), original.getXmlBaseContext(), 
                                importedUri.getText(), physicalUri, 
                                original.getXmlBase());
        }
    }
    
    private class ChooseFileAction extends AbstractAction {
        private JComponent parent;
        private UriEntry original;
        private JTextField physicalLocation;
        
        public ChooseFileAction(JComponent parent, UriEntry original, JTextField physicalLocation) {
            this.parent = parent;
            this.original = original;
            this.physicalLocation = physicalLocation;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            URI base = CatalogUtilities.resolveXmlBase(original);
            File directory;
            if (base != null && (directory = new File(base)).isDirectory()) {
                fileChooser.setCurrentDirectory(directory);
            }
            int retValue = fileChooser.showOpenDialog(parent);
            if (retValue == JFileChooser.APPROVE_OPTION) {
                physicalLocation.setText(fileChooser.getSelectedFile().toURI().toString());
            }
        }
    }
    
    private class GetOntologyNamePanel extends JDialog {        
        private JTextField ontologyNameField;
        private JTextField ontologyVersionField;
        private JButton useOntologyName;
        private JButton useOntologyVersion;
        
        public GetOntologyNamePanel(JComponent parent, final JTextField importedUri, URI physicalLocation) {
            super(JOptionPane.getFrameForComponent(parent), "Update Import Declaration Using Ontology Name");
            
            getContentPane().setLayout(new BorderLayout());
            JPanel centerPanel = new JPanel(new GridLayout(0,2));

            centerPanel.add(new JLabel("Ontology Name:"));
            centerPanel.add(ontologyNameField = new JTextField());

            centerPanel.add(new JLabel("Ontology Version: "));
            centerPanel.add(ontologyVersionField = new JTextField());

            getContentPane().add(centerPanel, BorderLayout.CENTER);

            JPanel southPanel = new JPanel(new FlowLayout());
            southPanel.add(useOntologyName = new JButton("Use Ontology Name"));
            useOntologyName.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    importedUri.setText(ontologyNameField.getText());
                    setVisible(false);
                }
            });
            southPanel.add(useOntologyVersion = new JButton("Use Ontology Version"));
            useOntologyVersion.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    importedUri.setText(ontologyVersionField.getText());
                    setVisible(false);
                }
            });
            
            JButton cancel = new JButton("Cancel");
            southPanel.add(cancel);
            cancel.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            
            getContentPane().add(southPanel, BorderLayout.SOUTH);

            updateOntologyName(physicalLocation);
        }

        
        private void updateOntologyName(URI physicalLocation) {
            try {
                ontologyNameField.setText(CALCULATING); // if it becomes multi-threaded
                ontologyVersionField.setText(CALCULATING);
                MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
                extractor.setPhysicalAddress(physicalLocation);
                OWLOntologyID id = extractor.getOntologyId();
                ontologyNameField.setText(id.getOntologyIRI().get().toString());
                if (id.getVersionIRI().isPresent()) {
                    ontologyVersionField.setText(id.getVersionIRI().get().toString());
                }
                else {
                    ontologyVersionField.setText(NO_VERSION);
                }
            }
            catch (Throwable t) {
                ontologyNameField.setText(NO_PARSE);
                ontologyVersionField.setText(NO_PARSE);
            }
            updateUseOntologyNameButtonsEnabled();
        }
        
        private void updateUseOntologyNameButtonsEnabled() {
            useOntologyName.setEnabled(isValidOntologyNameOrVersion(ontologyNameField.getText()));
            useOntologyVersion.setEnabled(isValidOntologyNameOrVersion(ontologyVersionField.getText()));
        }
        
        private boolean isValidOntologyNameOrVersion(String name) {
            return !(name == null 
                     || name.equals(UNKNOWN) 
                     || name.equals(CALCULATING)
                     || name.equals(NO_PARSE)
                     || name.equals(NO_VERSION));
        }

    }
}

