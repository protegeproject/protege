package org.protege.editor.owl.ui.tree;

import com.google.common.eventbus.Subscribe;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.menu.MenuBuilder;
import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.transfer.OWLObjectDragSource;
import org.protege.editor.owl.ui.transfer.OWLObjectDropTarget;
import org.protege.editor.owl.ui.transfer.OWLObjectTreeDragGestureListener;
import org.protege.editor.owl.ui.transfer.OWLObjectTreeDropTargetListener;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.view.HasCopySubHierarchyToClipboard;
import org.protege.editor.owl.ui.view.HasExpandAll;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectTree<N extends OWLObject> extends JTree implements OWLObjectDropTarget, OWLObjectDragSource, HasExpandAll, HasCopySubHierarchyToClipboard, Copyable, RefreshableComponent {

    private Map<OWLObject, Set<OWLObjectTreeNode<N>>> nodeMap;

    private OWLEditorKit eKit;

    private OWLObjectHierarchyProvider<N> provider;

    private OWLObjectHierarchyProviderListener<N> listener;

    private Comparator<OWLObject> comparator;

    private OWLTreeDragAndDropHandler<N> dragAndDropHandler;

    private boolean dragOriginator;

    private Point mouseDownPos;

    private Optional<PopupMenuId> popupMenuId = Optional.empty();


    public OWLObjectTree(OWLEditorKit eKit, OWLObjectHierarchyProvider<N> provider) {
        this(eKit, provider, null);
    }


    public OWLObjectTree(OWLEditorKit eKit, OWLObjectHierarchyProvider<N> provider,
                         Comparator<OWLObject> objectComparator) {
        this(eKit, provider, provider.getRoots(), objectComparator);
    }


    public OWLObjectTree(OWLEditorKit eKit, OWLObjectHierarchyProvider<N> provider, Set<N> rootObjects,
                         Comparator<OWLObject> owlObjectComparator) {
        this.eKit = eKit;
        setupLineStyle();


        ToolTipManager.sharedInstance().registerComponent(this);

        this.comparator = owlObjectComparator;
        this.provider = provider;

        nodeMap = new HashMap<>();
        listener = new OWLObjectHierarchyProviderListener<N>() {
            public void hierarchyChanged() {
                reload();
            }


            public void nodeChanged(N node) {
                updateNode(node);
            }
        };
        provider.addListener(listener);
        setModel(new DefaultTreeModel(new OWLObjectTreeRootNode<>(this, rootObjects)));
        setShowsRootHandles(true);
        setRootVisible(false);
        setRowHeight(18);
        setScrollsOnExpand(true);
        setAutoscrolls(true);
        setExpandsSelectedPaths(true);
        OWLTreePreferences treePreferences = OWLTreePreferences.getInstance();
        DropTarget dt = new DropTarget(this, new OWLObjectTreeDropTargetListener(this, treePreferences));
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new OWLObjectTreeDragGestureListener(eKit, this));

        addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 3 && e.isControlDown() && e.isShiftDown()) {
                    reload();
                }
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {   
                	ensureSelected(e.getX(), e.getY());
                	showPopupMenu(e);
                }
                if (e.isAltDown()) {
                    expandDescendantsOfRowAt(e.getX(), e.getY());
                }
            }
        });
        
        
        

        getSelectionModel().addTreeSelectionListener(event -> {
            scrollPathToVisible(event.getNewLeadSelectionPath());
        });
    }

    private void setupLineStyle() {
        if(OWLTreePreferences.getInstance().isPaintLines()) {
            putClientProperty("JTree.lineStyle", "Angled");
        }
        else {
            putClientProperty("JTree.lineStyle", "None");
        }
    }

    public void expandDescendantsOfRowAt(final int x, int y) {
        // It's necessary to traverse all rows to find the path where the user clicked.  This is because
        // the getRowAt(X,Y) call only returns a row index if the actual node rendering is clicked.  We
        // Want to detect if the node handle is clicked (or anywhere in the white space of a row).
        for(int i = 0; i < getRowCount(); i++) {
            Rectangle rowBounds = getRowBounds(i);
            if(rowBounds != null && rowBounds.y <= y && y <= rowBounds.y + rowBounds.height) {
            	expandDescendantsOfRow(i);
                break;
            }
        }
    }
    
    public void ensureSelected(final int x, int y) {
        // It's necessary to traverse all rows to find the path where the user clicked.  This is because
        // the getRowAt(X,Y) call only returns a row index if the actual node rendering is clicked.  We
        // Want to detect if the node handle is clicked (or anywhere in the white space of a row).
        for(int i = 0; i < getRowCount(); i++) {
            Rectangle rowBounds = getRowBounds(i);
            if(rowBounds != null && rowBounds.y <= y && y <= rowBounds.y + rowBounds.height) {
            	this.setSelectionRow(i);
            	this.requestFocusInWindow();
                break;
            }
        }
    }

    /**
     * Clears the data displayed by the component and
     * reloads data.
     */
    @Override
    final public void refreshComponent() {
        setupLineStyle();
        reload();
    }

    /**
     * Sets the popupMenuId for this tree.
     *
     * @param popupMenuId The id.  Not {@code null}.
     */
    public void setPopupMenuId(PopupMenuId popupMenuId) {
        this.popupMenuId = Optional.of(checkNotNull(popupMenuId));
    }

    /**
     * Clears the popupMenuId for this tree.
     */
    public void clearPopupMenuId() {
        this.popupMenuId = Optional.empty();
    }

    private Optional<PopupMenuId> getPopupMenuId() {
        return popupMenuId;
    }

    public void showPopupMenu(MouseEvent e) {
        if (!getPopupMenuId().isPresent()) {
            return;
        }
        MenuBuilder menuBuilder = new MenuBuilder(eKit);
        PopupMenuId popupMenuId = getPopupMenuId().get();
        JPopupMenu popupMenu = menuBuilder.buildPopupMenu(popupMenuId);
        popupMenu.show(this, e.getX(), e.getY());
    }


    public String getToolTipText(MouseEvent event) {
        N obj = getOWLObjectAtMousePosition(event);
        if (obj instanceof OWLEntity) {
            return ((OWLEntity) obj).getIRI().toString();
        }
        return null;
    }


    private void updateNode(N node) {
        // This method is called when the parents or children of
        // a node might have changed.  We handle the following possibilities
        // 1) The node had a child added
        // 2) The node had a child removed
        // If we are displaying the node which had the child added or removed
        // then we update the node

        final Set<OWLObjectTreeNode<N>> treeNodes = nodeMap.get(node);

        // The parents/children might have changed
        if (treeNodes != null && !treeNodes.isEmpty()) {
            // Remove children that aren't there any more
            Set<N> children = provider.getChildren(node);


            Set<OWLObjectTreeNode<N>> nodesToRemove = new HashSet<>();
            for (OWLObjectTreeNode<N> treeNode : treeNodes) {
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    OWLObjectTreeNode<N> childTreeNode = (OWLObjectTreeNode<N>) treeNode.getChildAt(i);
                    if (!children.contains(childTreeNode.getOWLObject())) {
                        nodesToRemove.add(childTreeNode);
                    }
                }
            }

            for (OWLObjectTreeNode<N> nodeToRemove : nodesToRemove) {
                // update the nodeMap to remove this parent from the child
                final Set<OWLObjectTreeNode<N>> childNodes = getNodes(nodeToRemove.getOWLObject());
                final Set<OWLObjectTreeNode<N>> updatedChildNodes = new HashSet<>();
                for (OWLObjectTreeNode<N> childNode : childNodes) {
                    if (!treeNodes.contains(childNode.getParent())) {
                        updatedChildNodes.add(childNode);
                    }
                }
                nodeMap.put(nodeToRemove.getOWLObject(), updatedChildNodes);
                ((DefaultTreeModel) getModel()).removeNodeFromParent(nodeToRemove);
            }

            // Add new children
            Set<N> existingChildren = new HashSet<>();
            for (OWLObjectTreeNode<N> treeNode : treeNodes) {
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    existingChildren.add(((OWLObjectTreeNode<N>) treeNode.getChildAt(i)).getOWLObject());
                }
            }


            for (OWLObjectTreeNode<N> treeNode : treeNodes) {
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
     * all expanded paths except for the current selection.
     */
    public void reload() {
        N currentSelection = getSelectedOWLObject();
        // Reload the tree
        nodeMap.clear();
        // TODO: getRoots needs to be changed - the user might have specified specific roots
        Set<N> roots = provider.getRoots();
        OWLObjectTreeRootNode<N> rootNode = new OWLObjectTreeRootNode<>(this, roots);
        ((DefaultTreeModel) getModel()).setRoot(rootNode);
        setSelectedOWLObject(currentSelection);
    }


    private void expandDescendantsOfRow(int row) {
        if(row == -1) {
            return;
        }
        TreePath pathToExpand = getPathForRow(row);
        if (pathToExpand == null) {
            return;
        }
        Stack<TreePath> stack = new Stack<>();
        stack.push(pathToExpand);

        while (!stack.isEmpty()) {
            TreePath path = stack.pop();
            for (int i = 0; i < getModel().getChildCount(path.getLastPathComponent()); i++) {
                Object curChild = getModel().getChild(path.getLastPathComponent(), i);
                TreePath childPath = path.pathByAddingChild(curChild);
                expandPath(childPath);
                stack.push(childPath);
            }
        }
    }


    public void setDragAndDropHandler(OWLTreeDragAndDropHandler<N> dragAndDropHandler) {
        this.dragAndDropHandler = dragAndDropHandler;
    }


    /**
     * @return the hierarchy provider that this tree uses to generate its branches
     */
    public OWLObjectHierarchyProvider<N> getProvider() {
        return provider;
    }


    /**
     * @return the comparator used to order sibling tree nodes
     */
    public Comparator<OWLObject> getOWLObjectComparator() {
        return (comparator != null) ? comparator : eKit.getOWLModelManager().getOWLObjectComparator();
    }


    /**
     * Sets the tree ordering and reloads the tree contents.
     *
     * @param owlObjectComparator the comparator that is used to order sibling tree nodes
     */
    public void setOWLObjectComparator(Comparator<OWLObject> owlObjectComparator) {
        this.comparator = owlObjectComparator;
        reload();
    }


//    private void removeDescendantNodesFromMap(OWLObjectTreeNode<N> parentNode) {
//        Enumeration e = parentNode.depthFirstEnumeration();
//        while (e.hasMoreElements()) {
//            OWLObjectTreeNode<N> curNode = (OWLObjectTreeNode<N>) e.nextElement();
//            getNodes(curNode.getOWLObject()).remove(curNode);
//        }
//    }


    protected List<OWLObjectTreeNode<N>> getChildNodes(OWLObjectTreeNode<N> parent) {
        List<OWLObjectTreeNode<N>> result = new ArrayList<>();
        Set<N> parentObjects = getParentObjectsForNode(parent);
        List<N> children = new ArrayList<>(provider.getChildren(parent.getOWLObject()));
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
        Set<N> parentObjects = new HashSet<>();
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
     *
     * @param n The object whose nodes are to be retrieved.
     * @return The nodes that represent the specified object.
     */
    protected Set<OWLObjectTreeNode<N>> getNodes(OWLObject n) {
        Set<OWLObjectTreeNode<N>> nodes = nodeMap.get(n);
        if (nodes == null) {
            nodes = new HashSet<>();
            nodeMap.put(n, nodes);
        }
        return nodes;
    }


    protected OWLObjectTreeNode<N> createTreeNode(N owlObject) {
        OWLObjectTreeNode<N> treeNode = new OWLObjectTreeNode<>(owlObject, this);
        for (N equiv : provider.getEquivalents(owlObject)) {
            treeNode.addEquivalentObject(equiv);
        }
        getNodes(owlObject).add(treeNode);
        return treeNode;
    }


    /**
     * If the object is contained in a collapsed branch then the branch is expanded.
     *
     * @param selObject the object to select if it exists in the tree
     */
    public void setSelectedOWLObject(N selObject) {
        setSelectedOWLObject(selObject, false);
    }


    public void setSelectedOWLObject(N selObject, boolean selectAll) {
        if (selObject == null) {
            return;
        }
        setSelectedOWLObjects(Collections.singleton(selObject), selectAll);
    }


    public void setSelectedOWLObjects(Set<N> owlObjects) {
        setSelectedOWLObjects(owlObjects, false);
    }


    public void setSelectedOWLObjects(Set<N> owlObjects, boolean selectAll) {
        List<N> currentSelection = getSelectedOWLObjects();
        if (currentSelection.containsAll(owlObjects) && owlObjects.containsAll(currentSelection)) {
            return;
        }
        clearSelection();
        if (!owlObjects.isEmpty()) {
            final List<TreePath> paths = new ArrayList<>();
            for (N obj : owlObjects) {
                Set<OWLObjectTreeNode<N>> nodes = getNodes(obj);
                if (nodes.isEmpty()) {
                    expandAndSelectPaths(obj, selectAll);
                }
                paths.addAll(getPaths(obj, selectAll));
            }
            if (!paths.isEmpty()) {
                setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
                // without this the selection never quite makes it onto the screen
                // probably because the component has not been sized yet
                SwingUtilities.invokeLater(() -> {
                    scrollPathToVisible(paths.get(0));
                });
            }
        }
    }


    private List<TreePath> getPaths(N selObject, boolean selectAll) {
        List<TreePath> paths = new ArrayList<>();
        Set<OWLObjectTreeNode<N>> nodes = getNodes(selObject);
        for (OWLObjectTreeNode<N> node : nodes) {
            paths.add(new TreePath(node.getPath()));
            if (!selectAll) {
                break;
            }
        }
        return paths;
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
        List<N> selObjects = new ArrayList<>();
        TreePath[] selPaths = getSelectionPaths();
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


    public boolean dropOWLObjects(final List<OWLObject> owlObjects, Point pt, int type) {
        if (dragAndDropHandler == null) {
            return false;
        }

        if (!OWLTreePreferences.getInstance().isTreeDragAndDropEnabled()) {
            return false;
        }

        TreePath dropPath = getPathForLocation(pt.x, pt.y);
        if (dropPath == null) {
            // If the object hasn't been dropped on a node, then don't accept drop
            return false;
        }

        N dropTargetObj = ((OWLObjectTreeNode<N>) dropPath.getLastPathComponent()).getOWLObject();

        final Set<N> droppedObjects = new HashSet<>();

        for (final OWLObject owlObject : owlObjects) {
            if (!dropTargetObj.equals(owlObject) && // don't drop on self
                    dragAndDropHandler.canDrop(owlObject, dropTargetObj)) {

                // the object must be in the acceptable bounds for the handler by now
                N dropObject = (N) owlObject;

                droppedObjects.add(dropObject);

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
                    dragAndDropHandler.add(dropObject, dropTargetObj);
                }
                else {
                    if (selObj.equals(owlObject)) {
                        if (selObjParent != null) {
                            dragAndDropHandler.move(dropObject, selObjParent, dropTargetObj);
                        }
                        else {
                            dragAndDropHandler.add(dropObject, dropTargetObj);
                        }
                    }
                    else {
                        // ADD op
                        dragAndDropHandler.add(dropObject, dropTargetObj);
                    }
                }
            }
        }

        if (droppedObjects.isEmpty()) {
            return false;
        }
        else {
            SwingUtilities.invokeLater(() -> {
                Set<N> nodes = new HashSet<>();
                for (N droppedObject : droppedObjects) {
                    if (getNodes(droppedObject) != null) { // if this node exists in the tree
                        nodes.add(droppedObject);
                    }
                }
                setSelectedOWLObjects(nodes);
            });
        }

        return true;
    }


    public OWLModelManager getOWLModelManager() {
        return eKit.getModelManager();
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
        if (OWLTreePreferences.getInstance().isTreeDragAndDropEnabled() && dropRow != -1) {
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

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {

    }

    protected N getOWLObjectAtMousePosition(MouseEvent event) {
        Point pt = event.getPoint();
        TreePath path = getPathForLocation(pt.x, pt.y);
        if (path == null) {
            return null;
        }
        OWLObjectTreeNode<N> node = (OWLObjectTreeNode<N>) path.getLastPathComponent();
        return node.getOWLObject();
    }

    @Override
    public void copySubHierarchyToClipboard() {
        N selObject = getSelectedOWLObject();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        copySubHierarchyToClipboard(selObject, pw, 0);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(sw.toString()), null);
    }

    private void copySubHierarchyToClipboard(N object, PrintWriter printWriter, int depth) {
        for (int i = 0; i < depth; i++) {
            printWriter.print("\t");
        }
        String rendering = getOWLModelManager().getRendering(object);
        printWriter.println(rendering);
        Set<N> children = provider.getChildren(object);
        List<N> sortedChildren = new ArrayList<>(children);
        sortedChildren.sort(new OWLObjectComparator<>(getOWLModelManager()));
        for (N child : sortedChildren) {
            copySubHierarchyToClipboard(child, printWriter, depth + 1);
        }
    }

    @Override
    public boolean canPerformCopySubHierarchyToClipboard() {
        return getSelectedOWLObject() != null;
    }

    @Override
    public boolean canCopy() {
        return !getSelectedOWLObjects().isEmpty();
    }

    @Override
    public List<OWLObject> getObjectsToCopy() {
        return new ArrayList<>(getSelectedOWLObjects());
    }
}
