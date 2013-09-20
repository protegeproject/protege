package org.protege.editor.owl.ui.renderer.styledstring;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 24/09/2012
 */
public class ForegroundAttribute extends StyleAttribute {

    private static final ForegroundAttribute BLACK = new ForegroundAttribute(Color.BLACK);

    private Color foreground;

    private ForegroundAttribute(Color foreground) {
        this.foreground = foreground;
    }

    public static ForegroundAttribute get(Color foreground) {
        return new ForegroundAttribute(foreground);
    }

    @Override
    public String getCSSPropertyName() {
        return CSSConstants.COLOR.getName();
    }

    @Override
    public String getCSSValue() {
//        String s = Integer.toHexString(foreground.getRGB() & 0x00ffffff);
        return String.format("#%06x", foreground.getRGB() & 0x00ffffff);
    }

    @Override
    public AttributedCharacterIterator.Attribute getAttributedStringAttribute() {
        return TextAttribute.FOREGROUND;
    }

    @Override
    public Object getAttributesStringValue() {
        return foreground;
    }

    public Color getForeground() {
        return foreground;
    }

    public ForegroundAttribute getBlack() {
        return BLACK;
    }

    @Override
    public StyleConstants getTextAttribute() {
        return (StyleConstants.ColorConstants) StyleConstants.Foreground;
    }

    @Override
    public Object getTextValue() {
        return foreground;
    }

    @Override
    public int hashCode() {
        return ForegroundAttribute.class.getSimpleName().hashCode() + foreground.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ForegroundAttribute)) {
            return false;
        }
        ForegroundAttribute other = (ForegroundAttribute) obj;
        return this.foreground.equals(other.foreground);
    }
}
