package org.protege.editor.core.ui.laf;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/03/15
 */
public class ProtegeScrollBarUI extends BasicScrollBarUI {

    public ProtegeScrollBarUI() {
        super();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroSizeButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroSizeButton();
    }

    private JButton createZeroSizeButton() {
        JButton button = new JButton();
        button.setMaximumSize(new Dimension(0, 0));
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        return button;
    }
}
