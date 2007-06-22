package org.protege.editor.core.ui.view;

import org.protege.editor.core.ui.view.button.ViewButtonUI;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
 * Date: 22-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A component that displays a "banner" with
 * white text and a coloured  background.
 */
public class ViewBanner extends JPanel {

    private JPanel toolBarPanel;

    private JPanel labelPanel;

    private JLabel label;

    private JToolBar toolBar;

    private Color backgroundColor;

    private Color foregroundColor;

    private String labelText;

    private Color defaultBackgroundColor;


    public ViewBanner(String labelText, Color bannerColor) {
        this.defaultBackgroundColor = bannerColor;
        this.backgroundColor = bannerColor;
        this.foregroundColor = Color.WHITE;
        this.labelText = labelText;
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(backgroundColor);
        labelPanel = new JPanel(new BorderLayout());
        add(labelPanel, BorderLayout.NORTH);
//        JLabel l = new JLabel(labelText);
//        l.setForeground(Color.WHITE);
//        l.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.DARK_GRAY),
//                BorderFactory.createEmptyBorder(1, 1, 1, 1)
//        ));
//        add(l, BorderLayout.NORTH);
        labelPanel.setBackground(null);
        labelPanel.setOpaque(true);
        label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        label.setForeground(foregroundColor);
        setText("");
        toolBar = new JToolBar();
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);
        toolBarPanel = new JPanel(new BorderLayout());
        toolBarPanel.add(toolBar, BorderLayout.EAST);
        toolBarPanel.setOpaque(true);
        toolBarPanel.setBackground(backgroundColor);
        labelPanel.add(toolBarPanel, BorderLayout.EAST);
        labelPanel.add(label, BorderLayout.WEST);
//        labelPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new PropertyChangeListener() {
            /**
             * This method gets called when a bound property is changed.
             * @param evt A PropertyChangeEvent object describing the event source
             *            and the property that has changed.
             */

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("focusOwner")) {
                    repaint();
                }
            }
        });
//        backgroundColor = new Color(80, 148, 229);
//        backgroundColor = new Color(30, 122, 232);
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        label.setEnabled(enabled);
    }


    /**
     * Sets the header sub header.
     * @param text The text that should be set as
     *             the sub header text.
     */
    public void setText(String text) {
        label.setForeground(foregroundColor);
        label.setText(labelText + ": " + text);
    }


    /**
     * Sets the background color of the header.
     * @param color The color to be set.
     */
    public void setBannerColor(Color color) {
        backgroundColor = color;
        labelPanel.setBackground(backgroundColor);
        toolBarPanel.setBackground(backgroundColor);
    }


    public void setPinned(boolean b) {
        if (b) {
            setBannerColor(Color.GRAY);
        }
        else {
            setBannerColor(defaultBackgroundColor);
        }
    }


    /**
     * Removes all actions from the header.
     */
    public void removeAllActions() {
        toolBar.removeAll();
    }


    /**
     * Adds an action to the view header.
     * @param action The action to be added.
     */
    public void addAction(Action action) {
        String name = (String) action.getValue(AbstractAction.NAME);
        action.putValue(AbstractAction.NAME, "");
        action.putValue(AbstractAction.SHORT_DESCRIPTION, name);
        JButton button = new JButton(action) {
            public void updateUI() {
//                super.updateUI();
            }
        };
        button.setFocusable(false);
        toolBar.add(button);
        Icon icon = (Icon) action.getValue(AbstractAction.SMALL_ICON);
        if (icon != null) {
            button.setPreferredSize(new Dimension(icon.getIconWidth() + 2, icon.getIconHeight()));
            button.setOpaque(false);
            button.setUI(new ViewButtonUI());
            button.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        }
    }


    /**
     * Calls the UI delegate's paint method, if the UI delegate
     * is non-<code>null</code>.  We pass the delegate a copy of the
     * <code>Graphics</code> object to protect the rest of the
     * paint code from irrevocable changes
     * (for example, <code>Graphics.translate</code>).
     * <p/>
     * If you override this in a subclass you should not make permanent
     * changes to the passed in <code>Graphics</code>. For example, you
     * should not alter the clip <code>Rectangle</code> or modify the
     * transform. If you need to do these operations you may find it
     * easier to create a new <code>Graphics</code> from the passed in
     * <code>Graphics</code> and manipulate it. Further, if you do not
     * invoker super's implementation you must honor the opaque property,
     * that is
     * if this component is opaque, you must completely fill in the background
     * in a non-opaque color. If you do not honor the opaque property you
     * will likely see visual artifacts.
     * <p/>
     * The passed in <code>Graphics</code> object might
     * have a transform other than the identify transform
     * installed on it.  In this case, you might get
     * unexpected results if you cumulatively apply
     * another transform.
     * @param g the <code>Graphics</code> object to protect
     * @see #paint
     * @see javax.swing.plaf.ComponentUI
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Color oldColor = g.getColor();
//        Color colour;
//        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
//        if(focusOwner != null) {
//            Component view = SwingUtilities.getAncestorOfClass(View.class, this);
//            if(SwingUtilities.isDescendingFrom(focusOwner, view)) {
//                colour = backgroundColor;// new Color(90, 115, 130);
//            }
//            else {
//                colour = Color.GRAY;
//            }
//        }
//        else {
//            colour = Color.GRAY;
//        }
//        Graphics2D g2 = (Graphics2D) g;
//        GradientPaint gp = new GradientPaint(new Point(0, 0), colour, new Point(0, getHeight() / 2), colour.darker(), true);
//        Paint oldPaint = g2.getPaint();
//        g2.setPaint(gp);
//        g2.fillRect(0, 0, getWidth(), getHeight());
//        g2.setPaint(oldPaint);
//        g2.setColor(oldColor);
    }
}
