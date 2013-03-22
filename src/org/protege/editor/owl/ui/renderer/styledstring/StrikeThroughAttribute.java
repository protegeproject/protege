package org.protege.editor.owl.ui.renderer.styledstring;

import javax.swing.text.StyleConstants;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 24/09/2012
 */
public class StrikeThroughAttribute extends StyleAttribute {


    private static final StrikeThroughAttribute SINGLE_STRIKE_THROUGH = new StrikeThroughAttribute(StrikeThrough.SINGLE);

    private StrikeThrough strikeThrough;

    private StrikeThroughAttribute(StrikeThrough strikeThrough) {
        this.strikeThrough = strikeThrough;
    }

    public static StrikeThroughAttribute get(StrikeThrough strikeThrough) {
        return new StrikeThroughAttribute(strikeThrough);
    }

    public static StrikeThroughAttribute getSingle() {
        return SINGLE_STRIKE_THROUGH;
    }

    @Override
    public String getCSSPropertyName() {
        return CSSConstants.TEXT_DECORATION.getName();
    }

    @Override
    public String getCSSValue() {
        return strikeThrough == StrikeThrough.SINGLE ? CSSConstants.LINE_THROUGH.getName() : CSSConstants.NONE.getName();
    }

    @Override
    public AttributedCharacterIterator.Attribute getAttributedStringAttribute() {
        return TextAttribute.STRIKETHROUGH;
    }

    @Override
    public Object getAttributesStringValue() {
        return strikeThrough == StrikeThrough.SINGLE;
    }

    @Override
    public StyleConstants getTextAttribute() {
        return (StyleConstants.CharacterConstants) StyleConstants.StrikeThrough;
    }

    @Override
    public Object getTextValue() {
        return strikeThrough == StrikeThrough.SINGLE;
    }
}
