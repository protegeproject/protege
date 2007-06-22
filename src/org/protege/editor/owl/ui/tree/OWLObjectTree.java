package org.protege.editor.owl.ui.tree;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.protege.editor.owl.ui.transfer.OWLObjectDragSource;
import org.protege.editor.owl.ui.transfer.OWLObjectDropTarget;
import org.protege.editor.owl.ui.transfer.OWLObjectTreeDragGestureListener;
import org.protege.editor.owl.ui.transfer.OWLObjectTreeDropTargetListener;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectTree<N extends OWLObject> extends JTree implements OWLObjectDropTarget, OWLObjectDragSource {

    private static final Logger logger = Logger.getLogger(OWLObjectTree.class);

    private Map<OWLObject, Set<OWLObjectTreeNode<N>>> nodeMap;

    private OWLEditorKit owlEditorKit;

    private OWLObjectHierarchyProvider<N> provider;

    private OWLObjectHierarchyProviderListener listener;

    private Comparator<N> comparator;

    private OWLTreeDragAndDropHandler<N> dragAndDropHandler;

    private boolean dragOriginator;

    private boolean menuShortCutKeyDown;

    private Point mouseDownPos;


    public OWLObjectTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider,
                         Comparator<N> objectComparator) {
        this(owlEditorKit, provider, provider.getRoots(), objectComparator);
    }


    public String getToolTipText(MouseEvent event) {
        Point pt = event.getPoint();
        TreePath path = getPathForLocation(pt.x, pt.y);
        if (path == null) {
            return null;
        }
        OWLObjectTreeNode<N> node = (OWLObjectTreeNode<N>) path.getLastPathComponent();
        N obj = node.getOWLObject();
        if (obj instanceof OWLEntity) {
            return ((OWLEntity) obj).getURI().toString();
        }
        return null;
    }


    public OWLObjectTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider, Set<N> rootObjects,
                         Comparator<N> owlObjectComparator) {
        this.owlEditorKit = owlEditorKit;

        ToolTipManager.sharedInstance().registerComponent(this);
        this.comparator = owlObjectComparator;
        this.provider = provider;
        this.nodeMap = new HashMap<OWLObject, Set<OWLObjectTreeNode<N>>>();
        listener = new OWLObjectHierarchyProviderListener<N>() {
            public void hierarchyChanged() {
                reload();
            }


            public void nodeChanged(N node) {
                updateNode(node);
            }
        };
        provider.addListener(listener);
        setModel(new DefaultTreeModel(new OWLObjectTreeRootNode<N>(this, rootObjects)));
        setShowsRootHandles(true);
        setRootVisible(false);
        setRowHeight(18);
        setScrollsOnExpand(true);
        setAutoscrolls(true);
        setExpandsSelectedPaths(true);
        DropTarget dt = new DropTarget(this, new OWLObjectTreeDropTargetListener(this));
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
                                                      DnDConstants.ACTION_COPY_OR_MOVE,
                                                      new OWLObjectTreeDragGestureListener(owlEditorKit, this));

        // A temp fix incase the tree somehow becomes unsynchronised
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 3 && e.isControlDown() && e.isShiftDown()) {
                    reload();
                }
            }
        });


        setRowHeight(18);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Check to see if the recursively expand key is held down.  This is
                // the key that corresponds to the menu accelerator key (CTRL on Windows, and
                // CMD on the Mac).
                menuShortCutKeyDown = (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
            }
        });


        addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
                handleExpansionEvent(event);
            }


            public void treeCollapsed(TreeExpansionEvent event) {
                // Do nothing
            }
        });
    }


    private void updateNode(N node) {
        // This method is called when the parents or children of
        // a node might have changed.  We handle the following possibilities
        // 1) The node had a child added
        // 2) The node had a child removed
        // If we are displaying the node which had the child added or removed
        // then we update the node

        // The parents/children might have changed
        if (nodeMap.containsKey(node)) {
            // Remove children that aren't there any more
            Set<N> children = provider.getChildren(node);

            Set<OWLObjectTreeNode<N>> nodesToRemove = new HashSet<OWLObjectTreeNode<N>>();
            for (OWLObjectTreeNode<N> treeNode : nodeMap.get(node)) {
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    OWLObjectTreeNode<N> childTreeNode = (OWLObjectTreeNode<N>) treeNode.getChildAt(i);
                    if (!children.contains(childTreeNode.getOWLObject())) {
                        nodesToRemove.add(childTreeNode);
                    }
                }
            }
            for (OWLObjectTreeNode<N> nodeToRemove : nodesToRemove) {
                ((DefaultTreeModel) getModel()).removeNodeFromParent(nodeToRemove);
            }

            // Add new children
            Set<N> existingChildren = new HashSet<N>();
            for (OWLObjectTreeNode<N> treeNode : nodeMap.get(node)) {
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    existingChildren.add(((OWLObjectTreeNode<N>) treeNode.getChildAt(i)).getOWLObject());
                }
            }


            for (OWLObjectTreeNode<N> treeNode : nodeMap.get(node)) {
                for (N child : children) {
                    if (!existingChildren.contains(child)) {
                        OWLObjectTreeNode<N> childTreeNode = createTreeNode(child);
                        ((DefaultTreeModel) getModel()).insertNodeInto(childTreeNode, treeNode, 0);
                    }
                }
            }

            if (provider.getRoots().contains(node)) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) getModel().getRoot();
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    OWLObjectTreeNode<N> objectTreeNode = (OWLObjectTreeNode<N>) treeNode.getChildAt(i);
                    if (objectTreeNode.getOWLObject().equals(node)) {
                        return;
                    }
                }
                ((DefaultTreeModel) getModel()).insertNodeInto(createTreeNode(node), treeNode, 0);
            }
            else {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
                for (int i = 0; i < rootNode.getChildCount(); i++) {
                    OWLObjectTreeNode<N> objectTreeNode = (OWLObjectTreeNode<N>) rootNode.getChildAt(i);
                    if (objectTreeNode.getOWLObject().equals(node)) {
                        ((DefaultTreeModel) getModel()).removeNodeFromParent(objectTreeNode);
                        return;
                    }
                }
            }
        }
        else {
            // Might be a new root!
            if (provider.getRoots().contains(node)) {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
                DefaultMutableTreeNode nn = createTreeNode(node);
                ((DefaultTreeModel) getModel()).insertNodeInto(nn, rootNode, 0);
                expandPath(new TreePath(rootNode.getPath()));
            }
        }
    }


    public void dispose() {
        provider.removeListener(listener);
    }


    public void updateUI() {
        super.updateUI();
        setRowHeight(getFontMetrics(getFont()).getHeight() + 4);
    }


    /**
     * Causes the tree to be reloaded.  Note that this will collapse
     * all expanded paths.
     */
    public void reload() {
        // Reload the tree
        nodeMap.clear();
        // TODO: getRoots needs to be changed - the user might have specified specific roots
        Set<N> roots = provider.getRoots();
        OWLObjectTreeRootNode<N> rootNode = new OWLObjectTreeRootNode(this, roots);
        ((DefaultTreeModel) getModel()).setRoot(rootNode);
    }


    private void handleExpansionEvent(TreeExpansionEvent event) {
        if (menuShortCutKeyDown) {
            // Recursively expand
            for (int i = 0; i < getModel().getChildCount(event.getPath().getLastPathComponent()); i++) {
                Object curChild = getModel().getChild(event.getPath().getLastPathComponent(), i);
                TreePath path = event.getPath().pathByAddingChild(curChild);
                expandPath(path);
            }
        }
    }


    public OWLObjectTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider) {
        this(owlEditorKit, provider, null);
    }


    public void setDragAndDropHandler(OWLTreeDragAndDropHandler<N> dragAndDropHandler) {
        this.dragAndDropHandler = dragAndDropHandler;
    }


    /**
     * Gets the hierarchy provider that this tree uses
     * to generate its branches.
     */
    public OWLObjectHierarchyProvider<N> getProvider() {
        return provider;
    }


    /**
     * Gets the comparator used to order sibling tree nodes.
     */
    public Comparator<N> getOWLObjectComparator() {
        return comparator;
    }


    /**
     * Sets the comparator that is used to order sibling tree nodes.
     */
    public void setOWLObjectComparator(Comparator<N> owlObjectComparator) {
        this.comparator = owlObjectComparator;
        reload();
    }


    private void removeDescendantNodesFromMap(OWLObjectTreeNode<N> parentNode) {
        Enumeration e = parentNode.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            OWLObjectTreeNode<N> curNode = (OWLObjectTreeNode<N>) e.nextElement();
            getNodes(curNode.getOWLObject()).remove(curNode);
        }
    }


    protected List<OWLObjectTreeNode<N>> getChildNodes(OWLObjectTreeNode<N> parent) {
        List<OWLObjectTreeNode<N>> result = new ArrayList<OWLObjectTreeNode<N>>();
        Set<N> parentObjects = getParentObjectsForNode(parent);
        List<N> children = new ArrayList<N>(provider.getChildren(parent.getOWLObject()));
        if (comparator != null) {
            Collections.sort(children, comparator);
        }
        for (N child : children) {
            if (!parentObjects.contains(child)) {
                result.add(createTreeNode(child));
            }
        }
        return result;
    }


    private Set<N> getParentObjectsForNode(OWLObjectTreeNode<N> node) {
        Set<N> parentObjects = new HashSet<N>();
        OWLObjectTreeNode<N> parentNode = node;
        while ((parentNode = (OWLObjectTreeNode<N>) parentNode.getParent()) != null) {
            if (parentNode.getOWLObject() != null) {
                parentObjects.add(parentNode.getOWLObject());
            }
        }
        return parentObjects;
    }


    protected int getChildCount(N owlObject) {
        if (owlObject == null) {
            return provider.getRoots().size();
        }
        else {
            return provider.getChildren(owlObject).size();
        }
    }


    /**
     * Gets the set of nodes that represent the specified
     * object
     * @param n The object whose nodes are to be retrieved.
     * @return The nodes that represent the specified object.
     */
    protected Set<OWLObjectTreeNode<N>> getNodes(OWLObject n) {
        Set<OWLObjectTreeNode<N>> nodes = nodeMap.get(n);
        if (nodes == null) {
            nodes = new HashSet<OWLObjectTreeNode<N>>();
            nodeMap.put(n, nodes);
        }
        return nodes;
    }


    protected OWLObjectTreeNode<N> createTreeNode(N owlObject) {
        OWLObjectTreeNode<N> treeNode = new OWLObjectTreeNode<N>(owlObject, this);
        for (N equiv : provider.getEquivalents(owlObject)) {
            treeNode.addEquivalentObject(equiv);
        }
        getNodes(owlObject).add(treeNode);
        return treeNode;
    }


    /**
     * Sets the selected object.  If the object is contained
     * in a collapsed branch then the branch is expanded.
     */
    public void setSelectedOWLObject(N selObject) {
        if (selObject == null) {
            clearSelection();
            return;
        }
        setSelectedOWLObject(selObject, false);
        TreePath path = getSelectionPath();
        if (path != null) {
            scrollPathToVisible(path);
        }
    }


    public void setSelectedOWLObject(N selObject, boolean selectAll) {
        // If the selected object is null, then clear the
        // tree selection
        if (selObject == null) {
            clearSelection();
            return;
        }
        List<TreePath> paths = new ArrayList<TreePath>();
        Set<OWLObjectTreeNode<N>> nodes = getNodes(selObject);
        if (nodes.isEmpty()) {
            expandAndSelectPaths(selObject, selectAll);
        }
        for (OWLObjectTreeNode<N> node : nodes) {
            paths.add(new TreePath(node.getPath()));
            if (!selectAll) {
                break;
            }
        }
        TreePath [] pathArray = new TreePath [paths.size()];
        for (int i = 0; i < pathArray.length; i++) {
            pathArray[i] = paths.get(i);
        }
        setSelectionPaths(pathArray);
    }


    private void expandAndSelectPaths(N obj, boolean selectAll) {
        for (List<N> objPath : provider.getPathsToRoot(obj)) {
            expandAndSelectPath(objPath);
            if (!selectAll) {
                break;
            }
        }
    }


    private void expandAndSelectPath(List<N> objectPath) {
        // Start from the end of the path and search back
        // through the path until we find a node in the tree
        // that represents the object.  If we find a node, then
        // we need to expand the child nodes.
        int index = 0;
        for (int i = objectPath.size() - 1; i > -1; i--) {
            index = i;
            N curObj = objectPath.get(i);
            if (!getNodes(curObj).isEmpty()) {
                break;
            }
        }
        Set<OWLObjectTreeNode<N>> nodes = getNodes(objectPath.get(index));
        if (nodes.isEmpty()) {
            return;
        }
        OWLObjectTreeNode<N> curParNode = nodes.iterator().next();
        for (int i = index + 1; i < objectPath.size(); i++) {
            expandPath(new TreePath(curParNode.getPath()));
            for (int j = 0; j < curParNode.getChildCount(); j++) {
                OWLObjectTreeNode<N> curChild = (OWLObjectTreeNode<N>) curParNode.getChildAt(j);
                if (curChild.getOWLObject().equals(objectPath.get(i))) {
                    curParNode = curChild;
                    break;
                }
            }
        }
    }


    public N getSelectedOWLObject() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        }
        return ((OWLObjectTreeNode<N>) path.getLastPathComponent()).getOWLObject();
    }


    public List<N> getSelectedOWLObjects() {
        List<N> selObjects = new ArrayList<N>();
        TreePath [] selPaths = getSelectionPaths();
        if (selPaths != null) {
            for (TreePath path : selPaths) {
                selObjects.add((((OWLObjectTreeNode<N>) path.getLastPathComponent()).getOWLObject()));
            }
        }
        return selObjects;
    }


    public JComponent getComponent() {
        return this;
    }


    public void setDragOriginater(boolean b) {
        dragOriginator = b;
    }


    public boolean dropOWLObjects(List<OWLObject> owlObjects, Point pt, int type) {
        if (dragAndDropHandler == null) {
            return false;
        }

        TreePath dropPath = getPathForLocation(pt.x, pt.y);
        if (dropPath == null) {
            // If the object hasn't been dropped on a node, then don't accept drop
            return false;
        }
        N dropTargetObj = ((OWLObjectTreeNode<N>) dropPath.getLastPathComponent()).getOWLObject();
        for (OWLObject owlObject : owlObjects) {
            if (!dropTargetObj.getClass().equals(owlObject.getClass())) {
                // If the object being dropped isn't the same class as the thing it
                // is being dropped on to then return.
                return false;
            }
            if (dropTargetObj.equals(owlObject)) {
                // Don't drop on top of self
                return false;
            }
            TreePath selPath = getSelectionPath();
            N selObj = null;
            N selObjParent = null;
            if (selPath != null) {
                OWLObjectTreeNode<N> selNode = ((OWLObjectTreeNode<N>) selPath.getLastPathComponent());
                selObj = selNode.getOWLObject();
                OWLObjectTreeNode<N> parentNode = (OWLObjectTreeNode<N>) selNode.getParent();
                if (parentNode != null) {
                    selObjParent = parentNode.getOWLObject();
                }
            }

            if (selObj == null) {
                // In ADD operation (We can only add here)
                dragAndDropHandler.add((N) owlObject, dropTargetObj);
                setSelectedOWLObject((N) owlObject);
            }
            else {
                if (selObj.equals(owlObject)) {
                    if (selObjParent != null) {
                        dragAndDropHandler.move((N) owlObject, selObjParent, dropTargetObj);
                    }
                    else {
                        dragAndDropHandler.add((N) owlObject, dropTargetObj);
                    }
                }
                else {
                    // ADD op
                    dragAndDropHandler.add((N) owlObject, dropTargetObj);
                }
            }
            setSelectedOWLObject((N) owlObject);
        }

        return true;
    }


    public OWLModelManager getOWLModelManager() {
        return owlEditorKit.getOWLModelManager();
    }


    private int dropRow = -1;

    /**
     * A timer that is used to automatically expand nodes if the
     * mouse hovers over a node during a drag and drop operation.
     */
    private Timer expandNodeTimer = new Timer(800, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (dropRow != -1) {
                TreePath path = getPathForRow(dropRow);
                expandPath(path);
                expandNodeTimer.stop();
            }
        }
    });


    public int getDropRow() {
        return dropRow;
    }


    public void setDropRow(int dropRow) {
        expandNodeTimer.restart();
        if (this.dropRow != -1) {
            Rectangle r = getDropRowBounds();
            if (r != null) {
                repaint(r);
            }
            expandNodeTimer.stop();
        }
        this.dropRow = dropRow;
        if (this.dropRow != -1) {
            Rectangle r = getDropRowBounds();
            if (r != null) {
                repaint(r);
            }
            expandNodeTimer.start();
            scrollRowToVisible(dropRow);
        }
    }


    public Rectangle getDropRowBounds() {
        Rectangle r = getRowBounds(dropRow);
        if (r == null) {
            return null;
        }
        r.x = r.x - 2;
        r.y = r.y - 2;
        r.width += 4;
        r.height += 4;
        return r;
    }


    private Stroke s = new BasicStroke(2.0f);


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Paint drop node
        if (dropRow != -1) {
            Rectangle r = getRowBounds(dropRow);
            if (r == null) {
                return;
            }

            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ((Graphics2D) g).setStroke(s);
            Color color = UIManager.getDefaults().getColor("Tree.selectionBorderColor");
            g.setColor(color);
            g.drawRoundRect(r.x, r.y, r.width, r.height, 7, 7);
        }
    }


    public void expandAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandPath(getPathForRow(i));
        }
    }
}
