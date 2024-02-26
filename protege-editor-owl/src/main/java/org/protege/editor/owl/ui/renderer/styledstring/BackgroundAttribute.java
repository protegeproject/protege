package org.protege.editor.owl.ui.renderer.styledstring;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;

import javax.swing.text.StyleConstants;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 24/09/2012
 */
public class BackgroundAttribute extends StyleAttribute {


    private Color background;

    private BackgroundAttribute(Color background) {
        this.background = background;
    }

    public static BackgroundAttribute get(Color color) {
        return new BackgroundAttribute(color);
    }

    @Override
    public String getCSSPropertyName() {
        return CSSConstants.BACKGROUND.getName();
    }

    @Override
    public String getCSSValue() {
        return Integer.toHexString(background.getRGB() & 0x00ffffff);
    }

    @Override
    public AttributedCharacterIterator.Attribute getAttributedStringAttribute() {
        return TextAttribute.BACKGROUND;
    }

    @Override
    public Object getAttributesStringValue() {
        return background;
    }

    @Override
    public StyleConstants getTextAttribute() {
        return (StyleConstants.ColorConstants) StyleConstants.Background;
    }

    @Override
    public Object getTextValue() {
        return background;
    }

    @Override
    public int hashCode() {
        return BackgroundAttribute.class.getSimpleName().hashCode() + background.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BackgroundAttribute)) {
            return false;
        }
        BackgroundAttribute other = (BackgroundAttribute) obj;
        return this.background.equals(other.background);
    }
}
