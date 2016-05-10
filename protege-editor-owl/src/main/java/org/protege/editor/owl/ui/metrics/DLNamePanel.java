package org.protege.editor.owl.ui.metrics;

import org.semanticweb.owlapi.util.DLExpressivityChecker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLNamePanel extends JPanel {

    private List<Icon> icons;

    private int maxHeight = 0;

    private int maxWidth = 0;


    private static final int TRACKING_ADJUSTMENT = -7;


    public DLNamePanel() {
        icons = new ArrayList<>();
    }


    public void setConstructs(List<DLExpressivityChecker.Construct> constructs) {
        icons.clear();
        for (DLExpressivityChecker.Construct constuct : constructs) {
            Icon curIcon = ExpressivityIcons.getIcon(constuct);
            if (curIcon != null) {
                icons.add(curIcon);
                if (curIcon.getIconHeight() > maxHeight) {
                    maxHeight = curIcon.getIconHeight();
                }
                maxWidth += curIcon.getIconWidth() + TRACKING_ADJUSTMENT;
            }
        }
        repaint(0, 0, getWidth(), getHeight());
    }


    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        return new Dimension(maxWidth + insets.left + insets.right, maxHeight + insets.top + insets.bottom);
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets insets = getInsets();
        g.translate(insets.left, insets.top);
        Graphics2D g2 = (Graphics2D) g;
        int x = 0;
        for (Icon icon : icons) {
            int y = maxHeight - icon.getIconHeight();
            icon.paintIcon(this, g2, x, y);
            x += icon.getIconWidth() + TRACKING_ADJUSTMENT;
        }

        g.translate(-insets.left, -insets.top);
    }
}
