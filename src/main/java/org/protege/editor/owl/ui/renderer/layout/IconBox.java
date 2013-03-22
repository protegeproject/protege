package org.protege.editor.owl.ui.renderer.layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 16/11/2011
 * <p>
 *     A Page layout box that contains an image.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class IconBox extends PageObject {

    private Icon image;

    /**
     * Constructs an IconBox.
     * @param image The image that should be drawn in the box.
     */
    public IconBox(Icon image) {
        this.image = image;
    }

    public void layout(FontRenderContext fontRenderContext) {
        setHeight(image.getIconHeight());
    }

    /**
     * Paints the content area of this page object.
     * @param g2 A graphics object which should be used to paint the content.  The origin will be the top left corner
     * of the page content.
     */
    @Override
    protected void paintContent(Graphics2D g2) {
        image.paintIcon(null, g2, 0 , 0);
    }

}
