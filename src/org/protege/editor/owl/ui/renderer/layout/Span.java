package org.protege.editor.owl.ui.renderer.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/11/2011
 * <br/>
 * Describes spans of text.
 */
public class Span {

    private int startIndex = -1;

    private int endIndex = -1;

    /**
     * Desribes a span over a String.
     * @param startIndex The start index of the span in the String
     * @param endIndex The end index of the span in the String.  The end index is exclusive in that the substring
     * denoted by this span is up to but not including the end index.  For example, given the string "abc" the Span
     * with a start index of 0 and an end index of 2 describes the substring "ab".
     */
    public Span(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Gets the start index of this span.
     * @return The start index.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Gets the end index of this span.
     * @return The end index.
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Determines if this span intersects with another span. A span C intersects with a span D if the start index of
     * C is less than the end index of D, and the end index of D is greate than the start index of C + 1.  This implies
     * that the intersection is non-empty.
     * @param span The other span to test for intersection with.
     * @return <code>true</code> if this span intersects with the other span, otherwise <code>false</code>.
     */
    public boolean intersectsWith(Span span) {
        return span.startIndex < endIndex && span.endIndex > startIndex + 1;
    }


    @Override
    public String toString() {
        return "Span(" + startIndex + ", " + endIndex + ")";
    }

    /**
     * Gets the length of this span.  This is equivalent to the end index minus the start index.
     * @return The length of this span.
     */
    public int getLength() {
        return endIndex - startIndex;
    }

    /**
     * Determines if this span is empty.  A span is empty if its start index is equal to its end index.
     */
    public boolean isEmpty() {
        return endIndex <= startIndex;
    }

    /**
     * Gets a span which is the same as this span but with (1) a start index (specified by the from parameter) that is
     * greater than or equal to the start index of this span, and an end index is less than the end index of this span,
     * and (2) an end index (specified by the to parameter) that is greater or equal to the start index of this span
     * but less than the end index of this span.
     * @param from The new crop index which specifies the start of the cropped span.  If from is less than the start
     * index of this span then it is set to the start index of this span.
     * @param to The new crop index which specified the end of the cropped span.  If to is greater than the end index
     * of this span then it is capped to the end index of this span.
     * @return The new span with a start index of from, and an end index of to.
     */
    public Span crop(int from, int to) {
        int croppedFrom = from > startIndex ? from : startIndex;
        int croppedTo = to < endIndex ? to : endIndex;
        return new Span(croppedFrom, croppedTo);
    }

    public List<Span> split(List<Integer> positions) {
        List<Span> stack = new ArrayList<Span>();
        stack.add(0, this);
        for(int position : positions) {
            if(position > startIndex && position < endIndex) {
                Span currentSpan = stack.get(0);
                if(position >= currentSpan.startIndex && position < currentSpan.endIndex) {
                    stack.remove(0);
                    stack.add(0, new Span(currentSpan.startIndex, position));
                    stack.add(0, new Span(position, currentSpan.endIndex));
                }
                else {
                    break;
                }
            }
        }
        Collections.reverse(stack);
        return stack;
    }
}
