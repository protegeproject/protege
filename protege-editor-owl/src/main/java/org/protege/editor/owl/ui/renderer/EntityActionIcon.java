package org.protege.editor.owl.ui.renderer;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 9 Aug 16
 */
public interface EntityActionIcon {

    BasicStroke ACTION_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

    static void setupState(Component c, Graphics2D g2, OWLEntityIcon icon) {
        icon.setEnabled(c.isEnabled());
    }
}
