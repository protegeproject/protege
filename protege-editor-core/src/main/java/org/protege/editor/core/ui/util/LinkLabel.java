package org.protege.editor.core.ui.util;

import org.protege.editor.core.Fonts;
import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.platform.OSUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LinkLabel extends JLabel {

    private Color linkColor;

    private Color hoverColor;

    private Cursor oldCursor;

    private ActionListener linkListener;


    public LinkLabel(String text, ActionListener linkListener) {
        super(text);
        this.linkListener = linkListener;
        linkColor = Color.BLACK;
        hoverColor = Color.BLUE;

        setForeground(linkColor);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setHoverMode(true);
            }


            public void mouseExited(MouseEvent e) {
                setHoverMode(false);
            }


            public void mouseReleased(MouseEvent e) {
                activateLink();
            }
        });

        setFont(Fonts.getMediumDialogFont().deriveFont(Font.BOLD, 14f));
    }


    public void setLinkColor(Color color) {
        linkColor = color;
    }


    public void setHoverColor(Color color) {
        hoverColor = color;
    }


    private void setHoverMode(boolean b) {
        if (b && isEnabled()) {
            setForeground(hoverColor);
            oldCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            setForeground(linkColor);
            if (oldCursor != null) {
                setCursor(oldCursor);
            }
        }
    }


    private void activateLink() {
        Point mousePosition = getMousePosition();
        if (isEnabled() && mousePosition != null && contains(mousePosition)) {
            linkListener.actionPerformed(new ActionEvent(this, 0, getText()));
        }
    }
}
