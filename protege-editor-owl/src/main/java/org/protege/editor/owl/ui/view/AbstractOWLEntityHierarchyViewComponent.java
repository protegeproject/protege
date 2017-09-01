package org.protege.editor.owl.ui.view;

import org.protege.editor.core.HasUpdateState;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewAction;
import org.protege.editor.core.ui.view.ViewMode;
import org.protege.editor.core.util.HandlerRegistration;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.util.OWLUtilities;
import org.protege.editor.owl.ui.OWLObjectComparatorAdapter;
import org.protege.editor.owl.ui.action.OWLObjectHierarchyDeleter;
import org.protege.editor.owl.ui.breadcrumb.Breadcrumb;
import org.protege.editor.owl.ui.breadcrumb.BreadcrumbTrailChangedHandler;
import org.protege.editor.owl.ui.breadcrumb.BreadcrumbTrailProvider;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 23, 2009<br><br>
 */
public abstract class AbstractOWLEntityHierarchyViewComponent<E extends OWLEntity> extends AbstractOWLSelectionViewComponent
 implements Findable<E>, Deleteable, HasDisplayDeprecatedEntities, BreadcrumbTrailProvider {

    private OWLObjectTree<E> assertedTree;

    private Optional<OWLObjectTree<E>> inferredTree;

    private TreeSelectionListener listener;

    private OWLObjectHierarchyDeleter<E> hierarchyDeleter;

    private final Logger logger = LoggerFactory.getLogger(AbstractOWLEntityHierarchyViewComponent.class);

    private final ViewModeComponent<OWLObjectTree<E>> viewModeComponent = new ViewModeComponent<>();

    @Nullable
    private HandlerRegistration breadCrumbTrailProviderRegistration;

    final public void initialiseView() throws Exception {
        setLayout(new BorderLayout(0, 0));
        add(viewModeComponent, BorderLayout.CENTER);
        assertedTree = new OWLModelManagerTree<>(getOWLEditorKit(), getHierarchyProvider());

        // ordering based on default, but putting Nothing at the top
        OWLObjectComparatorAdapter<OWLObject> treeNodeComp = createComparator(getOWLModelManager());
        assertedTree.setOWLObjectComparator(treeNodeComp);


        // render keywords should be on now for class expressions
        final TreeCellRenderer treeCellRenderer = assertedTree.getCellRenderer();
        if (treeCellRenderer instanceof OWLCellRenderer){
            ((OWLCellRenderer) treeCellRenderer).setHighlightKeywords(true);
        }

        viewModeComponent.add(assertedTree, ViewMode.ASSERTED, true);


        performExtraInitialisation();
        E entity = getSelectedEntity();
        if (entity != null) {
            setGlobalSelection(entity);
        }
        TreeModelListener treeModelListener = new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
            }
            public void treeNodesInserted(TreeModelEvent e) {
                ensureSelection();
            }
            public void treeNodesRemoved(TreeModelEvent e) {
                ensureSelection();
            }
            public void treeStructureChanged(TreeModelEvent e) {
                ensureSelection();
            }
        };
        assertedTree.getModel().addTreeModelListener(treeModelListener);

        assertedTree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                transmitSelection();
            }
        });


        try {
            Optional<OWLObjectHierarchyProvider<E>> inferredHierarchyProvider = getInferredHierarchyProvider();
            if (inferredHierarchyProvider.isPresent()) {
                inferredTree = Optional.of(new OWLModelManagerTree<>(getOWLEditorKit(), inferredHierarchyProvider.get()));
                inferredTree.get().setBackground(OWLFrameList.INFERRED_BG_COLOR);
                inferredTree.get().setOWLObjectComparator(treeNodeComp);
                inferredTree.get().getModel().addTreeModelListener(treeModelListener);
                inferredTree.get().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        transmitSelection();
                    }
                });
                viewModeComponent.add(inferredTree.get(), ViewMode.INFERRED, true);
                getView().addViewMode(ViewMode.ASSERTED);
                getView().addViewMode(ViewMode.INFERRED);
                getView().addViewModeChangedHandler(this::switchViewMode);
            }
            else {
                inferredTree = Optional.empty();
            }
        } catch (Exception e) {
            logger.error("An error occurred whilst getting the inferred hierarchy provider", e);
        }

        hierarchyDeleter = new OWLObjectHierarchyDeleter<>(getOWLEditorKit(),
                                                                   getHierarchyProvider(),
                                                                    () -> new HashSet<>(assertedTree.getSelectedOWLObjects()),
                                                                   getCollectiveTypeName());
        listener = e -> transmitSelection();
        assertedTree.addTreeSelectionListener(listener);
        if (inferredTree.isPresent()) {
            inferredTree.get().addTreeSelectionListener(listener);
        }

        breadCrumbTrailProviderRegistration = getOWLWorkspace().registerBreadcrumbTrailProvider(this);
    }

    protected boolean isInAssertedMode() {
        return getView().getViewMode().equals(Optional.of(ViewMode.ASSERTED));
    }

    private static OWLObjectComparatorAdapter<OWLObject> createComparator(OWLModelManager modelManager) {
        final Comparator<OWLObject> comp = modelManager.getOWLObjectComparator();
        return new OWLObjectComparatorAdapter<OWLObject>(comp) {
            public int compare(OWLObject o1, OWLObject o2) {
                if (modelManager.getOWLDataFactory().getOWLNothing().equals(o1)) {
                    return -1;
                }
                else if (modelManager.getOWLDataFactory().getOWLNothing().equals(o2)) {
                    return 1;
                }
                else {
                    boolean deprecated1 = modelManager.isDeprecated(o1);
                    boolean deprecated2 = modelManager.isDeprecated(o2);
                    if (deprecated1 != deprecated2) {
                        return deprecated1 ? 1 : -1;
                    }
                    return comp.compare(o1, o2);
                }
            }
        };
    }

    private void switchViewMode(Optional<ViewMode> viewMode) {
        E sel = viewModeComponent.getComponentForCurrentViewMode().getSelectedOWLObject();
        viewModeComponent.setViewMode(viewMode);
        if(sel != null) {
            setSelectedEntity(sel);
        }
        updateViewActions();
        updateView();
        fireBreadcrumbTrailChanged();
    }

    private void updateViewActions() {
        for(ViewAction viewAction : getView().getViewActions()) {
            if(viewAction instanceof HasUpdateState) {
                ((HasUpdateState) viewAction).updateState();
            }
        }
    }

    protected abstract void performExtraInitialisation() throws Exception;


    protected abstract OWLObjectHierarchyProvider<E> getHierarchyProvider();

    protected abstract Optional<OWLObjectHierarchyProvider<E>> getInferredHierarchyProvider();


    /**
     * Override with the name of the entities to be used in the Edit | Delete menu - eg "classes"
     * @return String the name of the entities
     */
    protected String getCollectiveTypeName(){
        return "entities";
    }


    public void setSelectedEntity(E entity) {
        getTree().setSelectedOWLObject(entity);
    }

    public OWLObjectTree<E> getAssertedTree() {
        return assertedTree;
    }


    public void setSelectedEntities(Set<E> entities) {
        getTree().setSelectedOWLObjects(entities);
    }


    public E getSelectedEntity() {
        return getTree().getSelectedOWLObject();
    }


    public Set<E> getSelectedEntities() {
        return new HashSet<>(getTree().getSelectedOWLObjects());
    }


    private void ensureSelection() {
        SwingUtilities.invokeLater(() -> {
            final E entity = getSelectedEntity();
            if (entity != null) {
                E treeSel = getTree().getSelectedOWLObject();
                if (treeSel == null || !treeSel.equals(entity)) {
                    getTree().setSelectedOWLObject(entity);
                }
            }
        });
    }


    public boolean requestFocusInWindow() {
        return getTree().requestFocusInWindow();
    }


    protected OWLObjectTree<E> getTree() {
        Optional<ViewMode> viewMode= getView().getViewMode();
        return viewModeComponent.getComponentForViewMode(viewMode);
    }


    protected void transmitSelection() {
        deletableChangeListenerMediator.fireStateChanged(this);
        E selEntity = getSelectedEntity();
        if (selEntity != null) {
            final View view = getView();
            if (view != null && !view.isPinned()){
                view.setPinned(true); // so that we don't follow the selection
                setGlobalSelection(selEntity);
                view.setPinned(false);
            }
            else{
                setGlobalSelection(selEntity);
            }
        }
        else {
            setGlobalSelection(null);
        }
        updateHeader(selEntity);
        fireBreadcrumbTrailChanged();
    }


    protected E updateView(E selEntity) {
        if (getTree().getSelectedOWLObject() == null) {
            if (selEntity != null) {
                getTree().setSelectedOWLObject(selEntity);
            }
        }
        else {
            if (!getTree().getSelectedOWLObject().equals(selEntity)) {
                getTree().setSelectedOWLObject(selEntity);
            }
        }
        return selEntity;
    }


    public void disposeView() {
        // Dispose of the assertedTree selection listener
        if (assertedTree != null) {
            assertedTree.removeTreeSelectionListener(listener);
            assertedTree.dispose();
        }
        if (inferredTree.isPresent()) {
            inferredTree.get().removeTreeSelectionListener(listener);
            inferredTree.get().dispose();
        }
        if(breadCrumbTrailProviderRegistration != null) {
            breadCrumbTrailProviderRegistration.removeHandler();
        }
    }


    protected OWLObject getObjectToCopy() {
        return getTree().getSelectedOWLObject();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of Deleteable
    //
    /////////////////////////////////////////////////////////////////////////////////////

    private ChangeListenerMediator deletableChangeListenerMediator = new ChangeListenerMediator();


    public void addChangeListener(ChangeListener listener) {
        deletableChangeListenerMediator.addChangeListener(listener);
    }


    public void removeChangeListener(ChangeListener listener) {
        deletableChangeListenerMediator.removeChangeListener(listener);
    }


    public void handleDelete() {
        hierarchyDeleter.performDeletion();
    }


    public boolean canDelete() {
        return !getTree().getSelectedOWLObjects().isEmpty();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of Findable
    //
    /////////////////////////////////////////////////////////////////////////////////////



    public void show(E owlEntity) {
        getTree().setSelectedOWLObject(owlEntity);
    }

    @Override
    public void setShowDeprecatedEntities(boolean showDeprecatedEntities) {
        if(showDeprecatedEntities) {
            getHierarchyProvider().setFilter(e -> true);
        }
        else {
            getHierarchyProvider().setFilter(this::isNotDeprecated);
        }
    }

    private boolean isNotDeprecated(E e) {
        return !getOWLModelManager().isDeprecated(e);
    }


    ////////////////////////////////


    @Nonnull
    @Override
    public List<Breadcrumb> getBreadcrumbTrail() {
        return getTree().getBreadcrumbTrail();
    }

    private final List<BreadcrumbTrailChangedHandler> breadcrumbTrailChangedHandlers = new ArrayList<>();

    @Nonnull
    @Override
    public HandlerRegistration addBreadcrumbTrailChangedHandler(@Nonnull BreadcrumbTrailChangedHandler handler) {
        breadcrumbTrailChangedHandlers.add(handler);
        return () -> {
            breadcrumbTrailChangedHandlers.remove(handler);
        };
    }

    @Override
    public void goToBreadcrumb(@Nonnull Breadcrumb breadcrumb) {
        getTree().goToBreadcrumb(breadcrumb);
    }

    private void fireBreadcrumbTrailChanged() {
        new ArrayList<>(breadcrumbTrailChangedHandlers).forEach(BreadcrumbTrailChangedHandler::handleBreadcrumbTrailChanged);
    }

    @Nonnull
    @Override
    public JComponent asJComponent() {
        return this;
    }
}
