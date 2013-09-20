package org.protege.editor.owl.ui.renderer.layout;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 07/11/2011
 * <p>
 *     Represents a rectangle on a page that contains a link.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class LinkBox extends PageObject {

    private Link link;

    public LinkBox(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public LinkBox translate(int dx, int dy) {
        Rectangle boundsCopy = new Rectangle(getX(), getY(), getWidth(), getHeight());
        boundsCopy.translate(dx, dy);
        LinkBox translatedLink = new LinkBox(link);
        translatedLink.setX(boundsCopy.x);
        translatedLink.setY(boundsCopy.y);
        translatedLink.setWidth(boundsCopy.width);
        translatedLink.setHeight(boundsCopy.height);
        return translatedLink;
    }

    public void layout(FontRenderContext fontRenderContext) {
    }

    @Override
    protected void doMouseReleased(MouseEvent event) {
        link.activate(event.getComponent(), event);
    }

    /**
     * Paints the content area of this page object.
     * @param g2 A graphics object which should be used to paint the content.  The origin will be the top left corner
     * of the page content.
     */
    @Override
    protected void paintContent(Graphics2D g2) {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LinkBox(");
        sb.append(link);
        sb.append(" ");
        sb.append(getBounds());
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected void doHandleMouseMoved(MouseEvent event) {
    }

}
