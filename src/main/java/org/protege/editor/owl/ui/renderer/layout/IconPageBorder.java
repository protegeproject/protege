package org.protege.editor.owl.ui.renderer.layout;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 07/12/2011
 */
public class IconPageBorder extends PageObjectBorder {

    private Icon icon;

    private int iconPadding;

    public IconPageBorder(Icon icon, int iconPadding) {
        super(0, 0, icon.getIconWidth() + iconPadding, 0);
        this.iconPadding = iconPadding;
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public int getIconPadding() {
        return iconPadding;
    }

    @Override
    protected void drawBorder(Graphics2D g2, int borderWidth, int borderHeight, PageObject pageObject) {
        icon.paintIcon(null, g2, 0, 0);
    }
}
