package org.protege.editor.core.ui.util;

import org.protege.editor.core.Fonts;

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
    private boolean hoverMode;
    private boolean isClickable;


    public LinkLabel(String text, ActionListener linkListener) {
        super(text);
        this.linkListener = linkListener;
        this.isClickable = linkListener != null;
        updateColorsFromUI();
        if (isClickable) {
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
        }
        setFont(Fonts.getMediumDialogFont().deriveFont(Font.BOLD, 14f));
    }


    public void setLinkColor(Color color) {
        linkColor = color;
        if (!hoverMode) {
            setForeground(linkColor);
        }
    }


    public void setHoverColor(Color color) {
        hoverColor = color;
    }


    private void setHoverMode(boolean b) {
        if (b && isEnabled()) {
            hoverMode = true;
            setForeground(hoverColor);
            oldCursor = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            hoverMode = false;
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

    @Override
    public void updateUI() {
        super.updateUI();
        updateColorsFromUI();
    }

    private void updateColorsFromUI() {
        if (isClickable) {
            Color uiLink = UIManager.getColor("Link.foreground");
            Color uiHover = UIManager.getColor("Link.hoverForeground");
            // Theme-aware fallback colors
            if (uiLink == null) {
                uiLink = ThemeManager.isDarkTheme() ? 
                    new Color(0x80, 0x70, 0xFF) : new Color(0x00, 0x64, 0xC8);  // Dark blue for light theme
            }
            if (uiHover == null) {
                uiHover = ThemeManager.isDarkTheme() ? 
                    uiLink.brighter() : uiLink.darker();
            }
            linkColor = uiLink;
            hoverColor = uiHover;
            if (!hoverMode) {
                setForeground(linkColor);
            }
        } else {
            // Use label foreground for non-clickable labels
            Color labelColor = UIManager.getColor("Label.foreground");
            if (labelColor == null) {
                labelColor = ThemeManager.isDarkTheme() ? Color.WHITE : Color.BLACK;
            }
            setForeground(labelColor);
        }
    }
}
