package org.protege.editor.core.ui.util;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
        if (isEnabled()) {
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
