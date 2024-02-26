package org.protege.editor.owl.ui.renderer.styledstring;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedString;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 26/09/2012
 * <p>
 * A simple first in first out cache for TextLayout objects of AttributedStrings.
 * </p>
 */
public class TextLayoutCache {

    private final AttributedString attributedString;

    private FontRenderContext cachedFontRenderContext;

    private TextLayout cachedLayout;

    public TextLayoutCache(AttributedString attributedString) {
        this.attributedString = checkNotNull(attributedString);
    }

    public AttributedString getAttributedString() {
        return attributedString;
    }

    public TextLayout getTextLayout(FontRenderContext fontRenderContext) {
        if (!isCachedFontRenderContext(fontRenderContext)) {
            cachedLayout = new TextLayout(attributedString.getIterator(), fontRenderContext);
            cachedFontRenderContext = fontRenderContext;
        }
        return cachedLayout;
    }

    public float getHeight(FontRenderContext fontRenderContext) {
        TextLayout tl = getTextLayout(fontRenderContext);
        return tl.getLeading() + tl.getAscent() + tl.getDescent();
    }

    public float getVisibleAdvance(FontRenderContext fontRenderContext) {
        TextLayout tl = getTextLayout(fontRenderContext);
        return tl.getVisibleAdvance();
    }

    public float getBaseline(FontRenderContext fontRenderContext) {
        TextLayout tl = getTextLayout(fontRenderContext);
        return tl.getLeading() + tl.getAscent();

    }


    private boolean isCachedFontRenderContext(FontRenderContext fontRenderContext) {
        return cachedFontRenderContext != null && cachedFontRenderContext.equals(fontRenderContext);
    }
}
