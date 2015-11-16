package org.protege.editor.core.ui.util;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;

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
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LinkLabel extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = -4294549567545547816L;

    private Color linkColor;

    private Color hoverColor;

    private Cursor oldCursor;

    private ActionListener linkListener;


    public LinkLabel(String text, ActionListener linkListener) {
        super(text);
        this.linkListener = linkListener;
        linkColor = PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.PROPERTY_COLOR_KEY),
                                          Color.GRAY);
        hoverColor = PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.CLASS_COLOR_KEY),
                                           Color.GRAY);

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
        setFont(getFont().deriveFont(Font.BOLD, 12.0f));
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


    public static void main(String[] args) {
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(new LinkLabel("Test link!", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        }));
        box.add(Box.createVerticalStrut(30));
        box.add(new LinkLabel("Another link", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        }));
        JFrame f = new JFrame();
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(box);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(panel);
        f.pack();
        f.setVisible(true);
    }
}
