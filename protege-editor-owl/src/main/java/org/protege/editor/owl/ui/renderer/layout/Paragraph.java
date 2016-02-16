package org.protege.editor.owl.ui.renderer.layout;

import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.File;
import java.net.URI;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 02/11/2011
 */
public class Paragraph extends PageObject {

    public static final int TAB_SIZE = 40;

    private AttributedString paragraphText;

    private String plainText;

    private boolean wrapText = true;


    private List<LinkSpan> linkSpans = new ArrayList<>();

    private Alignment alignment = Alignment.LEFT;

    private Icon icon;

    private Icon bulletIcon;

    public Paragraph(String paragraphText) {
        this(new AttributedString(paragraphText));
    }

    public Paragraph(String paragraphText, Link link) {
        this(new AttributedString(paragraphText), Arrays.asList(new LinkSpan(link, new Span(0, paragraphText.length()))));
    }

    public Paragraph(String paragraphText, List<LinkSpan> linkSpans) {
        this(new AttributedString(paragraphText), linkSpans);
    }

    private Paragraph(AttributedString paragraphText) {
        this(paragraphText, Collections.<LinkSpan>emptyList());
    }

    private Paragraph(AttributedString paragraphText, List<LinkSpan> linkSpans) {
        this.paragraphText = paragraphText;
        this.linkSpans.addAll(linkSpans);
        computePlainString();
        int fontSize = OWLRendererPreferences.getInstance().getFontSize();
        setSize(fontSize);
    }

    public void append(String text, Color foreground) {
        if(text.length() == 0) {
            throw new IllegalArgumentException("text must not be empty");
        }
        AttributedString replacementText = new AttributedString(plainText + text);
        replacementText.addAttributes(paragraphText.getIterator().getAttributes(), 0, plainText.length());
        AttributedCharacterIterator charIt = paragraphText.getIterator();
        while(charIt.current() != AttributedCharacterIterator.DONE) {
            replacementText.addAttributes(charIt.getAttributes(), charIt.getIndex(), charIt.getEndIndex() + 1);
            charIt.next();
        }
        replacementText.addAttribute(TextAttribute.FOREGROUND, foreground, plainText.length(), plainText.length() + text.length());
        paragraphText = replacementText;
        computePlainString();
        invalidateLayout();
    }

    public void append(Number number, Color foreground) {
        append(number.toString(), foreground);
    }

    private void computePlainString() {
        StringBuilder sb = new StringBuilder();
        char ch;
        AttributedCharacterIterator iterator = paragraphText.getIterator();

        while((ch = iterator.current()) != AttributedCharacterIterator.DONE) {
            sb.append(ch);
            iterator.next();
        }
        plainText = sb.toString();
    }

    public boolean isBulletPoint() {
        return bulletIcon != null;
    }

    public void setBulletPoint(boolean bulletPoint) {
        if (bulletPoint) {
            this.bulletIcon = new BulletIcon();
        }
        else {
            this.bulletIcon = null;
        }
        invalidateLayout();
    }

    public void addLink(LinkSpan linkSpan) {
        linkSpans.add(linkSpan);
        invalidateLayout();
    }

    public void addLink(Link link) {
        linkSpans.add(new LinkSpan(link, new Span(0, plainText.length())));
    }

    public void addLink(URI uri) {
        addLink(new HTTPLink(uri));
    }

    public void addLink(File file) {
        addLink(new FileLink(file));
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setBold(boolean b) {
        if (b) {
            paragraphText.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        else {
            paragraphText.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        }
    }

    public void setForeground(Color foreground) {
        paragraphText.addAttribute(TextAttribute.FOREGROUND, foreground);
    }

    public void setSize(int size) {
        paragraphText.addAttribute(TextAttribute.SIZE, size);
    }

    public void setStrikeThrough(boolean b) {
        paragraphText.addAttribute(TextAttribute.STRIKETHROUGH, b);
    }

    public void setStrikeThrough(int start, int end, boolean b) {
        paragraphText.addAttribute(TextAttribute.STRIKETHROUGH, b, start, end);
    }

//    public List<LinkBox> getLinks() {
//        List<LinkBox> result = new ArrayList<LinkBox>();
//        for(PageObject child : getChildren()) {
//            for(LinkBox link : child.getLinks()) {
//                result.add(link.translate(getX(), getY()));
//            }
//        }
//        return result;
//    }

    public int getLength() {
        return plainText.length();
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    private Set<LinkSpan> highlightedLinkSpan = new HashSet<>();

//    public void setLinkHighlightLoaction(int x, int y) {
//        this.linkHighlightX = x;
//        this.linkHighlightY = y;
//        highlightedLinkSpan.clear();
//        for(PageObject line : getChildren()) {
//            LinkSpan linkSpan = line.getLinkSpanAt(x - this.getX(), y - this.getY());
//            if(linkSpan != null) {
//                highlightedLinkSpan.add(linkSpan);
//            }
//        }
//    }


    public boolean isWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
        invalidateLayout();
    }

    public void setTabCount(int tabCount) {
        setMarginLeft(tabCount * TAB_SIZE);
    }

    public void increaseTabCount() {
        int marginSize = getMarginLeft() + TAB_SIZE;
        setMarginLeft(marginSize);
    }

    public AttributedString getParagraphText() {
        return paragraphText;
    }

    public String getPlainText() {
        return plainText;
    }

    public List<LinkSpan> getLinkSpans() {
        return new ArrayList<>(linkSpans);
    }

    public void layout(FontRenderContext fontRenderContext) {

        clear();

        AttributedString as;
        if(plainText.isEmpty()) {
            as = new AttributedString(" ");
        }
        else {
            as =  new AttributedString(paragraphText.getIterator());
        }
        highlightLinks(as);
        AttributedCharacterIterator iterator = as.getIterator();
        LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(iterator, fontRenderContext);


        int iconIndent = getBulletWidth() + getIconWidth();

        int totalIndent = getInsetsLeft() + iconIndent;

        int contentWidth = getWidth() - getInsetsLeft() - getInsetsRight() - iconIndent; //getContentWidth() - iconIndent;

        if (contentWidth > 0) {
            int lineOffset = getInsetsTop();
            while (lineBreakMeasurer.getPosition() < iterator.getEndIndex()) {
                int lineStart = lineBreakMeasurer.getPosition();
                int nextNewLineIndex = plainText.indexOf('\n', lineStart + 1);
                int offsetLimit = nextNewLineIndex != -1 ? nextNewLineIndex : plainText.length();
                int nextOffset;
                if(nextNewLineIndex != -1) {
                    nextOffset = lineBreakMeasurer.nextOffset(contentWidth, offsetLimit, false);
                }
                else {
                    nextOffset = lineBreakMeasurer.nextOffset(contentWidth, Integer.MAX_VALUE, false);
                }
                lineBreakMeasurer.setPosition(nextOffset);
                int lineEnd = nextOffset;

                Line line = new Line(new AttributedString(iterator, lineStart, lineEnd));
                line.setWidth(contentWidth);
                line.setX(totalIndent);
                line.setY(lineOffset);
                line.layout(fontRenderContext);
                lineOffset += line.getHeight();
                add(line);
               
                for (LinkSpan linkSpan : linkSpans) {
                    if (linkSpan.getSpan().intersectsWith(new Span(lineStart, lineEnd))) {
                        LinkSpan croppedSpan = linkSpan.crop(lineStart, lineEnd);
                        int linkStartIndex = croppedSpan.getSpan().getStartIndex() - lineStart;
                        int linkEndIndex = croppedSpan.getSpan().getEndIndex() - lineStart;
                        line.addLink(linkSpan, linkStartIndex, linkEndIndex);
                    }
                }
            }
            int iconHeight = getIconHeight();
            int textHeight = lineOffset;
            int contentHeight = iconHeight > textHeight ? iconHeight : textHeight;
            setHeight(getInsetsTop() + contentHeight + getInsetsBottom());
        }

    }

    private int getBulletWidth() {
        return bulletIcon == null ? 0 : bulletIcon.getIconWidth() + 4;
    }

    private int getIconWidth() {
        return icon != null ? icon.getIconWidth() + 5 : 0;
    }

    private int getIconHeight() {
        return icon != null ? icon.getIconHeight() : 0;
    }

    @Override
    protected void paintContent(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(bulletIcon != null) {
            List<PageObject> children = getChildren();
            if (!children.isEmpty()) {
                int firstChildHeight = children.get(0).getHeight();
                bulletIcon.paintIcon(null, g2, 0, firstChildHeight / 2 - bulletIcon.getIconHeight() / 2);
            }
        }
        if(icon != null) {
            icon.paintIcon(null, g2, getBulletWidth(), 0);
        }
     }

    private void highlightLinks(AttributedString as) {
        for (LinkSpan linkSpan : linkSpans) {
            if (highlightedLinkSpan.contains(linkSpan) || !linkSpan.getLink().isRollOverLink()) {
                as.addAttribute(TextAttribute.FOREGROUND, Color.BLUE, linkSpan.getSpan().getStartIndex(), linkSpan.getSpan().getEndIndex());
                as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL, linkSpan.getSpan().getStartIndex(), linkSpan.getSpan().getEndIndex());
            }

        }
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Paragraph(");
        sb.append(plainText);
        sb.append(")");
        return sb.toString();
    }
}
