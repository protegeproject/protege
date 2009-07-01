package org.protege.editor.owl.ui.library;

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
import org.semanticweb.owlapi.model.IRI;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


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
        TreePath selPath = tree.getSelectionPath();
        if (selPath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (node.getUserObject() instanceof OntologyLibrary) {
            return;
        }
        IRI iri = ((IRI) node.getUserObject());
        OntologyLibrary lib = (OntologyLibrary) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
        try {
            final URI physicalURI = lib.getPhysicalURI(iri);
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
        return owlEditorKit.getModelManager();
    }


    private void handleAddLibrary() {
        File f = UIUtil.chooseFolder(OntologyLibraryPanel.this, "Please select a folder containing ontologies");
        if (f != null) {
            OntologyLibrary lib = new FolderOntologyLibrary(f);
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
        DefaultMutableTreeNode libNode = new DefaultMutableTreeNode(lib);
        model.insertNodeInto(libNode, rootNode, rootNode.getChildCount());
        TreeSet<IRI> iris = new TreeSet<IRI>(lib.getOntologyIRIs());
        int uriIndex = 0;
        for (IRI iri : iris) {
            model.insertNodeInto(new DefaultMutableTreeNode(iri), libNode, uriIndex);
            uriIndex++;
        }
        tree.expandPath(new TreePath(libNode.getPath()));
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


    public class OntologyLibraryCellRenderer extends DefaultTreeCellRenderer {

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
                    IRI iri = ((IRI) node.getUserObject());
                    OntologyLibrary lib = (OntologyLibrary) parentNode.getUserObject();
                    URI physicalURI = lib.getPhysicalURI(iri);
                    label.setText("<html><body><b>" + iri + "</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "<font color=\"gray\">" + physicalURI + "</font></body></html>");
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
