package org.protege.editor.owl.ui.renderer.layout;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/11/2011
 * <p>
 *     Represents a link of text laid out on a page.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class Line extends PageObject {


    private AttributedString attributedString;

    private TextLayout textLayout;

    private Map<LinkBox, AttributedString> linkMap = new HashMap<LinkBox, AttributedString>();

    public Line(AttributedString attributedString) {
        this.attributedString = attributedString;
    }

    public void addLink(LinkSpan linkSpan, int linkStart, int linkEnd) {
        Rectangle linkBounds = textLayout.getLogicalHighlightShape(linkStart, linkEnd).getBounds();
        linkBounds.translate(0, (int)(textLayout.getAscent() + textLayout.getLeading()));
        AttributedString linkString = new AttributedString(attributedString.getIterator());
        linkString.addAttribute(TextAttribute.FOREGROUND, Color.MAGENTA.darker(), linkStart, linkEnd);
        linkString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL, linkStart, linkEnd);
        LinkBox linkBox = new LinkBox(linkSpan.getLink());
        linkBox.setBounds(linkBounds);
        add(linkBox);
    }

    public void layout(FontRenderContext fontRenderContext) {
        this.textLayout = new TextLayout(attributedString.getIterator(), fontRenderContext);
        int textHeight = (int) (textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading());
        int totalHeight = textHeight + getInsetsTop() + getInsetsBottom();
        setHeight(totalHeight);
    }

    public void paintContent(Graphics2D g2) {
        float v = getBaseline();
        AttributedString toRender = attributedString;
//        LinkSpan activeLinkSpan = getPage().getActiveLinkSpan();
//        if (activeLinkSpan != null) {
//            for(LinkBox link : linkMap.keySet()) {
//                if(link.getLinkSpan().equals(activeLinkSpan)) {
//                    toRender = linkMap.get(link);
//                    break;
//                }
//            }
//        }
        g2.drawString(toRender.getIterator(), 0, v);
    }

    private float getBaseline() {
        return textLayout.getAscent() + textLayout.getLeading();
    }

}