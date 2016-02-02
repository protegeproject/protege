package org.protege.editor.owl.ui.renderer.layout;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 11/11/2011
 * <p>
 * The base class for objects that can appear on a page and the page itself.  Page objects can have another page
 * object as their parent and have a location and size.
 * </p>
 */
public abstract class PageObject {

    private PageObject parent;

    private List<PageObject> children = new ArrayList<>();

    private Rectangle bounds = new Rectangle();

    private Insets marginInsets = new Insets(0, 0, 0, 0);

    private Insets paddingInsets = new Insets(0, 0, 0, 0);

    private boolean mouseOver = false;

    private double opacity = 1.0;

    private PageObjectBorder pageObjectBorder = EmptyPageObjectBorder.getEmptyPageObjectBorder();

    /**
     * Adds a child to the page object.  The parent of the child will become this page object. If the specified child
     * is alread a child of this page then nothing will happen.
     * @param child The child to be added.
     */
    public void add(PageObject child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
            invalidateLayout();
        }
    }

    protected void setParent(PageObject pageObject) {
        parent = pageObject;
    }

    /**
     * Gets the PageObjects that are children of this PageObject.
     * @return A list of children that belong to this PageObject.
     */
    public List<PageObject> getChildren() {
        return new ArrayList<>(children);
    }

    public void clear() {
        for (Iterator<? extends PageObject> it = children.iterator(); it.hasNext(); ) {
            PageObject pageObject = it.next();
            it.remove();
            pageObject.parent = null;
        }
    }

    /**
     * Gets the parent {@link PageObject} of this {@link PageObject}.
     * @return The parent {@link PageObject} or <code>null</code> if this {@link PageObject} has not yet been added to
     *         a parent.
     */
    public PageObject getParent() {
        return parent;
    }

    /**
     * Gets the {@link Page} that this {@link PageObject} belongs to.  This may be <code>null</code> if this {@link PageObject}
     * has not been added as the child of another {@link PageObject}.
     * @return The {@link Page} that this page object belongs to.  May be <code>null</code>.
     */
    public Page getPage() {
        if (this instanceof Page) {
            return (Page) this;
        }
        PageObject parent = getParent();
        if (parent != null) {
            return parent.getPage();
        }
        return null;
    }

    /**
     * Sets the bounds of this page object.  The bounds are set relative to the parent of this {@link PageObject}
     * @param rectangle The bounds.  A copy will be made of this rectangle so that altering the location and size of the
     * rectangle will not alter the bounds of this page object.
     */
    public void setBounds(Rectangle rectangle) {
        bounds.setBounds(rectangle);
    }


    /**
     * Gets the x-axis, or horizontal, location of this {@link PageObject} relative to its parent {@link PageObject}.
     * This location may be negative.
     * @return The x-axis location of this page object relative to its parent.
     */
    public int getX() {
        return bounds.x;
    }

    /**
     * Sets the x-axis, or horizontal, location of this {@link PageObject} relative to its parent {@link PageObject}.
     * This location may be negative.
     * @param x The x-axis location to be set.
     */
    public void setX(int x) {
        if (x != bounds.x) {
            this.bounds.x = x;
        }
    }

    /**
     * Gets the y-axis, or vertical, location of this {@link PageObject} relative to its parent {@link PageObject}.
     * This location may be negative.
     * @return The y-axis location of this page object relative to its parent.
     */
    public int getY() {
        return bounds.y;
    }

    /**
     * Sets the y-axis, or vertical, location of this {@link PageObject} relative to its parent {@link PageObject}.
     * This location may be negative.
     * @param y The y-axis location to be set.
     */
    public void setY(int y) {
        if (bounds.y != y) {
            bounds.y = y;
        }
    }

    /**
     * Gets the left margin width.
     * @return The left margin width which will be greater or equal to zero.
     */
    public int getMarginLeft() {
        return marginInsets.left;
    }

    /**
     * Sets the left margin width.
     * @param marginLeft The margin width to set.
     * @throws IllegalArgumentException if the width is less than zero.
     */
    public void setMarginLeft(int marginLeft) {
        if (marginLeft < 0) {
            throw new IllegalArgumentException("marginLeft < 0");
        }
        if (this.marginInsets.left != marginLeft) {
            this.marginInsets.left = marginLeft;
            invalidateLayout();
        }
    }


    /**
     * Gets the left margin width.
     * @return The left margin width which will be greater or equal to zero.
     */
    public int getMarginRight() {
        return marginInsets.right;
    }

    /**
     * Sets the right margin width.
     * @param marginRight The margin width to set.
     * @throws IllegalArgumentException if the width is less than zero.
     */
    public void setMarginRight(int marginRight) {
        if (marginRight < 0) {
            throw new IllegalArgumentException("marginRight < 0");
        }
        if (this.marginInsets.right != marginRight) {
            this.marginInsets.right = marginRight;
            invalidateLayout();
        }
    }

    /**
     * Gets the top margin height.
     * @return The top margin height which will be greater or equal to zero.
     */
    public int getMarginTop() {
        return marginInsets.top;
    }

    /**
     * Sets the top margin height.
     * @param marginTop The margin height to set.
     * @throws IllegalArgumentException if the height is less than zero.
     */
    public void setMarginTop(int marginTop) {
        if (marginTop < 0) {
            throw new IllegalArgumentException("marginTop < 0");
        }
        if (this.marginInsets.top != marginTop) {
            marginInsets.top = marginTop;
            invalidateLayout();
        }
    }

    /**
     * Gets the bottom margin height.
     * @return The bottom margin height which will be greater or equal to zero.
     */
    public int getMarginBottom() {
        return marginInsets.bottom;
    }

    /**
     * Sets the top margin height.
     * @param marginBottom The margin height to set.
     * @throws IllegalArgumentException if the height is less than zero.
     */
    public void setMarginBottom(int marginBottom) {
        if (marginBottom < 0) {
            throw new IllegalArgumentException("marginBottom < 0");
        }
        if (this.marginInsets.bottom != marginBottom) {
            marginInsets.bottom = marginBottom;
            invalidateLayout();
        }
    }

    /**
     * A shortcut for setting the widths of all margins.
     * Sets the top, left, right and bottoms margins to the specified width.
     * @param margin The size of the margins.
     * @throws IllegalArgumentException is the specified size is less than zero.
     */
    public void setMargin(int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("margin < 0");
        }
        setMarginLeft(margin);
        setMarginRight(margin);
        setMarginTop(margin);
        setMarginBottom(margin);
    }

    public void setBorder(PageObjectBorder pageObjectBorder) {
        this.pageObjectBorder = pageObjectBorder;
        invalidateLayout();
    }

    public int getBorderLeft() {
        return pageObjectBorder.getLeftInset();
    }

    public int getBorderRight() {
        return pageObjectBorder.getRightInset();
    }

    public int getBorderTop() {
        return pageObjectBorder.getTopInset();
    }

    public int getBorderBottom() {
        return pageObjectBorder.getBottomInset();
    }

    public int getPaddingLeft() {
        return paddingInsets.left;
    }

    public int getPaddingRight() {
        return paddingInsets.right;
    }

    public int getPaddingTop() {
        return paddingInsets.top;
    }

    public int getPaddingBottom() {
        return paddingInsets.bottom;
    }

    /**
     * Gets the width of the this page object's border.  The border is calculated as the width of this page object
     * minus the width of the left margin minus the width of the right margin.
     * @return The border width
     */
    public int getBorderWidth() {
        return getWidth() - getMarginLeft() - getMarginRight();
    }

    /**
     * Gets the height of the this page object's border.  The border is calculated as the height of this page object
     * minus the height of the top margin minus the height of the bottom margin.
     * @return The border height
     */
    public int getBorderHeight() {
        return getHeight() - getMarginTop() - getMarginBottom();
    }

    /**
     * Gets the width of the left insets of this page object.  The insets are made up of the margin plus any extra padding
     * and or border width.
     * @return  The width of the left insets.
     */
    public int getInsetsLeft() {
        return marginInsets.left + pageObjectBorder.getLeftInset() + paddingInsets.left;
    }

    /**
     * Gets the width of the right insets of this page object.  The insets are made up of the margin plus any extra padding
     * and or border width.
     * @return  The width of the right insets.
     */
    public int getInsetsRight() {
        return marginInsets.right + pageObjectBorder.getRightInset() + paddingInsets.right;
    }

    /**
     * Gets the height of the top insets of this page object.  The insets are made up of the margin plus any extra padding
     * and or border height.
     * @return  The height of the top insets.
     */
    public int getInsetsTop() {
        return marginInsets.top + pageObjectBorder.getTopInset() + paddingInsets.top;
    }

    /**
     * Gets the height of the bottom insets of this page object.  The insets are made up of the margin plus any extra padding
     * and or border height.
     * @return  The height of the bottom insets.
     */
    public int getInsetsBottom() {
        return marginInsets.bottom + pageObjectBorder.getBottomInset() + paddingInsets.bottom;
    }

    /**
     * Gets the width of this page object.
     * @return The width.  The width of page object that aren't themselves pages is determined when the page that they
     * belong to is laid out.
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Sets the width of this page object.
     * @param width The width.
     */
    public void setWidth(int width) {
        if (bounds.width != width) {
            bounds.width = width;
        }
    }

    /**
     * Gets the height of this page object.
     * @return The height of this page object.  The height is usually determined when the page object is laid out.
     */
    final public int getHeight() {
        return bounds.height;
    }

    /**
     * Sets the height of this page object.
     * @param height The height.
     */
    public void setHeight(int height) {
        if (bounds.height != height) {
            bounds.height = height;
        }
    }

    /**
     * Gets the content width, which is equal to the width of this page object minus the width of the left inset (given
     * by {@link #getInsetsLeft()}) minus the width of the right inset (given by {@link #getInsetsRight()}).
     * @return The content width.
     */
    public int getContentWidth() {
        return getWidth() - getInsetsLeft() - getInsetsRight();
    }

    /**
     * Gets the content height, which is equal to the height of this page object minus the height of the top inset (given
     * by {@link #getInsetsTop()}) minus the height of the bottom inset (given by {@link #getInsetsBottom()}).
     * @return The content height.
     */
    public int getContentHeight() {
        return getHeight() - getInsetsTop() - getInsetsBottom();
    }

    /**
     * Gets the width and height of this page object encapsulated in a {@link Dimension} object.
     * @return The dimension object that encapsulates the width and height of this page object.  Changing the width
     * and height of the returned dimension object will not change the width and height of this page object.
     */
    public Dimension getSize() {
        return new Dimension(bounds.getSize());
    }

    /**
     * Gets the location, width and height of this page object encapsulated in a {@link Rectangle}.
     * @return The rectangle that represents the bounds of this page object.  Changing the location, width or height of
     * the returned rectangle object will not change the location, width of height of this page object.  The location
     * provided by the returned rectangle is relative to the location of the parent of this page object.
     */
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    public Rectangle getAbsoluteBounds() {
        Rectangle absoluteBounds = new Rectangle(bounds);
        PageObject parent = getParent();
        while(parent != null) {
            absoluteBounds.translate(parent.getX(), parent.getY());
            parent = parent.getParent();
        }
        return absoluteBounds;
    }

    /**
     * Determines whether this page object contains a point.
     * @param x The x co-ordinate of the point.  This should be specified relative to the location of the parent of this
     * page object.
     * @param y The y co-ordinate of the point.  This should be specified relative to the location of the parent of this
     * page object.
     * @return <code>true</code> if this page object contains the point described by the specified x and y co-ordinates.
     */
    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    /**
     * Determines whether this page object contains a point.
     * @param point The point relative to the location of the parent of this page object.
     * @return <code>true</code> if this page object contains the specified point.
     */
    public boolean contains(Point point) {
        return bounds.contains(point);
    }


    /**
     * Marks this page object as needing to be laid out.
     */
    public void invalidateLayout() {
        if (parent != null) {
            parent.invalidateLayout();
        }
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public double getOpacity() {
        return opacity;
    }

    /**
     * Lays out this {@link PageObject} using the specified {@link java.awt.font.FontRenderContext} to determine sizes of rendered
     * text.
     * @param fontRenderContext The {@link java.awt.font.FontRenderContext} that is used to lay out any text that is contained in
     * this {@link PageObject}.
     */
    public abstract void layout(FontRenderContext fontRenderContext);

    final public void handleMouseMoved(MouseEvent event) {
        if(bounds.contains(event.getPoint())) {
            setMouseOver(true);
            doHandleMouseMoved(event);
            event.translatePoint(-getX(), -getY());
            for (PageObject child : getChildren()) {
//                if (child.contains(event.getPoint())) {
                    child.handleMouseMoved(event);
//                }
            }
            event.translatePoint(getX(), getY());
        }
        else {
            setMouseOver(false);
        }
    }

    protected void doHandleMouseEntered(MouseEvent e) {

    }

    protected void doHandleMouseExited(MouseEvent e) {

    }

    final public List<LinkBox> getLinks() {
        List<LinkBox> result = new ArrayList<>();
        for (PageObject childPageObject : children) {
            if (childPageObject instanceof LinkBox) {
                LinkBox childLinkBox = (LinkBox) childPageObject;
                LinkBox translatedLinkBox = childLinkBox.translate(getX(), getY());
                result.add(translatedLinkBox);
            }
            else {
                List<LinkBox> childLinks = childPageObject.getLinks();
                for (LinkBox childLinkBox : childLinks) {
                    result.add(childLinkBox.translate(getX(), getY()));
                }
            }
        }
        return result;
    }


    private static final Color[] colors = new Color[]{Color.MAGENTA, Color.GREEN, Color.CYAN, Color.ORANGE, Color.RED, Color.YELLOW, Color.PINK};

    final public void draw(Graphics2D g2) {
//        drawDebugBounds(g2);
        if (!g2.getClip().intersects(getBounds())) {
            return;
        }
        double compoundOpacity = getOpacity();
        PageObject parent = getParent();
        while(parent != null) {
            compoundOpacity *= parent.getOpacity();
            parent = parent.getParent();
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) compoundOpacity));
        requestPaintContent(g2);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        for (PageObject pageObject : children) {
            pageObject.draw(g2);
        }
        g2.translate(getMarginLeft(), getMarginTop());
        pageObjectBorder.drawBorder(g2, getBorderWidth(), getBorderHeight(), this);
        g2.translate(-getMarginLeft(), -getMarginTop());
        g2.translate(-getX(), -getY());
    }


    private void requestPaintContent(Graphics2D g2) {
        int graphicsXOffset = getX() + getInsetsLeft();
        int graphicsYOffset = getY() + getInsetsTop();
        g2.translate(graphicsXOffset, graphicsYOffset);
        paintContent(g2);
        g2.translate(-graphicsXOffset, -graphicsYOffset);
        g2.translate(getX(), getY());
    }


    private void drawDebugBounds(Graphics2D g2) {
        Color oldColor = g2.getColor();
        int index = getDepth() % colors.length;
        g2.setColor(colors[index]);
        g2.draw(bounds);
        g2.drawLine(getX(), getY() + getHeight() / 2, getX() + getInsetsLeft(), getY() + getHeight() / 2);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[]{5f, 5f}, 0f));
        g2.drawRect(getX() + getInsetsLeft(), getY() + getInsetsTop(), getContentWidth(), getContentHeight());
        g2.setStroke(oldStroke);
        g2.setColor(oldColor);
    }

    private int getDepth() {
        PageObject parent = getParent();
        int depth = 0;
        while (parent != null) {
            parent = parent.getParent();
            depth++;
        }
        return depth;
    }





    final public void handleMouseReleased(MouseEvent event) {
        if (contains(event.getX(), event.getY())) {
            doMouseReleased(event);
            event.translatePoint(getX(), getY());
            for (PageObject childPageObject : getChildren()) {
                if (childPageObject.contains(event.getPoint())) {
                    childPageObject.handleMouseReleased(event);
                }
            }
            event.translatePoint(-getX(), -getY());
        }
    }

    /**
     * Called when the mouse is released on this page object.
     * @param event The event that describes the mouse being released.
     */
    protected void doMouseReleased(MouseEvent event) {

    }

    protected void doHandleMouseMoved(MouseEvent event) {
    }

    /**
     * Paints the content area of this page object.
     * @param g2 A graphics object which should be used to paint the content.  The origin will be the top left corner
     * of the page content.
     */
    protected abstract void paintContent(Graphics2D g2);


    public PageObject getDeepestPageObjectAt(Point pt) {
        return getDeepestPageObjectOfClassAt(PageObject.class, pt);
    }

    /**
     * Gets the deepest {@link PageObject} of a specific type at a given point.
     * @param pt The point relative to this page object's top left corner.
     * @param cls The class of {@link PageObject} to look for.
     * @return A {@link PageObject} P such that (1) P is an instance of the class <code>cls</code>, (2) P is either this
     * page object or is a descendant of this page object, (3) P does not contain a page object that contains pt and is
     * also an instance of cls.  <code>null</code> is returned if there is no such P.
     */
    public <T extends PageObject> T getDeepestPageObjectOfClassAt(Class<T> cls, Point pt) {
        T result = null;
        if(bounds.contains(pt)) {
            if (cls.isInstance(this)) {
                result = cls.cast(this);
            }
            pt.translate(-getX(), -getY());
            for(PageObject childPageObject : children) {
                PageObject o = childPageObject.getDeepestPageObjectAt(pt);
                if(o != null && cls.isInstance(o)) {
                    result = cls.cast(o);
                }
            }
            pt.translate(getX(), getY());
        }
        return result;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    private void setMouseOver(boolean b) {
        mouseOver = b;
        if(!b) {
            for(PageObject child : children) {
                child.setMouseOver(false);
            }
        }
    }

}
