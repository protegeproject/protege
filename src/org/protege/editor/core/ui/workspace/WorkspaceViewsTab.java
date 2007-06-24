package org.protege.editor.core.ui.workspace;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.view.ViewsPaneMemento;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 6, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class WorkspaceViewsTab extends WorkspaceTab {

    public static final Logger logger = Logger.getLogger(WorkspaceViewsTab.class);

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
