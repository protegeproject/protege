package org.protege.editor.owl.ui.library;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.view.ViewBarComponent;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.library.CatalogEntryManager;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.GroupEntry;
import org.protege.xmlcatalog.entry.UriEntry;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 *
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * Panel for showing the available ontology libraries and allowing them to be edited.
 * Editing of the tree does not automatically cause the available libraries to be updated
 * <code>updateLibraryManager()</code> must be called for this to happen.
 */
public class OntologyLibraryPanel extends JPanel {

    private final OntologyLibraryCellRenderer cellRenderer = new OntologyLibraryCellRenderer();

    private XMLCatalog catalog;

    private File catalogFile;

    private JTree tree;

    private Action addLibraryAction;

    private Action removeLibraryAction;

    private Action moveLibraryUpAction;

    private Action moveLibraryDownAction;

    private DefaultMutableTreeNode rootNode;

    private List<CatalogEntryManager> entryManagers;

    private ViewBarComponent vbc;

    public OntologyLibraryPanel(List<CatalogEntryManager> entryManagers,
                                File catalogFile) throws IOException {
        this.entryManagers = entryManagers;
        this.catalogFile = catalogFile;
        catalog = CatalogUtilities.parseDocument(catalogFile.toURI().toURL());
        createUI(catalogFile);
    }

    public static void showDialog(OWLEditorKit owlEditorKit,
                                  File catalogFile) throws IOException {
        OntologyCatalogManager catalogManager = owlEditorKit.getOWLModelManager().getOntologyCatalogManager();
        UIHelper helper = new UIHelper(owlEditorKit);
        OntologyLibraryPanel panel = new OntologyLibraryPanel(catalogManager.getCatalogEntryManagers(), catalogFile);
        if(helper.showDialog("Catalog Entries", panel) == JOptionPane.OK_OPTION) {
            try {
                CatalogUtilities.save(panel.catalog, panel.catalogFile);
                owlEditorKit.getModelManager().getOntologyCatalogManager().reloadFolder(catalogFile.getParentFile());
            } catch(IOException e) {
                LoggerFactory
                        .getLogger(OntologyLibraryPanel.class)
                        .error("An error occurred whilst saving the catalog file: {}", e);
            }
        }
        panel.dispose();
    }

    public void dispose() {

    }

    private void createUI(File catalogFile) {
        JPanel panel = new JPanel(new BorderLayout());
        vbc = new ViewBarComponent("Catalog Entries", PropertyUtil.getColor(ProtegeProperties
                                                                                    .getInstance()
                                                                                    .getProperty(ProtegeProperties.ONTOLOGY_COLOR_KEY), Color.GRAY), panel);
        vbc.getViewBar().getViewBanner().setText(catalogFile.getAbsolutePath());
        setLayout(new BorderLayout());
        add(vbc);
        rootNode = new DefaultMutableTreeNode("Entries");
        rootNode.setUserObject(catalogFile);
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        tree = new JTree(model);
        tree.setRowHeight(-1);
        tree.setRootVisible(true);
        tree.setCellRenderer(cellRenderer);

        panel.add(ComponentFactory.createScrollPane(tree));

        addLibraryAction = new AbstractAction("Add catalog entry...", OWLIcons.getIcon("ontology.library.add.png")) {

            public void actionPerformed(ActionEvent e) {
                handleAddLibrary();
            }
        };
        vbc.addAction(addLibraryAction);

        removeLibraryAction = new AbstractAction("Remove selected catalog entry", OWLIcons.getIcon("ontology.library.remove.png")) {

            public void actionPerformed(ActionEvent e) {
                handleRemoveEntry();
            }
        };
        vbc.addAction(removeLibraryAction);

        moveLibraryDownAction = new AbstractAction("Move catalog entry down", Icons.getIcon("facet.move_down.gif")) {

            public void actionPerformed(ActionEvent e) {
                handleMoveEntryDown();
            }
        };
        vbc.addAction(moveLibraryDownAction);

        moveLibraryUpAction = new AbstractAction("Move catalog entry up", Icons.getIcon("facet.move_up.gif")) {

            public void actionPerformed(ActionEvent e) {
                handleMoveEntryUp();
            }
        };
        vbc.addAction(moveLibraryUpAction);

        tree.getSelectionModel().addTreeSelectionListener(e -> {
            updateState();
        });

        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
        });
        reloadTree();
        updateState();
    }

    private void showPopupMenu(MouseEvent e) {
        TreePath selectionPath = tree.getSelectionPath();
        if(selectionPath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        Object o = node.getUserObject();
        if(o instanceof CatalogEntryManager) {
            return;
        }
        else if(o instanceof Entry) {
            Entry entry = (Entry) o;
            JPopupMenu popupMenu = new JPopupMenu();

            if(o instanceof UriEntry) {
                popupMenu.add(new EditUriAction(tree, selectionPath, catalogFile));
            }
            popupMenu.add(new DeleteRedirectAction(tree, selectionPath));

            popupMenu.show(tree, e.getX(), e.getY());
        }
    }

    private void handleAddLibrary() {
        if(AddEntryDialog.askUserForRepository(this, catalog, entryManagers) != null) {
            reloadTree();
        }
    }

    private void handleRemoveEntry() {
        TreePath path = tree.getSelectionPath();
        if(path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if(!(node.getUserObject() instanceof Entry)) {
            return;
        }
        Entry lib = (Entry) node.getUserObject();
        catalog.removeEntry(lib);
        removeLibraryFromTree(lib);
    }

    private void handleMoveEntryUp() {
        TreePath path = tree.getSelectionPath();
        if(path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if(!(node.getUserObject() instanceof Entry)) {
            return;
        }
        Entry lib = (Entry) node.getUserObject();
        int i = catalog.getEntries().indexOf(lib);
        if(i == 0) {
            return;
        }
        catalog.removeEntry(lib);
        catalog.addEntry(i - 1, lib);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.removeNodeFromParent(node);
        model.insertNodeInto(node, rootNode, i - 1);
        tree.setSelectionPath(path);
    }

    private void handleMoveEntryDown() {
        TreePath path = tree.getSelectionPath();
        if(path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if(!(node.getUserObject() instanceof Entry)) {
            return;
        }
        Entry lib = (Entry) node.getUserObject();
        int i = catalog.getEntries().indexOf(lib);
        if(i == catalog.getEntries().size() - 1) {
            return;
        }
        catalog.removeEntry(lib);
        catalog.addEntry(i + 1, lib);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.removeNodeFromParent(node);
        model.insertNodeInto(node, rootNode, i + 1);
        tree.setSelectionPath(path);
    }

    private void updateState() {
        TreePath path = tree.getSelectionPath();
        if(path == null) {
            removeLibraryAction.setEnabled(false);
            moveLibraryDownAction.setEnabled(false);
            moveLibraryUpAction.setEnabled(false);
            return;
        }
        Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        removeLibraryAction.setEnabled(o instanceof Entry);
        moveLibraryDownAction.setEnabled(o instanceof Entry);
        moveLibraryUpAction.setEnabled(o instanceof Entry);
    }

    private void reloadTree() {
        rootNode.removeAllChildren();
        for(Entry lib : catalog.getEntries()) {
            insertLibraryIntoTree(lib);
        }
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(rootNode);
    }

    private void removeLibraryFromTree(Entry lib) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        MutableTreeNode removeNode = null;
        for(int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if(node.getUserObject().equals(lib)) {
                removeNode = node;
                break;
            }
        }
        if(removeNode != null) {
            model.removeNodeFromParent(removeNode);
        }
    }

    private void insertLibraryIntoTree(Entry entry) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode libNode = new DefaultMutableTreeNode(entry);
        model.insertNodeInto(libNode, rootNode, rootNode.getChildCount());
        if(entry instanceof GroupEntry) {
            for(Entry subEntry : ((GroupEntry) entry).getEntries()) {
                insertEntryIntoTree(libNode, subEntry);
            }
        }
        tree.expandPath(new TreePath(libNode.getPath()));
    }

    private void insertEntryIntoTree(DefaultMutableTreeNode parent,
                                     Entry entry) {
        if(entry instanceof UriEntry && !isHidden((UriEntry) entry)) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode entryNode = new DefaultMutableTreeNode(entry);
            model.insertNodeInto(entryNode, parent, parent.getChildCount());
        }
    }

    private boolean isHidden(UriEntry entry) {
        String u = entry.getName();
        for(String ignoredScheme : CatalogEntryManager.IGNORED_SCHEMES) {
            if(u.startsWith(ignoredScheme)) {
                return true;
            }
        }
        return false;
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    private class OntologyLibraryCellRenderer extends DefaultTreeCellRenderer {

        private boolean hasBorder = false;

        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean sel,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(!hasBorder) {
                label.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                hasBorder = true;
            }

            // Modify the text
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if(userObject instanceof File) {
                label.setText("Catalog file " + ((File) userObject).getPath());
            }
            else if(userObject instanceof Entry) {
                boolean foundManager = false;
                Entry entry = (Entry) userObject;
                for(CatalogEntryManager entryManager : entryManagers) {
                    if(entryManager.isSuitable(entry)) {
                        label.setText(entryManager.getDescription(entry));
                        foundManager = true;
                        break;
                    }
                }
                if(!foundManager && entry.getId() != null) {
                    label.setText("Custom entry with id = " + entry.getId());
                }
                else if(!foundManager) {
                    label.setText("Custom entry");
                }
            }
            else if(userObject instanceof String) {
                label.setText((String) userObject);
            }
            return label;
        }
    }
}
