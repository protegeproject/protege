package org.protege.editor.owl.ui.renderer.layout;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 23/11/2011
 * <p>
 *     Represents a bullet in a bulleted list.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class BulletIcon implements Icon {

    public static final int DEFAULT_BULLET_DIMENSION = 6;

    private int bulletDimension = DEFAULT_BULLET_DIMENSION;


    public BulletIcon() {
        this(DEFAULT_BULLET_DIMENSION);
    }

    public BulletIcon(int bulletDimension) {
        this.bulletDimension = bulletDimension;
    }


    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.translate(x, y);
        g.fillOval(0, 0, bulletDimension, bulletDimension);
        g.translate(-x, -y);
    }

    /**
     * Returns the icon's width.
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return bulletDimension;
    }

    /**
     * Returns the icon's height.
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return bulletDimension;
    }
}
