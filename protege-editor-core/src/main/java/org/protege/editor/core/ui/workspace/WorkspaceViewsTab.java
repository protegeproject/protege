package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 6, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * Represents a workspace tab that hosts a views panel.
 */
public class WorkspaceViewsTab extends WorkspaceTab implements Resettable {


    private final Logger logger = LoggerFactory.getLogger(WorkspaceViewsTab.class);

    private ViewsPane viewsPane;


    public ViewsPane getViewsPane() {
        return viewsPane;
    }


    public void initialise() {
        setLayout(new BorderLayout());
        ViewsPaneMemento memento = new ViewsPaneMemento(this);
        viewsPane = new ViewsPane(getWorkspace(), memento);
        add(viewsPane, BorderLayout.CENTER);
        getWorkspace().getViewManager().registerViews(this);
    }


    public void reset() {

        for (View view : viewsPane.getViews()) {
            if (view.getViewComponent() != null) {
                if (view.getViewComponent() instanceof Resettable) {
                    ((Resettable) view.getViewComponent()).reset();
                }
            }
        }
        getWorkspace().getViewManager().unregisterViews(this);
        remove(viewsPane);
        viewsPane.dispose();
        ViewsPaneMemento memento = new ViewsPaneMemento(this);
        memento.setForceReset(true);
        viewsPane = new ViewsPane(getWorkspace(), memento);
        add(viewsPane, BorderLayout.CENTER);
        getWorkspace().getViewManager().registerViews(this);
    }

    public void reset(String serialisation) {
        getWorkspace().getViewManager().unregisterViews(this);
        viewsPane.storeViewLayout(serialisation);
        viewsPane.dispose();
        remove(viewsPane);
        viewsPane = new ViewsPane(getWorkspace(), new ViewsPaneMemento(this));
        add(viewsPane, BorderLayout.CENTER);
        getWorkspace().getViewManager().registerViews(this);
        validate();

    }


    protected void setTopComponent(JComponent component) {
        add(component, BorderLayout.NORTH);
    }


    protected void setLeftComponent(JComponent component) {
        add(component, BorderLayout.WEST);
    }


    protected void setRightComponent(JComponent component) {
        add(component, BorderLayout.EAST);
    }


    protected void setBottomComponent(JComponent component) {
        add(component, BorderLayout.SOUTH);
    }


    public void bringViewToFront(String viewId) {
        viewsPane.bringViewToFront(viewId);
    }


    public void save() {
        super.save();
        viewsPane.saveViews();
    }


    public void dispose() {
        // Save the current views
        viewsPane.saveViews();
        // Dispose of the views
        logger.debug("Disposing of views");
        viewsPane.dispose();
    }
}
