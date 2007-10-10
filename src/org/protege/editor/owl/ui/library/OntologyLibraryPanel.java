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
import java.net.URI;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileUtils;
import org.protege.editor.core.PropertyUtil;
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


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyLibraryPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(OntologyLibraryPanel.class);


    private OWLEditorKit owlEditorKit;

    private JTree tree;

    private Action removeLibraryAction;

    private DefaultMutableTreeNode rootNode;


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
                addLibrary();
            }
        });
        removeLibraryAction = new AbstractAction("Remove selected library",
                                                 OWLIcons.getIcon("ontology.library.remove.png")) {
            public void actionPerformed(ActionEvent e) {
                removeLibrary();
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
        reloadTree();
        updateState();
    }


    private void showPopupMenu(MouseEvent e) {
        TreePath selPath = tree.getSelectionPath();
        if (selPath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (node.getUserObject() instanceof OntologyLibrary) {
            return;
        }
        URI uri = ((URI) node.getUserObject());
        OntologyLibrary lib = (OntologyLibrary) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
        try {
            final URI physicalURI = lib.getPhysicalURI(uri);
            if (!physicalURI.getScheme().equals("file")) {
                return;
            }
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add(new AbstractAction("Show file...") {
                public void actionPerformed(ActionEvent e) {
                    try {
                        FileUtils.showFile(new File(physicalURI));
                    }
                    catch (IOException ex) {
                        logger.error(ex);
                    }
                }
            });
            popupMenu.show(tree, e.getX(), e.getY());
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }


    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getOWLModelManager();
    }


    private void addLibrary() {
        File f = UIUtil.chooseFolder(OntologyLibraryPanel.this, "Please select a folder containing ontologies");
        if (f != null) {
            getOWLModelManager().getOntologyLibraryManager().addLibrary(new FolderOntologyLibrary(f));
            reloadTree();
        }
    }


    private void removeLibrary() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object o = node.getUserObject();
        if (o instanceof OntologyLibrary) {
            getOWLModelManager().getOntologyLibraryManager().removeLibraray((OntologyLibrary) o);
            reloadTree();
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


    private void reloadTree() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            model.removeNodeFromParent((DefaultMutableTreeNode) rootNode.getChildAt(0));
        }
        OntologyLibraryManager ontologyLibrary = getOWLModelManager().getOntologyLibraryManager();
        int index = 0;
        for (OntologyLibrary lib : ontologyLibrary.getLibraries()) {
            DefaultMutableTreeNode libNode = new DefaultMutableTreeNode(lib);
            model.insertNodeInto(libNode, rootNode, index);
            TreeSet<URI> uris = new TreeSet<URI>(lib.getOntologyURIs());
            int uriIndex = 0;
            for (URI uri : uris) {
                model.insertNodeInto(new DefaultMutableTreeNode(uri), libNode, uriIndex);
                uriIndex++;
            }
            tree.expandPath(new TreePath(libNode.getPath()));
            index++;
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }


    public class OntologyLibraryCellRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // Modify the text
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof OntologyLibrary) {
                label.setText("<html><body><b>" + ((OntologyLibrary) userObject).getDescription() + "</b></body></html>");
                label.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 0));
                setIcon(OWLIcons.getIcon("ontology.library.png"));
            }
            else {
                DefaultMutableTreeNode parentNode = ((DefaultMutableTreeNode) node.getParent());
                if (parentNode != null) {
                    URI uri = ((URI) node.getUserObject());
                    OntologyLibrary lib = (OntologyLibrary) parentNode.getUserObject();
                    URI physicalURI = lib.getPhysicalURI(uri);
                    label.setText("<html><body><b>" + uri + "</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "<font color=\"gray\">" + physicalURI + "</font></body></html>");
                    label.setIcon(null);
                }
                label.setBorder(BorderFactory.createEmptyBorder(2, 20, 8, 0));
            }
            return label;
        }
    }


    public static void showDialog(OWLEditorKit owlEditorKit) {
        UIHelper helper = new UIHelper(owlEditorKit);
        OntologyLibraryPanel panel = new OntologyLibraryPanel(owlEditorKit);
        helper.showDialog("Ontology libraries", panel);
        panel.dispose();
    }
}
