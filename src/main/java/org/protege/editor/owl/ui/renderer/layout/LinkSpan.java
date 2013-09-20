package org.protege.editor.owl.ui.renderer.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/11/2011
 * <p>
 *     Describes a span of a link over some text.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class LinkSpan {

    private Link link;

    private Span span;

    /**
     * Constructs a LinkSpan to describe a link in some text.
     * @param link The link.
     * @param span The position of the link in the text.
     */
    public LinkSpan(Link link, Span span) {
        this.link = link;
        this.span = span;
    }

    /**
     * Gets the link
     * @return The link.
     */
    public Link getLink() {
        return link;
    }

    /**
     * Gets the span of the link that desribes the position of the link in some text.
     * @return The span of the link.
     */
    public Span getSpan() {
        return span;
    }

    /**
     * Crops this LinkSpan so that it spans a different position to this span.
     * @param from The new start position of the link.
     * @param to The new end position of the link.
     * @see LinkSpan#crop(int, int) 
     * @return The cropped link span.
     */
    public LinkSpan crop(int from, int to) {
        return new LinkSpan(link, span.crop(from, to));
    }
}
