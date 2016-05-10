package org.protege.editor.core.ui.view;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.tabbedpane.ViewTabbedPane;
import org.protege.editor.core.ui.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Collection;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 7, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewHolder extends JComponent implements Disposable {

    private static final long serialVersionUID = 5192903791732587041L;

    private JSplitPane splitPane;

    private String loc;

    private JTabbedPane tabbedHolder;

    public ViewHolder(String id, String loc, JSplitPane sp) {
        this.loc = loc;
        this.splitPane = sp;
        setLayout(new BorderLayout());
        //setMinimumSize(new Dimension(5, 5));
        addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                if (getComponentCount() == 1) {
                    splitPane.add(ViewHolder.this, ViewHolder.this.loc);
                }
            }


            public void componentRemoved(ContainerEvent e) {
                if (getComponentCount() == 0) {
                    splitPane.remove(ViewHolder.this);
                }
            }
        });
        createHolder();
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                                                     BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }


    private void createHolder() {
        // We display the views in a customised tab pane.
        // This means that if there are multiple views in
        // a particular area they get stacked and are accessible
        // to the user.
        tabbedHolder = new ViewTabbedPane();
        tabbedHolder.setTabPlacement(SwingConstants.BOTTOM);
        // Set the minimum size of the holder so that the
        // split panes can be dragged around easily.  We don't
        // want to set the size to zero, because this allows the
        // contents of the split pane to be completely hidden,
        // which could be confusing for the user.
        // tabbedHolder.setMinimumSize(new Dimension(10, 10));

        tabbedHolder.addContainerListener(new ContainerListener() {
            public void componentAdded(ContainerEvent e) {
                if (tabbedHolder.getComponentCount() == 1) {
                    ViewHolder.this.add(tabbedHolder);
                }
            }


            public void componentRemoved(ContainerEvent e) {
                if (tabbedHolder.getComponentCount() == 0) {
                    ViewHolder.this.remove(tabbedHolder);
                }
            }
        });
    }


    /**
     * Adds a <code>View</code> to the holder.
     * @param view The <code>View</code> to be added.
     */
    public void addView(View view) {
        ViewContainer viewContainer = new ViewContainer(view);
        tabbedHolder.addTab(view.getViewName(), viewContainer);
        tabbedHolder.setSelectedComponent(viewContainer);
    }


    /**
     * Gets hold of a <code>View</code> that is contained
     * within the holder, that has a specified id.
     * @param id The id of the <code>View</code> to be retrieved.
     * @return The <code>View</code>, or <code>null</code>
     *         if the holder doesn't contain a <code>View</code> with
     *         the specified id.
     */
    public View getView(String id) {
        return getView(id, this);
    }


    private View getView(String id, Component c) {
    	Collection<View> views = UIUtil.getComponentsExtending(c, View.class);
    	for (View view : views) {
    		if (view.getId().equals(id)) {
    			return view;
    		}
    	}
    	return null;
    }


    /**
     * Tests to see whether the holder contains a view
     * with a particular id.
     * @param id The id of the view to test for.
     * @return <code>true</code> if the holder contains the view,
     *         or <code>false</code> if the holder does not contain the
     *         view.
     */
    public boolean containsView(String id) {
        return getView(id) != null;
    }
    
    public void dispose() {
    	for (ViewContainer container : UIUtil.getComponentsExtending(this, ViewContainer.class)) {
    		container.dispose();
    	}
	}
}
