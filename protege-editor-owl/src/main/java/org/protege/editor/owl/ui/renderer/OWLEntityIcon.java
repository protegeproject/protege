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

    private static final int BASE_SIZE = 16;

    private final FillType fillType;

    private Color overrideColor = null;

    private boolean enabled = true;

    protected OWLEntityIcon(FillType fillType) {
        this.fillType = fillType;
    }

    public abstract Color getEntityColor();

    public Color getColor() {
        if(!enabled) {
            return Color.LIGHT_GRAY;
        }
        if(overrideColor == null) {
            return getEntityColor();
        }
        else {
            return overrideColor;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public OWLEntityIcon setOverrideColor(Color overrideColor) {
        this.overrideColor = overrideColor;
        return this;
    }

    public OWLEntityIcon setOverriderColorToForegroundColor() {
        return setOverrideColor(OWLSystemColors.getForegroundColor());
    }

    public Color getBorderColor() {
        return getColor();
    }

    public FillType getFillType() {
        return fillType;
    }

    public int getBaseSize() {
        return BASE_SIZE;
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

    /**
     * Returns the icon's width.
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return getBaseSize();
    }

    /**
     * Returns the icon's height.
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return getBaseSize();
    }

    public enum FillType {
        FILLED,
        HOLLOW
    }
}
