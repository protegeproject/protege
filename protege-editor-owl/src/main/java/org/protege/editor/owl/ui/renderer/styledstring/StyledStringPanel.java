package org.protege.editor.owl.ui.renderer.styledstring;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 */
public class StyledStringPanel extends JPanel {

    public static final int DEFAULT_ICON_PADDING = 2;

    private StyledString styledString = new StyledString();

    private Icon icon = null;

    public StyledStringPanel() {
        setOpaque(true);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public StyledStringPanel(StyledString styledString) {
        setOpaque(true);
        this.styledString = styledString;
    }

    public void setStyledString(StyledString styledString) {
        setOpaque(true);
        this.styledString = styledString;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Insets insets = getInsets();
        if (insets != null) {
            g.translate(insets.left, insets.top);
        }
        g2.setFont(getFont());
        Shape clip = g2.getClip();
        g2.setColor(getBackground());
        g2.fill(clip);

        if (icon != null) {
            icon.paintIcon(this, g2, 0, 0);
            g.translate(icon.getIconWidth() + DEFAULT_ICON_PADDING, 0);
        }
        g2.setColor(getForeground());
        styledString.draw(g2, 0, 0);

        if (icon != null) {
            g.translate(-icon.getIconWidth() - DEFAULT_ICON_PADDING, 0);
        }

        if (insets != null) {
            g.translate(-insets.left, -insets.top);
        }
    }
}
