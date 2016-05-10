package org.protege.editor.core.ui.split;

import javax.swing.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 19, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * This is a customisation of a <code>JSplitPane</code>
 * that modifies the split pane dividers and borders.

 * The split pane UI is replaced so that the divider
 * and borders are "flat".  The split pane also changes
 * the size of the splitter depending on the number of
 * components contained in the split pane.  If there is
 * only one component then the splitter is set to be a
 * zero width.
 */
public class ViewSplitPane extends JSplitPane {

        private static final int DEFAULT_DIVIDER_SIZE = 6;


    public ViewSplitPane(int orientation) {
        super(orientation);
        setUI(new ViewSplitPaneUI());
        setBorder(null);
        addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                updateDividerSize();
            }


            public void componentRemoved(ContainerEvent e) {
                updateDividerSize();
            }
        });
        updateDividerSize();
    }


    public void updateUI() {
//        super.updateUI();
    }


    public int getMinimumDividerLocation() {
        return 0;
    }


    public int getMaximumDividerLocation() {
        return Integer.MAX_VALUE;
    }


    private void updateDividerSize() {
        if (getComponentCount() > 2) {
            setDividerSize(DEFAULT_DIVIDER_SIZE);
        }
        else {
            setDividerSize(0);
        }
    }
}
