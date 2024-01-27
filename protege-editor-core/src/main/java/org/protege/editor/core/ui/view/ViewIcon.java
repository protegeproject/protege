package org.protege.editor.core.ui.view;

import javax.swing.Icon;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public abstract class ViewIcon implements Icon {

    private int size = 12;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Returns the icon's width.
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return getSize();
    }

    /**
     * Returns the icon's height.
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return getSize();
    }
}
