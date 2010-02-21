package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererListener;
import org.semanticweb.owlapi.model.*;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 8, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A view that has a notion of a selected <code>OWLObject</code>
 */
public abstract class AbstractOWLSelectionViewComponent extends AbstractOWLViewComponent implements RefreshableComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 3436509499024697735L;

    private OWLSelectionModelListener listener;

    private Set<OWLSelectionViewAction> registeredActions;

    private boolean initialUpdatePerformed;

    private OWLModelManagerListener modelManagerListener;

    private OWLObject lastDisplayedObject;

    private OWLEntityRendererListener entityRendererListener;

    private HierarchyListener hierarchyListener;

    private boolean needsRefresh;


    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    final public void initialiseOWLView() throws Exception {
        registeredActions = new HashSet<OWLSelectionViewAction>();
        listener = new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {
                final OWLObject owlObject = getOWLWorkspace().getOWLSelectionModel().getSelectedObject();
                if (owlObject instanceof OWLEntity){
                    if (canShowEntity((OWLEntity)owlObject)){
                        updateViewContentAndHeader();
                    }
                }
            }
        };

        entityRendererListener = new OWLEntityRendererListener() {
            public void renderingChanged(OWLEntity entity, OWLEntityRenderer renderer) {
                if (lastDisplayedObject != null) {
                    if (lastDisplayedObject.equals(entity)) {
                        updateHeader(lastDisplayedObject);
                    }
                }
            }
        };

        hierarchyListener = new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if (needsRefresh && isShowing()) {
                    updateViewContentAndHeader();
                }
            }
        };

        modelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                    getOWLModelManager().getOWLEntityRenderer().addListener(entityRendererListener);
                }
            }
        };

        addHierarchyListener(hierarchyListener);

        getOWLModelManager().addListener(modelManagerListener);
        getOWLModelManager().getOWLEntityRenderer().addListener(entityRendererListener);
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);

        initialiseView();
        updateViewContentAndHeader();
    }


    public void refreshComponent() {
        updateHeader(lastDisplayedObject);
    }


    /**
     * A convenience method that sets the specified entity to be the
     * selected entity in the <code>OWLSelectionModel</code>.
     */
    protected void setGlobalSelection(OWLEntity owlEntity) {
        if (getView() != null) {
            if (getView().isSyncronizing()) {
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(owlEntity);
            }
        }
    }


    protected void registerSelectionAction(OWLSelectionViewAction action) {
        registeredActions.add(action);
    }


    protected void addAction(OWLSelectionViewAction action, String group, String groupIndex) {
        registerSelectionAction(action);
        super.addAction(action, group, groupIndex);
    }


    public abstract void initialiseView() throws Exception;


    /**
     * This method is called at the end of a plugin
     * life cycle, when the plugin needs to be removed
     * from the system.  Plugins should remove any listeners
     * that they setup and perform other cleanup, so that
     * the plugin can be garbage collected.
     */
    public final void disposeOWLView() {
        registeredActions.clear();
        if (listener != null) {
            getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        }
        removeHierarchyListener(hierarchyListener);
        getOWLModelManager().removeListener(modelManagerListener);
        getOWLModelManager().getOWLEntityRenderer().removeListener(entityRendererListener);
        disposeView();
    }


    public abstract void disposeView();


    protected void disableRegisteredActions() {
        for (OWLSelectionViewAction action : registeredActions) {
            action.setEnabled(false);
        }
    }


    protected void updateRegisteredActions() {
        for (OWLSelectionViewAction action : registeredActions) {
            action.updateState();
        }
    }


    protected void updateViewContentAndHeader() {
        if (!isShowing()) {
            needsRefresh = true;
            return;
        }
        needsRefresh = false;
        if (isPinned() && initialUpdatePerformed) {
            return;
        }
        initialUpdatePerformed = true;
        if (isSynchronizing()){
            lastDisplayedObject = updateView();
            updateHeader(lastDisplayedObject);
        }
    }


    protected void updateHeader(OWLObject object) {
        // Set the label in the header to reflect the entity that the view
        // is displaying
        if (object != null) {
            updateRegisteredActions();
            getView().setHeaderText(getOWLModelManager().getRendering(object));
        }
        else {
            // Not displaying an entity, so disable all actions
            disableRegisteredActions();
            getView().setHeaderText("");
        }
    }


    /**
     * Request that the view is updated to display the current selection.
     * @return The OWLEntity that the view is displaying.  This
     *         list is typically used to generate the view header text to give the
     *         user an indication of what the view is displaying.
     */
    protected abstract OWLObject updateView();


    protected boolean isOWLClassView() {
        return canNavigate(ProtegeProperties.CLASS_VIEW_CATEGORY);
    }


    protected boolean isOWLObjectPropertyView() {
        return canNavigate(ProtegeProperties.OBJECT_PROPERTY_VIEW_CATEGORY);
    }


    protected boolean isOWLDataPropertyView() {
        return canNavigate(ProtegeProperties.DATA_PROPERTY_VIEW_CATEGORY);
    }


    protected boolean isOWLIndividualView() {
        return canNavigate(ProtegeProperties.INDIVIDUAL_VIEW_CATEGORY);
    }


    protected boolean isOWLAnnotationPropertyView() {
        return canNavigate(ProtegeProperties.ANNOTATION_PROPERTY_VIEW_CATEGORY);
    }


    protected boolean isOWLDatatypeView() {
        return canNavigate(ProtegeProperties.DATATYPE_VIEW_CATEGORY);
    }


    // by default, asks the plugin whether the entity can be displayed
    private boolean canNavigate(String type){
        ViewComponentPlugin plugin = getWorkspace().getViewManager().getViewComponentPlugin(getView().getId());
        return plugin != null && plugin.getNavigates().contains(ProtegeProperties.getInstance().getProperty(type));
    }


    public final boolean canShowEntity(OWLEntity owlEntity){
        return owlEntity != null && new AcceptableEntityVisitor().canShowEntity(owlEntity);
    }


    class AcceptableEntityVisitor implements OWLEntityVisitor {
        boolean result;

        public boolean canShowEntity(OWLEntity owlEntity){
            result = false;
            owlEntity.accept(this);
            return result;
        }

        public void visit(OWLClass owlClass) {
            result = isOWLClassView();
        }


        public void visit(OWLObjectProperty owlObjectProperty) {
            result = isOWLObjectPropertyView();
        }


        public void visit(OWLDataProperty owlDataProperty) {
            result = isOWLDataPropertyView();
        }


        public void visit(OWLNamedIndividual owlIndividual) {
            result = isOWLIndividualView();
        }


        public void visit(OWLDatatype owlDatatype) {
            result = isOWLDatatypeView();
        }


        public void visit(OWLAnnotationProperty owlAnnotationProperty) {
            result = isOWLAnnotationPropertyView();
        }
    }
}
