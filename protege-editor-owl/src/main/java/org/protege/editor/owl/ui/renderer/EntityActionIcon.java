package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public interface EntityActionIcon {

    static final BasicStroke ACTION_STROKE = new BasicStroke(2);

    static void setupAlpha(Component c, Graphics2D g2) {
        if(!c.isEnabled()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
    }
}
