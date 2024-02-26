package org.protege.editor.owl.ui.renderer.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 02/11/2011
 * <br/>
 * Represents a Page, possibly containing hyperlinks.  Pages contain {@link PageObject}s such as {@link Paragraph}s.
 * These classes abstract over the notion of a page formatted in HTML.
 * <br>
 * <b>21st November 2011: This class is subject to change and it is therefore not recommended for public use</b>.
 */
public class Page extends PageObject {

    public static final Color DEFAULT_FOREGROUND = Color.BLACK;

    private PageLayoutState layoutState = PageLayoutState.INVALID;

    private LinkSpan activeLinkSpan;


    public Page() {
    }

    /**
     * Creates and adds a {@link Paragraph} to this page.
     * @param paragraph The paragraph text to be added.
     * @return The paragraph that was added.
     */
    public Paragraph addParagraph(String paragraph) {
        return this.addParagraph(paragraph, DEFAULT_FOREGROUND);
    }

    /**
     * Creates and adds a {@link Paragraph} to this page.  The paragraph text will be formatted with the specified
     * foreground color.
     * @param paragraph The string representing the paragraph text to be added.
     * @param foreground The foreground color for the paragraph.
     * @return The {@link Paragraph} that was created and added.
     */
    public Paragraph addParagraph(String paragraph, Color foreground) {
        Paragraph para = new Paragraph(paragraph);
        para.setForeground(foreground);
        add(para);
        return para;
    }

    /**
     * A convenience method that creates and adds a {@link Paragraph} to this page.  The whole paragraph is a {@link Link}
     * which is specified by the <code>link</code> parameter.
     * @param paragraph The string representing the paragraph text to be added.
     * @param link The link that will be a link over the entire paragraph.
     * @return The {@link Paragraph} that was created and added.
     */
    public Paragraph addParagraph(String paragraph, Link link) {
        Paragraph para = new Paragraph(paragraph, link);
        add(para);
        return para;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////
    ////////  Layout.  The exact page metrics depend on how the page is rendered.
    ////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void invalidateLayout() {
        layoutState = PageLayoutState.INVALID;
    }

    public void layout(FontRenderContext fontRenderContext) {
        if (layoutState.equals(PageLayoutState.VALID)) {
            return;
        }
        int verticalOffset = getInsetsTop();
        for (PageObject childPageObject : getChildren()) {
            int insetsLeft = getInsetsLeft();
            childPageObject.setX(insetsLeft);
            childPageObject.setY(verticalOffset);
            childPageObject.setWidth(getWidth() - getInsetsLeft() - getInsetsRight());
            childPageObject.layout(fontRenderContext);
            verticalOffset += childPageObject.getHeight();
        }
        int height = verticalOffset + getInsetsBottom();
        setHeight(height);
        layoutState = PageLayoutState.VALID;

    }

    public void paintContent(Graphics2D g) {
    }


    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }
}
