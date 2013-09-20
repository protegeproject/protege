package org.protege.editor.owl.ui.tree;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererListener;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLModelManagerTree<N extends OWLObject> extends OWLObjectTree<N> implements RefreshableComponent {
	private static final long serialVersionUID = 9168555447316716896L;
	
    private OWLModelManagerListener listener;

    private OWLEntityRendererListener rendererListener;

    public OWLModelManagerEntityRenderer currentRenderer = null; // only use to clean up old listeners

    public OWLModelManagerTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider) {
        super(owlEditorKit, provider);
        initialise(owlEditorKit);
    }


    public OWLModelManagerTree(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<N> provider, Set<N> rootObjects) {
        super(owlEditorKit, provider, rootObjects, owlEditorKit.getModelManager().getOWLObjectComparator());
        initialise(owlEditorKit);
    }


    private void initialise(OWLEditorKit owlEditorKit) {
        final OWLObjectTreeCellRenderer renderer = new OWLObjectTreeCellRenderer(owlEditorKit);
        renderer.setWrap(false);
        setCellRenderer(renderer);
        setHighlightKeywords(false);
        setupListener();
        installPopupMenu();
        setRowHeight(-1);
        autoExpandTree();
    }


    public void refreshComponent() {
        reload();
    }


    public void reload() {
        super.reload();
        autoExpandTree();
    }


    public void setHighlightKeywords(boolean b) {
        TreeCellRenderer ren = getCellRenderer();
        if (ren instanceof OWLObjectTreeCellRenderer) {
            ((OWLObjectTreeCellRenderer) ren).setHighlightKeywords(b);
        }

    }


    private void autoExpandTree() {
        OWLTreePreferences prefs = OWLTreePreferences.getInstance();
        if (!prefs.isAutoExpandEnabled()) {
            return;
        }
        OWLObjectHierarchyProvider<N> prov = getProvider();
        for (N root : prov.getRoots()) {
            autoExpand(root, 0);
        }
    }


    private void autoExpand(N node, int currentDepth) {
        OWLTreePreferences prefs = OWLTreePreferences.getInstance();
        int maxDepth = prefs.getAutoExpansionDepthLimit();
        if (currentDepth >= maxDepth) {
            return;
        }
        OWLObjectHierarchyProvider<N> prov = getProvider();
        int childCountLimit = prefs.getAutoExpansionChildLimit();
        Set<N> children = prov.getChildren(node);
        if (children.size() <= childCountLimit) {
            for (OWLObjectTreeNode<N> treeNode : getNodes(node)) {
                TreePath path = new TreePath(treeNode.getPath());
                expandPath(path);
            }
            for(N child : prov.getChildren(node)) {
                autoExpand(child, currentDepth + 1);
            }
        }

    }


    private void setupListener() {
        listener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                    refreshEntityRenderer();
                }
            }
        };
        getOWLModelManager().addListener(listener);
        rendererListener = new OWLEntityRendererListener() {
            public void renderingChanged(OWLEntity entity, OWLModelManagerEntityRenderer renderer) {
                handleRenderingChanged(entity);
            }
        };
        refreshEntityRenderer();
    }


    private void handleRenderingChanged(OWLEntity entity) {
        try {
            for (OWLObjectTreeNode<N> node : getNodes(entity)) {
                DefaultTreeModel model = (DefaultTreeModel) getModel();
                model.nodeStructureChanged(node);
            }
        }
        catch (ClassCastException e) {

        }
    }


    private void refreshEntityRenderer() {
        invalidate();
        if (currentRenderer != null){
            currentRenderer.removeListener(rendererListener);
        }
        currentRenderer = getOWLModelManager().getOWLEntityRenderer();
        currentRenderer.addListener(rendererListener);
    }



    private void installPopupMenu() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e);
            }


            public void mouseReleased(MouseEvent e) {
                handleMouseEvent(e);
            }
        });
    }


    protected void handleMouseEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            TreePath treePath = getPathForLocation(e.getX(), e.getY());
            if (treePath != null) {
                handlePopupMenuInvoked(treePath, e.getPoint());
            }
        }
    }


    protected void handlePopupMenuInvoked(TreePath path, Point pt) {

    }


    public void dispose() {
        super.dispose();
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().getOWLEntityRenderer().removeListener(rendererListener);
    }
}
