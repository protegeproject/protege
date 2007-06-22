package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.FilteringOWLSelectionModelListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererListener;
import org.semanticweb.owl.model.*;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
public abstract class AbstractOWLSelectionViewComponent extends AbstractOWLViewComponent {

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
        listener = new FilteringOWLSelectionModelListener(getOWLEditorKit()) {


            public void visit(OWLClass cls) {
                if (isOWLClassView()) {
                    updateViewContentAndHeader();
                }
            }


            public void visit(OWLObjectProperty property) {
                if (isOWLObjectPropertyView()) {
                    updateViewContentAndHeader();
                }
            }


            public void visit(OWLDataProperty property) {
                if (isOWLDataPropertyView()) {
                    updateViewContentAndHeader();
                }
            }


            public void visit(OWLIndividual individual) {
                if (isOWLIndividualView()) {
                    updateViewContentAndHeader();
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
        addHierarchyListener(hierarchyListener);
        getOWLModelManager().addListener(modelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                    getOWLModelManager().getOWLEntityRenderer().addListener(entityRendererListener);
                }
            }
        });
        getOWLModelManager().getOWLEntityRenderer().addListener(entityRendererListener);
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
        initialiseView();
        updateViewContentAndHeader();
    }


    /**
     * A convenience method that sets the specified entity to be the
     * selected entity in the <code>OWLSelectionModel</code>.
     */
    protected void setSelectedEntity(OWLEntity owlEntity) {
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
        lastDisplayedObject = updateView();
        updateHeader(lastDisplayedObject);
    }


    protected void updateHeader(OWLObject object) {
        // Set the label in the header to reflect the entity that the view
        // is displaying
        String label = "";
        if (object != null) {
            label += getOWLModelManager().getOWLObjectRenderer().render(object,
                                                                        getOWLModelManager().getOWLEntityRenderer());
            updateRegisteredActions();
        }
        else {
            // Not displaying an entity, so disable all actions
            disableRegisteredActions();
        }
        getView().setHeaderText(label);
    }


    /**
     * Request that the view is updated to display the current selection.
     * @return The OWLEntity that the view is displaying.  This
     *         list is typically used to generate the view header text to give the
     *         user an indication of what the view is displaying.
     */
    protected abstract OWLObject updateView();


    protected boolean isOWLClassView() {
        return false;
    }


    protected boolean isOWLObjectPropertyView() {
        return false;
    }


    protected boolean isOWLDataPropertyView() {
        return false;
    }


    protected boolean isOWLIndividualView() {
        return false;
    }
}
