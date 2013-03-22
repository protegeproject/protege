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
public class UnderlineAttribute extends StyleAttribute {

    private static final UnderlineAttribute NONE = new UnderlineAttribute(Underline.NONE);

    private static final UnderlineAttribute SINGLE = new UnderlineAttribute(Underline.SINGLE);


    private Underline underline;

    public UnderlineAttribute(Underline underline) {
        this.underline = underline;
    }

    public Underline getUnderline() {
        return underline;
    }

    public static UnderlineAttribute getNone() {
        return NONE;
    }

    public static UnderlineAttribute getSingle() {
        return SINGLE;
    }

    @Override
    public String getCSSPropertyName() {
        return CSSConstants.TEXT_DECORATION.getName();
    }

    @Override
    public String getCSSValue() {
        return underline == Underline.SINGLE ? CSSConstants.UNDERLINE.getName() : CSSConstants.NONE.getName();
    }

    @Override
    public AttributedCharacterIterator.Attribute getAttributedStringAttribute() {
        return TextAttribute.UNDERLINE;
    }

    @Override
    public Object getAttributesStringValue() {
        return TextAttribute.UNDERLINE_LOW_ONE_PIXEL;
    }

    @Override
    public StyleConstants getTextAttribute() {
        return (StyleConstants.CharacterConstants) StyleConstants.Underline;
    }

    @Override
    public Object getTextValue() {
        return underline == Underline.SINGLE;
    }

    @Override
    public int hashCode() {
        return UnderlineAttribute.class.getSimpleName().hashCode() + underline.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnderlineAttribute)) {
            return false;
        }
        UnderlineAttribute other = (UnderlineAttribute) obj;
        return this.underline.equals(other.underline);
    }
}
