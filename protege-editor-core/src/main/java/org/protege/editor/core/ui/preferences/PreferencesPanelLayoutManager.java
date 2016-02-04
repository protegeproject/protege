package org.protege.editor.core.ui.preferences;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


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


    public PreferencesPanelLayoutManager(JComponent component) {
        compList = new ArrayList<>();
        labelMap = new HashMap<>();
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
        Insets insets = parent.getInsets();
        int prefWidth = insets.left + insets.right + GUTTER_WIDTH;
        int prefHeight = insets.top + insets.bottom;
        int maxLabelWidth = 0;
        for (JLabel label : labelMap.values()) {
            int labelPrefWidth = label.getPreferredSize().width;
            if (labelPrefWidth > maxLabelWidth) {
                maxLabelWidth = labelPrefWidth;
            }
        }
        prefWidth = prefWidth + maxLabelWidth;

        int maxComponentPrefWidth = 0;
        for (Iterator<Component> it = compList.iterator(); it.hasNext(); ) {
            Component c = it.next();
            Dimension prefCompSize = c.getPreferredSize();
            if(prefCompSize.width > maxComponentPrefWidth) {
                maxComponentPrefWidth = prefCompSize.width;
            }
            prefHeight = prefHeight + prefCompSize.height;
            if(it.hasNext()) {
                prefHeight = prefHeight + ROW_MARGIN;
            }
        }
        prefWidth += maxComponentPrefWidth;
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
            if (!(c instanceof PreferencesPanel)) {
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
}
