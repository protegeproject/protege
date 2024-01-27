package org.protege.editor.owl.ui.renderer.styledstring;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 */
public class Style {

    public static final Style STRIKE_THROUGH_STYLE = new Style(StrikeThroughAttribute.get(StrikeThrough.SINGLE));

    public static final Style BOLD_STYLE = new Style(FontWeightAttribute.getBoldFontWeight());

    private List<StyleAttribute> styleAttributes = new ArrayList<>(1);

    public Style(List<StyleAttribute> styleAttributes) {
        this.styleAttributes.addAll(styleAttributes);
    }

    public Style(StyleAttribute... attributes) {
        this(Arrays.asList(attributes));
    }

    public Style(Color foreground) {
        this(ForegroundAttribute.get(foreground));
    }

    public static Style getForeground(Color foreground) {
        return new Style(ForegroundAttribute.get(foreground));
    }

    public static Style getBoldForeground(Color foreground) {
        return new Style(FontWeightAttribute.getBoldFontWeight(), ForegroundAttribute.get(foreground));
    }

    public static Style getBackground(Color background) {
        return new Style(BackgroundAttribute.get(background));
    }

    public static Style getBoldBackground(Color background) {
        return new Style(FontWeightAttribute.getBoldFontWeight(), BackgroundAttribute.get(background));
    }

    public static Style getBold() {
        return BOLD_STYLE;
    }

    public static Style getStrikeThrough() {
        return STRIKE_THROUGH_STYLE;
    }

    public List<StyleAttribute> getStyleAttributes() {
        return Collections.unmodifiableList(styleAttributes);
    }

    public Style append(Style style) {
        int size = styleAttributes.size() + style.styleAttributes.size();
        List<StyleAttribute> allAttributes = new ArrayList<>(size);
        allAttributes.addAll(styleAttributes);
        allAttributes.addAll(style.styleAttributes);
        return new Style(allAttributes);
    }

    @Override
    public int hashCode() {
        return Style.class.getSimpleName().hashCode() + styleAttributes.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Style)) {
            return false;
        }
        Style other = (Style) obj;
        return this.styleAttributes.equals(other.styleAttributes);
    }
}
