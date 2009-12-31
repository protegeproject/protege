package org.protege.editor.owl.ui.library;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.library.OntologyLibrary;
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
    
    public EditUriAction(JTree parent, TreePath selectionPath) {
        super("Edit Library Entry");
        this.parent = parent;
        this.selectionPath = selectionPath;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XMLCatalog catalog = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        Object container = ((DefaultMutableTreeNode) selectionPath.getPathComponent(selectionPath.getPathCount()-2)).getUserObject();

        UriEntry entry = (UriEntry) node.getUserObject();
        EditPanel panel = new EditPanel(entry);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog("Edit URI Redirect");
        dialog.setVisible(true);
        Object value = pane.getValue();
        if (value != null && value.equals(new Integer(JOptionPane.OK_OPTION))) {
            UriEntry editted = panel.getUriEntry();
            if (container instanceof OntologyLibrary) {
                OntologyLibrary lib = (OntologyLibrary) container;
                catalog = ((OntologyLibrary) container).getXmlCatalog();
                catalog.replaceEntry(entry, editted);
                node.setUserObject(editted);
                try {
                    lib.save();
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
        
        private JTextField ontologyNameField;
        private JTextField ontologyVersionField;
        private JButton useOntologyName;
        private JButton useOntologyVersion;
        
        public EditPanel(UriEntry entry) {
            original = entry;
            setLayout(new BoxLayout(EditPanel.this, BoxLayout.Y_AXIS));
            
            JPanel panel1 = new JPanel(new GridLayout(0,2));
            panel1.add(new JLabel("Imported Declaration: "));
            importedUri = new JTextField(entry.getName());
            panel1.add(importedUri);

            panel1.add(new JLabel("Physical Location: "));
            physicalLocation = new JTextField(entry.getAbsoluteURI().toString());
            panel1.add(physicalLocation);
            physicalLocation.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    invalidateOntologyName();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e) {
                    invalidateOntologyName();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e) {
                    invalidateOntologyName();
                }
            });
            
            panel1.add(new JLabel());
            JButton fileButton = new JButton("Browse");
            fileButton.addActionListener(new ChooseFileAction(EditPanel.this, original, physicalLocation));
            panel1.add(fileButton);
            
            add(panel1);
            
            add(new JSeparator());
            
            JPanel panel2 = new JPanel(new GridLayout(0,2));
            
            JButton recalculateName = new JButton("Calculate Ontology Name");
            recalculateName.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateOntologyName();
                }
            });
            panel2.add(recalculateName);
            panel2.add(new JLabel());
            
            panel2.add(new JLabel("Ontology Name:"));
            panel2.add(ontologyNameField = new JTextField());
            
            panel2.add(new JLabel("Ontology Version: "));
            panel2.add(ontologyVersionField = new JTextField());
            
            add(panel2);

            add(useOntologyName = new JButton("Use Ontology Name for import declaration"));
            useOntologyName.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    importedUri.setText(ontologyNameField.getText());
                }
            });
            add(useOntologyVersion = new JButton("Use Ontology Version for import declaration"));
            useOntologyVersion.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    importedUri.setText(ontologyVersionField.getText());
                }
            });
        }
        
        private void invalidateOntologyName() {
            ontologyNameField.setText(UNKNOWN);
            ontologyVersionField.setText(UNKNOWN);
            useOntologyName.setEnabled(false);
            useOntologyVersion.setEnabled(false);
        }

        
        private void updateOntologyName() {
            try {
                ontologyNameField.setText(CALCULATING); // if it becomes multi-threaded
                ontologyVersionField.setText(CALCULATING);
                MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
                extractor.setPhysicalAddress(URI.create(physicalLocation.getText()));
                OWLOntologyID id = extractor.getOntologyId();
                ontologyNameField.setText(id.getOntologyIRI().toString());
                if (id.getVersionIRI() != null) {
                    ontologyVersionField.setText(id.getVersionIRI().toString());
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

        @Override
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
}

