package org.protege.editor.owl.ui.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public abstract class OWLEntityIcon implements Icon {

    private static int DEFAULT_PADDING_PX = 2;

    private SizeBias sizeBias;

    protected OWLEntityIcon(SizeBias sizeBias) {
        this.sizeBias = sizeBias;
    }

    protected int getSize() {
        int size;
        int fontSize = OWLRendererPreferences.getInstance().getFontSize();
        int idealSize = fontSize + getPadding() * 2;
        if(sizeBias.equals(SizeBias.EVEN)) {
            size = (idealSize / 2) * 2;
        }
        else {
            size = (idealSize / 2) * 2 + 1;
        }
        return size;
    }
    
    protected int getBaseline(Component c, int defaultBaseline) {
        if(c == null) {
            return defaultBaseline;
        }
        Font f = c.getFont();
        if(f == null) {
            return defaultBaseline;
        }
        FontMetrics fm = c.getFontMetrics(f);
        return fm.getAscent() + fm.getLeading();
    }
    
    protected int getPadding() {
        return DEFAULT_PADDING_PX;
    }
    
    /**
     * Returns the icon's width.
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return getSize();
    }

    /**
     * Returns the icon's height.
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return getSize();
    }
    
    public static enum SizeBias {
        ODD,
        EVEN
    }
}
