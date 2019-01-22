package org.protege.editor.core.ui.laf;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/03/15
 */
public class ProtegeScrollBarUI extends BasicScrollBarUI {

    public ProtegeScrollBarUI() {
        super();
        System.out.println("INSTAN SCROLL BAR");
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
