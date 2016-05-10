package org.protege.editor.owl.ui.renderer.styledstring;

import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 * <p>
 * A string marked up with various styles.  Each StyledString is immutable.
 * </p>
 */
public final class StyledString implements CharSequence, Comparable<StyledString> {

    public static final String EMPTY_STRING = "";

    public static final StyledString EMPTY_STYLED_STRING = new StyledString();

    private final String plainString;

    private final List<StyledStringMarkup> plainStringMarkup;

    private StyledStringLayout styledStringLayout;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an empty StyledString.
     */
    public StyledString() {
        this(EMPTY_STRING);
    }

    /**
     * Constructs a StyledString for the specified plain string.  The string has no markup associated with it.
     * @param text The plain string.  Not {@code null}.
     * @throws NullPointerException if text is {@code null}.
     */
    public StyledString(String text) {
        this(text, Collections.<StyledStringMarkup>emptyList());
    }

    /**
     * Constructs a StyledString with the specified plain string marked up with the specified markup.
     * @param text The plain string.  Not {@code null}.
     * @param markup The markup.  Not {@code null}.  May be empty.
     * @throws NullPointerException if {@code text} is {@code null}, if {@code markup} is {@code null}.
     */
    public StyledString(String text, List<StyledStringMarkup> markup) {
        this.plainString = text;
        this.plainStringMarkup = Collections.unmodifiableList(new ArrayList<>(markup));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the {@code char} value at the specified index.  An index ranges from zero
     * to <tt>length() - 1</tt>.  The first {@code char} value of the sequence is at
     * index zero, the next at index one, and so on, as for array
     * indexing. </p>

     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     * @param index the index of the {@code char} value to be returned
     * @return the specified {@code char} value
     * @throws IndexOutOfBoundsException if the <tt>index</tt> argument is negative or not less than
     *                                   <tt>length()</tt>
     */
    public char charAt(int index) {
        return plainString.charAt(index);
    }

    /**
     * Returns a new {@code CharSequence} that is a subsequence of this sequence.
     * The subsequence starts with the {@code char} value at the specified index and
     * ends with the {@code char} value at index <tt>end - 1</tt>.  The length
     * (in {@code char}s) of the
     * returned sequence is <tt>end - start</tt>, so if <tt>start == end</tt>
     * then an empty sequence is returned. </p>
     * @param start the start index, inclusive
     * @param end the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if <tt>start</tt> or <tt>end</tt> are negative,
     *                                   if <tt>end</tt> is greater than <tt>length()</tt>,
     *                                   or if <tt>start</tt> is greater than <tt>end</tt>
     */
    public StyledString subSequence(int start, int end) {
        checkStartAndEnd(start, end);
        return substring(start, end);
    }

    /**
     * Checks to see if {@code start} and {@code end} are within bounds for this string.
     * @param start The start
     * @param end The end
     * @throws IndexOutOfBoundsException if {@code start} or {@code end} are negative,
     *                                   if {@code end} is greater than {@code length()}
     *                                   if {@code start} is greater than {@code end}.
     */
    private void checkStartAndEnd(int start, int end) {
        if (start < 0) {
            throw new IndexOutOfBoundsException("start < 0");
        }
        if (end < 0) {
            throw new IndexOutOfBoundsException("end < 0");
        }
        if (end > length()) {
            throw new IndexOutOfBoundsException("end > length");
        }
        if (start > end) {
            throw new IndexOutOfBoundsException("start > end");
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Compares this StyledString with another StyledString.  The comparison is based purely on the lexical value of
     * the
     * each StyledString and not the markup.
     * @param o The other StyledString to compare to.
     * @return See {@link String#compareTo(Object)}.
     */
    public int compareTo(StyledString o) {
        return this.plainString.compareTo(o.plainString);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets a StyledString.Builder initialised with the contents of this StyledString.
     * @return A Builder.  Not {@code null}.
     */
    public Builder builder() {
        return new Builder(this);
    }

    /**
     * A builder for StyledStrings.  The Builder allows strings to be appended to each other with specification of
     * Styles or StyleAttributes.
     */
    public static final class Builder {

        public static final String NEW_LINE = "\n";

        public static final String SPACE = " ";

        public static final String TAB = "\t";

        private StringBuilder buffer = new StringBuilder();

        private List<StyledStringMarkup> markup = new ArrayList<>();

        public Builder() {

        }

        public Builder(StyledString styledString) {
            buffer.append(styledString.plainString);
            markup.addAll(styledString.plainStringMarkup);
        }

        public int mark() {
            return buffer.length();
        }

        public void append(String s) {
            buffer.append(s);
        }

        public void appendWithStyle(String s, Style style) {
            int start = mark();
            buffer.append(s);
            int end = mark();
            markup.add(new StyledStringMarkup(start, end, style));
        }

        public void appendWithAttributes(String s, StyleAttribute... attributes) {
            int start = mark();
            buffer.append(s);
            int end = mark();
            Style style = new Style(attributes);
            markup.add(new StyledStringMarkup(start, end, style));
        }


        public void appendNewLine() {
            buffer.append(NEW_LINE);
        }

        public void appendSpace() {
            buffer.append(SPACE);
        }

        public void appendTab() {
            buffer.append(TAB);
        }

        public void append(Number i) {
            buffer.append(i);
        }

        public void appendStyledString(StyledString styledString) {
            int offset = mark();
            buffer.append(styledString.plainString);
            for (StyledStringMarkup markup : styledString.plainStringMarkup) {
                this.markup.add(new StyledStringMarkup(markup.getStart() + offset, markup.getEnd() + offset, markup.getStyle()));
            }
        }

        public void applyStyle(int from, int to, Style style) {
            markup.add(new StyledStringMarkup(from, to, style));
        }

        public void applyStyleAttributes(int from, int to, StyleAttribute... styleAttributes) throws IndexOutOfBoundsException {
            if (styleAttributes.length == 0) {
                return;
            }
            if (buffer.length() == 0) {
                return;
            }
            if (from < 0) {
                throw new IndexOutOfBoundsException("from < 0");
            }
            if (to < 0) {
                throw new IndexOutOfBoundsException("to < 0");
            }
            if (from >= buffer.length()) {
                throw new IndexOutOfBoundsException("from >= mark");
            }
            if (to > buffer.length()) {
                throw new IndexOutOfBoundsException("to >= mark");
            }
            Style style = new Style(styleAttributes);
            markup.add(new StyledStringMarkup(from, to, style));
        }

        public void applyStyleAttributes(StyleAttribute... styleAttributes) {
            applyStyleAttributes(0, mark(), styleAttributes);
        }


        public StyledString build() {
            return new StyledString(buffer.toString(), markup);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean isEmpty() {
        return plainString.isEmpty();
    }


    public String getString() {
        return plainString;
    }

    public int length() {
        return plainString.length();
    }


    public StyledString substring(int start, int end) {
        if (end < start) {
            throw new IndexOutOfBoundsException("start (" + start + ") < end (" + end + ")");
        }
        if (start == end) {
            return EMPTY_STYLED_STRING;
        }
        String substring = plainString.substring(start, end);
        List<StyledStringMarkup> substringMarkup = new ArrayList<>();
        for (StyledStringMarkup markup : plainStringMarkup) {
            if (start < markup.getEnd() && end > markup.getStart()) {
                int substringMarkupStart;
                if (markup.getStart() < start) {
                    substringMarkupStart = start;
                }
                else {
                    substringMarkupStart = markup.getStart();
                }
                substringMarkupStart = substringMarkupStart - start;

                int substringMarkupEnd;
                if (markup.getEnd() > end) {
                    substringMarkupEnd = end;
                }
                else {
                    substringMarkupEnd = markup.getEnd();
                }
                substringMarkupEnd = substringMarkupEnd - start;
                substringMarkup.add(new StyledStringMarkup(substringMarkupStart, substringMarkupEnd, markup.getStyle()));
            }
        }
        return new StyledString(substring, substringMarkup);
    }

    private StyledStringLayout getStyledStringLayout() {
        if (styledStringLayout == null) {
            styledStringLayout = new StyledStringLayout(this);
        }
        return styledStringLayout;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void draw(Graphics2D g2, final float x, final float y) {
        getStyledStringLayout().draw(g2, x, y);
    }

    public AttributedString toAttributedString() {
        AttributedString as = new AttributedString(plainString.toString());
        for (StyledStringMarkup markup : plainStringMarkup) {
            Style style = markup.getStyle();
            int start = markup.getStart();
            int end = markup.getEnd();
            if (start < end && -1 < start && start < plainString.length() && 0 < end && end <= plainString.length()) {
                for (StyleAttribute styleAttribute : style.getStyleAttributes()) {
                    AttributedCharacterIterator.Attribute att = styleAttribute.getAttributedStringAttribute();
                    Object val = styleAttribute.getAttributesStringValue();
                    as.addAttribute(att, val, start, end);
                }
            }
        }
        return as;
    }

    public void drawVerticallyCentredInRect(Graphics2D g2, Rectangle rectangle) {
        FontRenderContext fontRenderContext = g2.getFontRenderContext();
        float height = getStyledStringLayout().getHeight(fontRenderContext);
        float x = rectangle.x;
        float y = rectangle.y + (float) (rectangle.getHeight() - height) / 2;
        draw(g2, x, y);
    }

    public void drawHorizontallyCenteredInRect(Graphics2D g2, Rectangle rectangle) {
        FontRenderContext fontRenderContext = g2.getFontRenderContext();
        float x = rectangle.x + (float) (rectangle.getWidth() - getStyledStringLayout().getWidth(fontRenderContext)) / 2;
        float y = rectangle.y;
        draw(g2, x, y);
    }

    public void drawCentredInRect(Graphics2D g2, Rectangle rectangle) {
        FontRenderContext fontRenderContext = g2.getFontRenderContext();
        StyledStringLayout styledStringLayout = getStyledStringLayout();
        float x = (float) (rectangle.getWidth() - styledStringLayout.getWidth(fontRenderContext)) / 2;
        float y = (float) (rectangle.getHeight() - styledStringLayout.getHeight(fontRenderContext)) / 2;
        draw(g2, x, y);
    }


//    public void addPrefix(String prefix) {
//        plainString.insert(0, prefix);
//        List<StyledStringMarkup> newMarkups = new ArrayList<StyledStringMarkup>();
//        for (StyledStringMarkup markup : plainStringMarkup) {
//            int inc = prefix.length();
//            newMarkups.add(new StyledStringMarkup(markup.getStart() + inc, markup.getEnd() + inc, markup.getStyle()));
//        }
//        plainStringMarkup.clear();
//        plainStringMarkup.addAll(newMarkups);
//        handleMutation();
//    }


    public void appendToStyledDocument(StyledDocument styledDocument) {
        try {
            styledDocument.remove(0, styledDocument.getLength());
            styledDocument.insertString(0, getString(), null);
            for (StyledStringMarkup markup : plainStringMarkup) {
                Style style = markup.getStyle();
                int length = markup.getEnd() - markup.getStart();
                MutableAttributeSet mas = new SimpleAttributeSet();
                for (StyleAttribute styleAttribute : style.getStyleAttributes()) {
                    StyleConstants styleConstants = styleAttribute.getTextAttribute();
                    Object value = styleAttribute.getTextValue();
                    mas.addAttribute(styleConstants, value);
                }
                styledDocument.setCharacterAttributes(markup.getStart(), length, mas, false);


            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    public List<Style> getStylesAt(int index) {
        List<Style> styles = new ArrayList<>();
        for (StyledStringMarkup markup : plainStringMarkup) {
            if (markup.getStart() <= index && index < markup.getEnd()) {
                styles.add(markup.getStyle());
            }
        }
        return styles;
    }

    public Style getMergedStyle(int index) {
        List<Style> styles = getStylesAt(index);
        if (styles.isEmpty()) {
            return new Style();
        }
        if (styles.size() == 1) {
            return styles.get(0);
        }
        Map<Class<? extends StyleAttribute>, StyleAttribute> atts = new HashMap<>();
        for (Style style : styles) {
            for (StyleAttribute styleAttribute : style.getStyleAttributes()) {
                atts.put(styleAttribute.getClass(), styleAttribute);
            }
        }
        return new Style(new ArrayList<>(atts.values()));
    }

    private void renderIntoHTML(Writer writer) {
        StringBuilder pw = new StringBuilder();

        List<StyledStringMarkup> sortedMarkups = new ArrayList<>(plainStringMarkup);
        Collections.sort(sortedMarkups);
        Set<Style> currentStyles = new HashSet<>();
        List<Integer> runLimits = new ArrayList<>();
        for (int i = 0; i < length(); i++) {
            Set<Style> iStyles = new HashSet<>();
            for (StyledStringMarkup markup : sortedMarkups) {
                if (markup.getStart() <= i && i < markup.getEnd()) {
                    iStyles.add(markup.getStyle());
                }
            }
            if (!iStyles.equals(currentStyles) || plainString.charAt(i) == '\n') {
                runLimits.add(i);
                currentStyles.clear();
                currentStyles.addAll(iStyles);
            }
        }
        runLimits.add(plainString.length());
//        PrintWriter pw = new PrintWriter(writer);
        int lastLimit = 0;
        for (Integer runLimit : runLimits) {
            List<StyleAttribute> styleAttributes = getMergedStyle(runLimit - 1).getStyleAttributes();
            if (!styleAttributes.isEmpty()) {
                pw.append("<span style=\"");
                for (StyleAttribute styleAttribute : styleAttributes) {
                    String propName = styleAttribute.getCSSPropertyName();
                    String propValue = styleAttribute.getCSSValue();
                    pw.append(propName);
                    pw.append(": ");
                    pw.append(propValue);
                    pw.append("; ");
                }
                pw.append("\">");
            }
            String substring = plainString.substring(lastLimit, runLimit);
            pw.append(substring);
            if (!styleAttributes.isEmpty()) {
                pw.append("</span>");
            }
            lastLimit = runLimit;
        }


        String[] lines = pw.toString().split("\\n");
        PrintWriter p = new PrintWriter(writer);
        p.append("<div style=\"font-family: verdana,sans-serif;\">");
        int counter = 0;
        for (String line : lines) {
            counter++;
            if (!line.equals("\n")) {
                p.append("<div class=\"line\">\n");
                p.append(line);
                p.append("\n");
                p.append("</div>\n");
            }
        }
        p.append("</div>");
        p.flush();


    }

    public String toPlainText() {
        return plainString;
    }

    public String toRTF() {
        try {
            RTFEditorKit editorKit = new RTFEditorKit();
            StyledDocument document = (StyledDocument) editorKit.createDefaultDocument();
            appendToStyledDocument(document);
            MutableAttributeSet fontFamily = new SimpleAttributeSet();
            StyleConstants.setFontFamily(fontFamily, Font.SANS_SERIF);
            document.setParagraphAttributes(0, document.getLength(), fontFamily, false);
            document.setCharacterAttributes(0, 4, fontFamily, false);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            editorKit.write(os, document, 0, document.getLength());
            return new String(os.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException("Problem rendering string into RTF", e);
        }
        catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public String toHTML() {
        StringWriter sw = new StringWriter();
        renderIntoHTML(sw);
        return sw.getBuffer().toString();
    }
}
