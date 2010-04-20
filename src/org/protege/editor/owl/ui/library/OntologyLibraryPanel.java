package org.protege.editor.owl.ui.library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.view.ViewBarComponent;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.library.OntologyGroupManager;
import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.EntryVisitor;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.DelegatePublicEntry;
import org.protege.xmlcatalog.entry.DelegateSystemEntry;
import org.protege.xmlcatalog.entry.DelegateUriEntry;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.protege.xmlcatalog.entry.NextCatalogEntry;
import org.protege.xmlcatalog.entry.PublicEntry;
import org.protege.xmlcatalog.entry.RewriteSystemEntry;
import org.protege.xmlcatalog.entry.RewriteUriEntry;
import org.protege.xmlcatalog.entry.SystemEntry;
import org.protege.xmlcatalog.entry.UriEntry;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * Panel for showing the available ontology libraries and allowing them to be edited.
 * Editing of the tree does not automatically cause the available libraries to be updated
 * <code>updateLibraryManager()</code> must be called for this to happen.
 */
public class OntologyLibraryPanel extends JPanel {
	private static final long serialVersionUID = 1831859637994718783L;

	private static final Logger logger = Logger.getLogger(OntologyLibraryPanel.class);

    private XMLCatalog catalog;
    private File catalogFile;
    
    private OWLEditorKit owlEditorKit;
    
    private FolderGroupManager folderManager;

    private JTree tree;

    private Action removeLibraryAction;

    private DefaultMutableTreeNode rootNode;
    
    public OntologyLibraryPanel(OWLEditorKit owlEditorKit, File catalogFile) throws MalformedURLException, IOException {
        this.owlEditorKit = owlEditorKit;
        this.catalogFile = catalogFile;
        folderManager = owlEditorKit.getOWLModelManager().getOntologyCatalogManager().getFolderOntologyLibraryBuilder();
        catalog = CatalogUtilities.parseDocument(catalogFile.toURI().toURL());
        createUI();
    }


    public void dispose() {

    }


    private void createUI() {
        JPanel panel = new JPanel(new BorderLayout());
        ViewBarComponent vbc = new ViewBarComponent("Ontology libraries",
                                                    PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(
                                                            ProtegeProperties.ONTOLOGY_COLOR_KEY), Color.GRAY),
                                                    panel);
        setLayout(new BorderLayout());
        add(vbc);
        rootNode = new DefaultMutableTreeNode("Ontology libraries");
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        tree = new JTree(model);
        tree.setRowHeight(-1);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new OntologyLibraryCellRenderer());

        panel.add(ComponentFactory.createScrollPane(tree));

        vbc.addAction(new AbstractAction("Add library...", OWLIcons.getIcon("ontology.library.add.png")) {
            public void actionPerformed(ActionEvent e) {
                handleAddLibrary();
            }
        });
        removeLibraryAction = new AbstractAction("Remove selected library",
                                                 OWLIcons.getIcon("ontology.library.remove.png")) {
            public void actionPerformed(ActionEvent e) {
                handleRemoveLibrary();
            }
        };
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                updateState();
            }
        });
        vbc.addAction(removeLibraryAction);
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }


            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }


            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
        });
        loadTree();
        updateState();
    }


    private void showPopupMenu(MouseEvent e) {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        Object o = node.getUserObject();
        if (o instanceof OntologyGroupManager) {
            return;
        }
        else if (o instanceof Entry) {
            Entry entry = (Entry) o;
            JPopupMenu popupMenu = new JPopupMenu();
            
            if (o instanceof UriEntry) {
                popupMenu.add(new EditUriAction(tree, selectionPath, catalogFile));
            }
            popupMenu.add(new DeleteRedirectAction(tree, selectionPath));
            
            popupMenu.show(tree, e.getX(), e.getY());
        }
    }

    private void handleAddLibrary() {
        File f = UIUtil.chooseFolder(OntologyLibraryPanel.this, "Please select a folder containing ontologies");
        if (f != null) {
            GroupEntry lib;
            try {
                lib = folderManager.createGroupEntry(f, catalog);
                folderManager.update(lib);
                catalog.addEntry(lib);
                CatalogUtilities.save(catalog, catalogFile);
            }
            catch (IOException ioe) {
                ProtegeApplication.getErrorLog().logError(ioe);
                return;
            }
            insertLibraryIntoTree(lib);
        }
    }

    private void handleRemoveLibrary() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        Entry lib = (Entry) node.getUserObject();
        removeLibraryFromTree(lib);
        if (path.getPathCount() == 2) {
            catalog.removeEntry(lib);
            model.removeNodeFromParent(node);
        }
        else if (path.getPathCount() > 2) {
        	GroupEntry parent = (GroupEntry) ((DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 2)).getUserObject();
        	parent.removeEntry(lib);
            model.removeNodeFromParent(node);
        }
    }


    private void updateState() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            removeLibraryAction.setEnabled(false);
            return;
        }
        Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        removeLibraryAction.setEnabled(o instanceof Entry);
    }


    private void loadTree() {
        for (Entry lib : catalog.getEntries()) {
            insertLibraryIntoTree(lib);
        }
    }

    private void removeLibraryFromTree(Entry lib) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        MutableTreeNode removeNode = null;
        for (int i=0; i<rootNode.getChildCount(); i++){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getChildAt(i);
            if (node.getUserObject().equals(lib)){
                removeNode = node;
                break;
            }
        }
        if (removeNode != null){
            model.removeNodeFromParent(removeNode);
        }
    }


    private void insertLibraryIntoTree(Entry entry) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode libNode = new DefaultMutableTreeNode(entry);
        model.insertNodeInto(libNode, rootNode, rootNode.getChildCount());
        if (entry instanceof GroupEntry) {
        	for (Entry subEntry : ((GroupEntry) entry).getEntries()) {
        		insertEntryIntoTree(libNode, subEntry, null);
        	}
        }
        tree.expandPath(new TreePath(libNode.getPath()));
    }
    
    private void insertEntryIntoTree(DefaultMutableTreeNode parent, Entry entry, Set<Entry> traversed) {
        if (traversed == null) {
            traversed = new HashSet<Entry>();
        }
        if (traversed.contains(entry)) {
            logger.error("Cycle found in xmlcatalog.  Entry " + entry + " recursively  repeated.");
            return;
        }
        traversed.add(entry);
        
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode entryNode = new DefaultMutableTreeNode(entry);
        model.insertNodeInto(entryNode, parent, parent.getChildCount());
        if (entry instanceof GroupEntry) {
            for (Entry groupEntry : ((GroupEntry) entry).getEntries()) {
                insertEntryIntoTree(entryNode, groupEntry, traversed);
            }
        }
        else if (entry instanceof NextCatalogEntry) {
            try {
                NextCatalogEntry subCatalog = (NextCatalogEntry) entry;
                for (Entry subCatalogEntry : subCatalog.getParsedCatalog().getEntries()){
                    insertEntryIntoTree(entryNode, subCatalogEntry, traversed);
                }
            }
            catch (IOException ioe) {
                ProtegeApplication.getErrorLog().logError(ioe);
                logger.error("Found problem with sub catalog of ontology library");
            }
        }
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }


    private class OntologyLibraryCellRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // Modify the text
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof GroupEntry && folderManager.isSuitable((GroupEntry) userObject)) {
                label.setText("<html><body><b>" + folderManager.getDescription((GroupEntry) userObject) + "</b></body></html>");
                label.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 0));
                setIcon(OWLIcons.getIcon("ontology.library.png"));
            }
            else {
                DefaultMutableTreeNode parentNode = ((DefaultMutableTreeNode) node.getParent());
                if (parentNode != null) {
                    Entry entry = (Entry) node.getUserObject();
                    EntryRendererVisitor  visitor = new EntryRendererVisitor(label);
                    entry.accept(visitor);
                    label = visitor.getLabel();
                }
                label.setBorder(BorderFactory.createEmptyBorder(2, 20, 8, 0));
            }
            return label;
        }
    }
    
    
    public class EntryRendererVisitor implements EntryVisitor {
        private JLabel label;
        
        public EntryRendererVisitor(JLabel label) {
            this.label = label;
        }
        
        public JLabel getLabel() {
            return label;
        }
        
        
        public void visit(UriEntry entry) {   
            label.setText("<html><body><b>Imported Location: " + entry.getName() + "</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" 
                          + "<font color=\"gray\">Redirected To: " + entry.getUri() + "</font></body></html>");
            label.setIcon(null);
        }
        
        
        public void visit(GroupEntry entry) {
        	if (folderManager.isSuitable(entry)) {
        		label.setText(folderManager.getDescription(entry));
        	}
        	else {
        		label.setText(entry.getId());
        	}
        }

        
        public void visit(PublicEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(SystemEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(RewriteSystemEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(DelegatePublicEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(DelegateSystemEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(RewriteUriEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(DelegateUriEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        
        public void visit(NextCatalogEntry entry) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
        
    }

    public static void showDialog(OWLEditorKit owlEditorKit) throws MalformedURLException, IOException {
        UIHelper helper = new UIHelper(owlEditorKit);
        OntologyLibraryPanel panel = new OntologyLibraryPanel(owlEditorKit, OntologyCatalogManager.getGlobalCatalogFile());
        if (helper.showDialog("Ontology libraries", panel) == JOptionPane.OK_OPTION){
        	try {
        		CatalogUtilities.save(panel.catalog, panel.catalogFile);
        		owlEditorKit.getModelManager().getOntologyCatalogManager().reloadGlobalCatalog();
        	}
        	catch (IOException e) {
        		ProtegeApplication.getErrorLog().logError(e);
        	}
        }
        panel.dispose();
    }
    
    public static void showDialog(OWLEditorKit owlEditorKit, File dir) throws MalformedURLException, IOException {
        UIHelper helper = new UIHelper(owlEditorKit);
        OntologyLibraryPanel panel = new OntologyLibraryPanel(owlEditorKit, OntologyCatalogManager.getCatalogFile(dir));
        if (helper.showDialog("Ontology libraries", panel) == JOptionPane.OK_OPTION){
        	try {
        		CatalogUtilities.save(panel.catalog, panel.catalogFile);
        		owlEditorKit.getModelManager().getOntologyCatalogManager().reloadFolder(dir);
        	}
        	catch (IOException e) {
        		ProtegeApplication.getErrorLog().logError(e);
        	}
        }
        panel.dispose();
    }
}
