package org.protege.editor.core.ui.view;

import javax.swing.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Nov 2016
 */
public abstract class ViewBarIcon implements Icon {

    private static final int WIDTH = 12;

    private static final int HEIGHT = 12;

    @Override
    public int getIconWidth() {
        return WIDTH;
    }

    @Override
    public int getIconHeight() {
        return HEIGHT;
    }
}
