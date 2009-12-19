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
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryManager;
import org.protege.editor.owl.model.library.folder.FolderOntologyLibrary;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
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

    private static final Logger logger = Logger.getLogger(OntologyLibraryPanel.class);


    private OWLEditorKit owlEditorKit;

    private JTree tree;

    private Action removeLibraryAction;

    private DefaultMutableTreeNode rootNode;

    private Set<OntologyLibrary> addedLibraries = new HashSet<OntologyLibrary>();

    private Set<OntologyLibrary> removedLibraries = new HashSet<OntologyLibrary>();

    public OntologyLibraryPanel(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
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
        if (node.getUserObject() instanceof OntologyLibrary) {
            return;
        }
        else if (node.getUserObject() instanceof Entry) {
            Entry entry = (Entry) node.getUserObject();
            JPopupMenu popupMenu = new JPopupMenu();
            
            popupMenu.add(new DeleteRedirectAction(selectionPath));
            
            popupMenu.show(tree, e.getX(), e.getY());
        }
    }

    private class DeleteRedirectAction extends AbstractAction {
        private TreePath selectionPath;
        
        public DeleteRedirectAction(TreePath selectionPath) {
            super("Delete Library Entry");
            this.selectionPath = selectionPath;
        }
        
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode nodeToDelete = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            boolean deleteFromTree = false;
            Entry toDelete = (Entry) nodeToDelete.getUserObject();
            Object o = ((DefaultMutableTreeNode) selectionPath.getParentPath().getLastPathComponent()).getUserObject();
            
            if (o instanceof OntologyLibrary) {
                OntologyLibrary lib = (OntologyLibrary) o;
                lib.getXmlCatalog().removeEntry(toDelete);
                deleteFromTree = true;
            }
            else if (o instanceof GroupEntry) {
                GroupEntry group = (GroupEntry) o;
                group.removeEntry(toDelete);
                deleteFromTree = true;
            }
            if (deleteFromTree)  {
                ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(nodeToDelete);
                tree.repaint();
            }
        }
    }
    
    
    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
    }


    private void handleAddLibrary() {
        File f = UIUtil.chooseFolder(OntologyLibraryPanel.this, "Please select a folder containing ontologies");
        if (f != null) {
            OntologyLibrary lib;
            try {
                lib = new FolderOntologyLibrary(f);
            }
            catch (IOException ioe) {
                ProtegeApplication.getErrorLog().logError(ioe);
                return;
            }
            if (!removedLibraries.remove(lib)){ // just in case of re-insertion
                addedLibraries.add(lib);
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
        Object o = node.getUserObject();
        if (o instanceof OntologyLibrary) {
            OntologyLibrary lib = (OntologyLibrary)o;
            if (!addedLibraries.remove(lib)){
                removedLibraries.add(lib);
            }
            removeLibraryFromTree(lib);
        }
    }


    private void updateState() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            removeLibraryAction.setEnabled(false);
            return;
        }
        Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        removeLibraryAction.setEnabled(o instanceof OntologyLibrary);
    }


    private void loadTree() {
        OntologyLibraryManager ontologyLibrary = getOWLModelManager().getOntologyLibraryManager();
        for (OntologyLibrary lib : ontologyLibrary.getLibraries()) {
            insertLibraryIntoTree(lib);
        }
    }

    private void removeLibraryFromTree(OntologyLibrary lib) {
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


    private void insertLibraryIntoTree(OntologyLibrary lib) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        XMLCatalog catalog = lib.getXmlCatalog();
        DefaultMutableTreeNode libNode = new DefaultMutableTreeNode(lib);
        model.insertNodeInto(libNode, rootNode, rootNode.getChildCount());
        for (Entry entry : catalog.getEntries()) {
            insertEntryIntoTree(libNode, entry, null);
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
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }


    public Set<OntologyLibrary> getAddedLibraries() {
        return addedLibraries;
    }


    public Set<OntologyLibrary> getRemovedLibraries(){
        return removedLibraries;
    }


    private class OntologyLibraryCellRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // Modify the text
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof OntologyLibrary) {
                label.setText("<html><body><b>" + ((OntologyLibrary) userObject).getClassExpression() + "</b></body></html>");
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
            throw new UnsupportedOperationException("Not implemented yet");
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

    public static void showDialog(OWLEditorKit owlEditorKit) {
        UIHelper helper = new UIHelper(owlEditorKit);
        OntologyLibraryPanel panel = new OntologyLibraryPanel(owlEditorKit);
        if (helper.showDialog("Ontology libraries", panel) == JOptionPane.OK_OPTION){
            panel.updateLibraryManager();
        }
        panel.dispose();
    }


    /**
     * Updates the library manager and saves its state
     */
    public void updateLibraryManager() {
        final OntologyLibraryManager libManager = owlEditorKit.getModelManager().getOntologyLibraryManager();
        for (OntologyLibrary lib : getAddedLibraries()){
            libManager.addLibrary(lib);
        }
        for (OntologyLibrary lib : getRemovedLibraries()){
            libManager.removeLibraray(lib);
        }
    }
}
