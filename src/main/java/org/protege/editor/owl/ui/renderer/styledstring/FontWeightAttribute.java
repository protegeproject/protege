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
public class FontWeightAttribute extends StyleAttribute {

    private static final FontWeightAttribute BOLD = new FontWeightAttribute(FontWeight.BOLD);


    private FontWeight fontWeight;

    public FontWeightAttribute(FontWeight fontWeight) {
        this.fontWeight = fontWeight;
    }

    @Override
    public String getCSSPropertyName() {
        return CSSConstants.FONT_WEIGHT.getName();
    }

    @Override
    public String getCSSValue() {
        return fontWeight == FontWeight.BOLD ? CSSConstants.BOLD.getName() : CSSConstants.NORMAL.getName();
    }

    @Override
    public AttributedCharacterIterator.Attribute getAttributedStringAttribute() {
        return TextAttribute.WEIGHT;
    }

    @Override
    public Object getAttributesStringValue() {
        return TextAttribute.WEIGHT_BOLD;
    }

    public static FontWeightAttribute getBoldFontWeight() {
        return BOLD;
    }

    public static FontWeightAttribute getRegularFontWeight() {
        return new FontWeightAttribute(FontWeight.REGULAR);
    }

    @Override
    public StyleConstants getTextAttribute() {
        return (StyleConstants.FontConstants) StyleConstants.Bold;
    }

    @Override
    public Object getTextValue() {
        return fontWeight == FontWeight.BOLD;
    }

    @Override
    public int hashCode() {
        return FontWeightAttribute.class.getSimpleName().hashCode() + fontWeight.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FontWeightAttribute)) {
            return false;
        }
        FontWeightAttribute other = (FontWeightAttribute) obj;
        return this.fontWeight == other.fontWeight;
    }

}
