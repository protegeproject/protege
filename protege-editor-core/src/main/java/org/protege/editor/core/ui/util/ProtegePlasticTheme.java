package org.protege.editor.core.ui.util;

import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;



/**
 *  @author Ray Fergerson
 *  
 */
public class ProtegePlasticTheme extends ExperienceBlue {
	
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults = { 
        		"Tree.expandedIcon", Icons.getIcon("hierarchy.expanded.gif"), 
        		"Tree.collapsedIcon", Icons.getIcon("hierarchy.collapsed.gif"), 
//        		"Table.selectionForeground", getMenuItemSelectedForeground(),
//                "Table.selectionBackground", getMenuItemSelectedBackground(),
//                "List.selectionForeground", getMenuItemSelectedForeground(),
//                "List.selectionBackground", getMenuItemSelectedBackground(),
//                "Tree.selectionForeground", getMenuItemSelectedForeground(),
//                "Tree.selectionBackground", getMenuItemSelectedBackground(),
        };
        table.putDefaults(uiDefaults);
    }

    protected ColorUIResource getSecondary1() {
        return new ColorUIResource(168, 168, 168);
    }

    protected ColorUIResource getSecondary2() {
        return new ColorUIResource(220, 220, 220);
    }

    protected ColorUIResource getSecondary3() {
        return new ColorUIResource(236, 236, 236);
    }

    /**
     * Returns the menu background color. This
     * returns the value of {@code getSecondary3()}.
     *
     * @return the menu background color
     */
    @Override
    public ColorUIResource getMenuBackground() {
        return new ColorUIResource(246, 246, 246);
    }

    @Override
    public ColorUIResource getMenuItemBackground() {
        return getMenuBackground();
    }
}