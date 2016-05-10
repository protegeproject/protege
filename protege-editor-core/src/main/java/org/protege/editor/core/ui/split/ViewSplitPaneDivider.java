package org.protege.editor.core.ui.split;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 19, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewSplitPaneDivider extends BasicSplitPaneDivider {

        // Debugging can be turned on (requires recompile) to
    // highlight the divider position and size
    public static final boolean DEBUG = false;


    public ViewSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
        setLayout(new BorderLayout());
        add(new DividerComponent());
    }


    /**
     * Paints the divider.
     */
    public void paint(Graphics g) {
        // We just want to paint the child components - i.e.
        // the divider panel.  This ensures that look & feel
        // specific rendering, such as grips/bumps are not
        // shown.
        paintComponents(g);
    }


    private static class DividerComponent extends JPanel {


        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (DEBUG) {
                Color oldColor = g.getColor();
                int width = getWidth();
                int height = getHeight();
                g.setColor(Color.MAGENTA);
                if (height > width) {
                    g.drawLine(width / 2, 2, width / 2, height - 2);
                }
                else {
                    g.drawLine(2, height / 2, width - 2, height / 2);
                }
                g.setColor(oldColor);
            }
        }
    }
}
