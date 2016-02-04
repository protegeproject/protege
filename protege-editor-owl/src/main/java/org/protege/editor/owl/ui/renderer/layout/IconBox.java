package org.protege.editor.owl.ui.renderer.layout;

import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.*;
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

    public static final Font FONT = new Font("sans-serif", Font.PLAIN, 20);

    private Icon image;

    private int maxHeight = -1;

    private LinkBox linkBox = null;

    private static final String warningIcon = "\u26A0";

    /**
     * Constructs an IconBox.
     * @param image The image that should be drawn in the box.
     */
    public IconBox(Icon image) {
        this.image = image;
    }

    public IconBox(Icon image, Link link) {
        this.image = image;
        this.linkBox =  new LinkBox(link);
        add(linkBox);
    }

    public void layout(FontRenderContext fontRenderContext) {
        if(isErrored()) {
            setWidth(26);
            setHeight(26);
        }
        else if (maxHeight < 0) {
            setHeight(image.getIconHeight());
            setWidth(image.getIconWidth());
        }
        else {
            setHeight(maxHeight);
            double scaleFactor = maxHeight * 1.0 / image.getIconHeight();
            setWidth((int)(scaleFactor * image.getIconWidth()));
        }
        if(linkBox != null) {
            linkBox.setX(0);
            linkBox.setY(0);
            linkBox.setWidth(getWidth());
            linkBox.setHeight(getHeight());
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * Paints the content area of this page object.
     * @param g2 A graphics object which should be used to paint the content.  The origin will be the top left corner
     * of the page content.
     */
    @Override
    protected void paintContent(Graphics2D g2) {
        boolean errored = isErrored();
        if (!errored) {
            int imageHeight = image.getIconHeight();
            if (getHeight() != imageHeight) {
                double scaleFactor = getHeight() * 1.0 / imageHeight;
                g2.scale(scaleFactor, scaleFactor);
            }
            image.paintIcon(null, g2, 0, 0);
            g2.scale(1, 1);
        }
        else {
            g2.setFont(FONT);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.ORANGE);
            int x = (int) (getWidth() / 2 - fm.getStringBounds(warningIcon, g2).getWidth() / 2);
            int ascent = fm.getAscent();
            int descent = fm.getDescent();
            int y = getHeight() / 2 - ((ascent + descent) / 2) + ascent;
            g2.drawString(warningIcon, x, y);
            g2.drawRect(1, 1, getWidth() - 1, getHeight() - 2);
        }

    }

    private boolean isErrored() {
        return !(image instanceof ImageIcon) || ((ImageIcon) image).getImageLoadStatus() == MediaTracker.ERRORED;
    }

}
