package org.protege.editor.core.ui.view;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JSplitPane;

import org.protege.editor.core.ui.split.ViewSplitPane;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 6, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class NestedViewSplitPane extends ViewSplitPane {


    /**
     * 
     */
    private static final long serialVersionUID = -6670708338163244319L;

    private static final int ZERO_CONTENT_COUNT = 1;

    private JSplitPane parentComponent;

    private String locationInParent;


    public NestedViewSplitPane(JSplitPane parentSplitPane, String locationInParent, int orientation) {
        super(orientation);
        this.parentComponent = parentSplitPane;
        this.locationInParent = locationInParent;
        addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                processComponentAdded();
            }


            public void componentRemoved(ContainerEvent e) {
                processComponentRemoved();
            }
        });
    }


    private void processComponentAdded() {
        if (getComponentCount() == ZERO_CONTENT_COUNT + 1) {
            if (locationInParent.equals(JSplitPane.LEFT) || locationInParent.equals(JSplitPane.TOP)) {
                parentComponent.setLeftComponent(this);
            }
            else {
                parentComponent.setBottomComponent(this);
            }
        }
    }


    private void processComponentRemoved() {
        if (getComponentCount() == ZERO_CONTENT_COUNT) {
            if (locationInParent.equals(JSplitPane.LEFT) || locationInParent.equals(JSplitPane.TOP)) {
                parentComponent.setTopComponent(null);
            }
            else {
                parentComponent.setBottomComponent(null);
            }
        }
    }
}
