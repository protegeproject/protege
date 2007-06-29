package org.protege.editor.core.ui.util;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;

import com.jgoodies.looks.plastic.theme.*;



/**
 *  @author Ray Fergerson
 *  
 */
public class ProtegePlasticTheme extends ExperienceBlue {
	
	public final static int DEFAULT_FONT_SIZE = 11;
	public final static Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, DEFAULT_FONT_SIZE);

    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults = { 
        		"Tree.expandedIcon", Icons.getIcon("hierarchy.expanded.gif"), 
        		"Tree.collapsedIcon", Icons.getIcon("hierarchy.collapsed.gif"), 
        		"Table.selectionForeground", getMenuItemSelectedForeground(),
                "Table.selectionBackground", getMenuItemSelectedBackground(), 
                "List.selectionForeground", getMenuItemSelectedForeground(), 
                "List.selectionBackground", getMenuItemSelectedBackground(),
                "Tree.selectionForeground", getMenuItemSelectedForeground(), 
                "Tree.selectionBackground", getMenuItemSelectedBackground(), 
        };
        table.putDefaults(uiDefaults);
    }
    
    public ColorUIResource getMenuItemSelectedBackground() {
        return getPrimary3();
    }

    public ColorUIResource getMenuItemSelectedForeground() {
        return new ColorUIResource(Color.BLACK);
    }

    public ColorUIResource getMenuSelectedBackground() {
        return getMenuItemSelectedBackground();
    }

    public ColorUIResource getMenuSelectedForeground() {
        return getMenuItemSelectedForeground();
    }
}