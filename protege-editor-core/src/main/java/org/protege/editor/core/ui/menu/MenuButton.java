package org.protege.editor.core.ui.menu;

import javax.swing.*;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class MenuButton extends JButton {

    public MenuButton() {
        super(MenuIcon.getGrayIcon());
        setBackground(null);
        setBorder(null);
        setCursor(getPredefinedCursor(HAND_CURSOR));
        setRolloverEnabled(true);
        setRolloverIcon(MenuIcon.getDarkGrayIcon());
    }
}
