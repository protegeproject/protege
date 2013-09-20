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
public class FontSizeAttribute extends StyleAttribute {

    private int fontSize;

    private FontSizeAttribute(int fontSize) {
        this.fontSize = fontSize;
    }

    public static FontSizeAttribute get(int fontSize) {
        return new FontSizeAttribute(fontSize);
    }

    @Override
    public String getCSSPropertyName() {
        return CSSConstants.FONT_SIZE.getName();
    }

    @Override
    public String getCSSValue() {
        return fontSize + "pt";
    }

    @Override
    public AttributedCharacterIterator.Attribute getAttributedStringAttribute() {
        return TextAttribute.SIZE;
    }

    @Override
    public Object getAttributesStringValue() {
        return fontSize;
    }

    @Override
    public StyleConstants getTextAttribute() {
        return (StyleConstants.FontConstants) StyleConstants.FontSize;
    }

    @Override
    public Object getTextValue() {
        return fontSize;
    }

    @Override
    public int hashCode() {
        return FontSizeAttribute.class.getSimpleName().hashCode() + fontSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FontSizeAttribute)) {
            return false;
        }
        FontSizeAttribute other = (FontSizeAttribute) obj;
        return this.fontSize == other.fontSize;
    }
}
