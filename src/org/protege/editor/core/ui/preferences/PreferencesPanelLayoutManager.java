package org.protege.editor.core.ui.preferences;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Date: 28-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesPanelLayoutManager implements LayoutManager2 {

    public static final int GUTTER_WIDTH = 10;

    public static final int ROW_MARGIN = 10;

    private List<Component> compList;

    private Map<Component, JLabel> labelMap;

    private JComponent baseComponent;

    int prefHeight = 0;

    int prefWidth = 0;


    public PreferencesPanelLayoutManager(JComponent component) {
        compList = new ArrayList<Component>();
        labelMap = new HashMap<Component, JLabel>();
        this.baseComponent = component;
    }


    public void addLayoutComponent(Component comp, Object constraints) {
        if (labelMap.values().contains(comp)) {
            // A label that we have added - ignore
            return;
        }
        compList.add(comp);
        if (constraints != null) {
            JLabel label = new JLabel((String) constraints);
            labelMap.put(comp, label);
            baseComponent.add(label);
        }
    }


    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    public float getLayoutAlignmentX(Container target) {
        return 0;
    }


    public float getLayoutAlignmentY(Container target) {
        return 0;
    }


    public void invalidateLayout(Container target) {

    }


    public void addLayoutComponent(String name, Component comp) {
    }


    public void removeLayoutComponent(Component comp) {
        labelMap.remove(comp);
        compList.remove(comp);
    }


    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(prefWidth, prefHeight);
    }


    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }


    public void layoutContainer(Container parent) {
        int maxWidth = 0;
        for (JLabel label : labelMap.values()) {
            int labelPrefWidth = label.getPreferredSize().width;
            if (labelPrefWidth > maxWidth) {
                maxWidth = labelPrefWidth;
            }
        }
        Insets insets = parent.getInsets();
        int curY = 0;
        int curX = 0;
        int maxY = parent.getHeight();
        int maxCompWidth = parent.getWidth() - maxWidth - insets.left - insets.right - GUTTER_WIDTH;
        curY = insets.top;
        curX = insets.left;
        maxY = maxY - insets.bottom;
        for (Component c : compList) {
            if (c instanceof PreferencesPanel == false) {
                JLabel label = labelMap.get(c);
                if (label != null) {
                    Dimension labelPrefSize = label.getPreferredSize();
                    label.setSize(labelPrefSize);
                    label.setLocation(curX + maxWidth - labelPrefSize.width, curY + 2);
                }
                Dimension prefCompSize = c.getPreferredSize();
                c.setSize(prefCompSize.width < maxCompWidth ? prefCompSize.width : maxCompWidth, prefCompSize.height);
                c.setLocation(curX + maxWidth + GUTTER_WIDTH, curY);
                curY = curY + prefCompSize.height + ROW_MARGIN;
            }
            else {
                c.setLocation(curX, curY);
                Dimension prefSize = c.getPreferredSize();
                c.setSize(parent.getWidth(), prefSize.height);
                curY = curY + prefSize.height + ROW_MARGIN;
            }
        }
    }


    public static void main(String[] args) {
        JPanel panel = new JPanel();
        panel.setLayout(new PreferencesPanelLayoutManager(panel));
        panel.add(new JTextField(30), "Base URI");
        panel.add(new JCheckBox("Include year"));
        panel.add(new JCheckBox("Include month"));
        panel.add(new JCheckBox("Include day"));
        panel.add(new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension(20, 20);
            }
        });
        panel.add(new JSeparator() {
            public Dimension getPreferredSize() {
                return new Dimension(100, 5);
            }
        });
        panel.setBorder(BorderFactory.createMatteBorder(30, 30, 30, 30, Color.MAGENTA));
        JList list = new JList(new Object []{"A", "B", "C"});
        list.setPreferredSize(new Dimension(300, 200));
        panel.add(list, "Choose a value for me");

        JFrame f = new JFrame();
        f.setContentPane(panel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
