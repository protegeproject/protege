package org.protege.editor.owl.ui.renderer.styledstring;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 26/09/2012
 */
public class StyledStringLayout {


    private final List<TextLayoutCache> textLayoutLines = new ArrayList<TextLayoutCache>();


    public StyledStringLayout(StyledString styledString) {
        createLines(styledString);
    }


    private void createLines(StyledString styledString) {
        if (styledString.isEmpty()) {
            return;
        }
        String[] lines = styledString.getString().split("\\n");
        int lineStart = 0;
        int lineEnd = 0;
        for (String line : lines) {
            lineEnd = lineStart + line.length();
            AttributedCharacterIterator iterator = styledString.toAttributedString().getIterator();
            AttributedString attributedLine = new AttributedString(iterator, lineStart, lineEnd);
            textLayoutLines.add(new TextLayoutCache(attributedLine));
            lineStart = lineEnd + 1;
        }
    }


    public float getWidth(FontRenderContext fontRenderContext) {
        float maxWidth = 0;
        for (TextLayoutCache cache : textLayoutLines) {
            float visibleAdvance = cache.getVisibleAdvance(fontRenderContext);
            if (visibleAdvance > maxWidth) {
                maxWidth = visibleAdvance;
            }
        }
        return maxWidth;
    }

    public float getHeight(FontRenderContext fontRenderContext) {
        float height = 0;
        for (TextLayoutCache textLayoutCache : textLayoutLines) {
            height += textLayoutCache.getHeight(fontRenderContext);
        }
        return height;
    }

    public void draw(Graphics2D g2, float x, float y) {
        float yOffset = y;
        float leading = 0;
        float ascent = 0;
        float descent = 0;
        for (TextLayoutCache cache : textLayoutLines) {
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout textLayout = cache.getTextLayout(frc);
            leading = textLayout.getLeading();
            ascent = textLayout.getAscent();
            yOffset += leading + ascent + descent;

            textLayout.draw(g2, x, yOffset);
            descent = textLayout.getDescent();

        }

    }


}
