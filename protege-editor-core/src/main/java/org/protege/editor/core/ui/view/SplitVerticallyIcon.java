package org.protege.editor.core.ui.view;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public class SplitVerticallyIcon extends ViewBarIcon {

    private static final SplitVerticallyIcon ICON = new SplitVerticallyIcon();

    private SplitVerticallyIcon() {
    }

    /**
     * Gets the Split Vertically Icon.
     * @return The Split Vertically Icon.
     */
    @Nonnull
    public static SplitVerticallyIcon get() {
        return ICON;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        int width = getIconWidth();
        int height = getIconHeight();
        g2.drawRect(1, 1, width - 2, height - 2);
        g2.drawLine(width / 2 - 1, 1, width / 2 - 1, height - 1);
        g2.drawLine(width / 2, 1, width / 2, height - 1);
        g2.drawLine(width / 2 + 1, 1, width / 2 + 1, height - 1);
    }


}
