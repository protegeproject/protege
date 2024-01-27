package org.protege.editor.owl.ui.renderer.layout;

import java.awt.Graphics2D;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 07/12/2011
 * <p>
 *     Represents a border of a page object.  A border provides (possibly) empty insets which get painted according to
 *     the style of the border.
 * </p>
 */
public abstract class PageObjectBorder {
 
    private int leftInset = 0;

    private int rightInset = 0;

    private int topInset = 0;
    
    private int bottomInset = 0;

    protected PageObjectBorder(int topInset, int bottomInset, int leftInset, int rightInset) {
        this.topInset = topInset;
        this.bottomInset = bottomInset;
        this.leftInset = leftInset;
        this.rightInset = rightInset;
    }

    /**
     * Gets the left inset.  This is immutable.
     * @return The left inset.
     */
    public int getLeftInset() {
        return leftInset;
    }


    /**
     * Gets the right inset.  This is immutable.
     * @return The right inset.
     */
    public int getRightInset() {
        return rightInset;
    }


    /**
     * Gets the top inset.  This is immutable.
     * @return The top inset.
     */
    public int getTopInset() {
        return topInset;
    }

    /**
     * Gets the bottom inset.  This is immutable.
     * @return The bottom inset.
     */
    public int getBottomInset() {
        return bottomInset;
    }
    
    protected abstract void drawBorder(Graphics2D g2, int borderWidth, int borderHeight, PageObject pageObject);
}
