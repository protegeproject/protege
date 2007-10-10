package org.protege.editor.core.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.protege.editor.core.ui.split.ViewSplitPane;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 20, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>ViewContainer</code> holds one component
 * which is either a <code>View</code> or a split pane
 * which contains two <code>View</code>s.
 */
public class ViewContainer extends JComponent {

    // Debugging can be turned on (requires recompile)
    // to show borders around the containers
    public static final boolean DEBUG = false;


    public ViewContainer(View view) {
        setLayout(new BorderLayout());
        add(view);
        if (DEBUG) {
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3),
                                                         BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED)));
        }
    }


    public ViewContainer(ViewContainer left, ViewContainer right, int orientation) {
        setLayout(new BorderLayout());
        JSplitPane sp = createSplitPane(orientation);
        add(sp);
        sp.setLeftComponent(left);
        sp.setRightComponent(right);
    }


    public void splitHorizontally(View rightView) {
        rightView.setSyncronizing(false);
        JSplitPane sp = createSplitPane(JSplitPane.VERTICAL_SPLIT);
        View view = (View) getComponent(0);
        remove(view);
        add(sp);
        validate();
        sp.setTopComponent(new ViewContainer(view));
        sp.setBottomComponent(new ViewContainer(rightView));
        sp.setDividerLocation(sp.getHeight() / 2);
    }


    public void splitVertically(View bottomView) {
        bottomView.setSyncronizing(false);
        JSplitPane sp = createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        View view = (View) getComponent(0);
        remove(view);
        add(sp);
        validate();
        sp.setLeftComponent(new ViewContainer(view));
        sp.setRightComponent(new ViewContainer(bottomView));
        sp.setDividerLocation(sp.getWidth() / 2);
    }


    private ViewSplitPane createSplitPane(int orientation) {
        ViewSplitPane sp = new TabViewContainerSplitPane(orientation);
        sp.setResizeWeight(0.5);
        return sp;
    }


    public void closeView(View view) {
        // Remove the tab view, which should belong to
        // this container.
        remove(view);
        // Remove this from the parent, as we're just an empty container now
        Container parent = getParent();
        parent.remove(this);
        // If the parent is a split pane, then collapse
        // the split pane
        if (parent instanceof TabViewContainerSplitPane) {
            collapseSplitPane((TabViewContainerSplitPane) parent);
        }
        else {
            parent.validate();
        }
    }


    public void collapseSplitPane(TabViewContainerSplitPane sp) {
        Component remainingComp = sp.getLeftComponent();
        if (remainingComp == null) {
            remainingComp = sp.getRightComponent();
        }
        sp.remove(remainingComp);
        Container parent = sp.getParent();
        parent.remove(sp);
        parent.add(remainingComp);
        if (remainingComp instanceof ViewContainer) {
            collapseTabViewContainer((ViewContainer) remainingComp);
        }
        else {
            parent.validate();
        }
    }


    public void collapseTabViewContainer(ViewContainer viewContainer) {
        Container parent = viewContainer.getParent();
        if (parent instanceof ViewContainer) {
            parent.remove(viewContainer);
            parent.add(viewContainer.getComponent(0));
            parent.validate();
        }
    }


    private static class TabViewContainerSplitPane extends ViewSplitPane {

        public TabViewContainerSplitPane(int orientation) {
            super(orientation);
        }
    }


    /**
     * If the minimum size has been set to a non-<code>null</code> value
     * just returns it.  If the UI delegate's <code>getMinimumSize</code>
     * method returns a non-<code>null</code> value then return that; otherwise
     * defer to the component's layout manager.
     * @return the value of the <code>minimumSize</code> property
     * @see #setMinimumSize
     * @see javax.swing.plaf.ComponentUI
     */
    public Dimension getMinimumSize() {
        return new Dimension(10, 10);
    }
}
