package org.protege.editor.core.ui.util;

import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import org.protege.editor.core.ui.laf.CheckBoxMenuItemIcon;
import org.protege.editor.core.ui.laf.ProtegeScrollBarUI;
import org.protege.editor.core.ui.laf.RadioButtonMenuItemIcon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;


/**
 *  @author Ray Fergerson
 *  
 */
public class ProtegePlasticTheme extends ExperienceBlue {
	
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Border lineBorder = BorderFactory.createLineBorder(getSecondary1(), 1, true);
        Border controlBorder = BorderFactory.createCompoundBorder(lineBorder,
                                                                  BorderFactory.createEmptyBorder(2, 5, 2, 5)
        );
        Color scrollBarThumbColor = new Color(200, 200, 200);
        Object[] uiDefaults = {
        		"Tree.expandedIcon", Icons.getIcon("hierarchy.expanded.gif"), 
        		"Tree.collapsedIcon", Icons.getIcon("hierarchy.collapsed.gif"),
                "Button.is3DEnabled", false,
                "Button.background", new Color(255, 255, 255),
                "Button.border", controlBorder,
                "CheckBoxMenuItem.checkIcon", new CheckBoxMenuItemIcon(),
                "RadioButtonMenuItem.checkIcon", new RadioButtonMenuItemIcon(),
                "ComboBox.background", new Color(255, 255, 255),
                "ComboBox.border", lineBorder,
                "ComboBox.arrowButtonBorder", BorderFactory.createEmptyBorder(),
                "Toolbar.background", new Color(255, 255, 255),
                "Separator.ui", "javax.swing.plaf.basic.BasicSeparatorUI",
                "Separator.background", getSecondary3(),
                "Menu.borderPainted", false,
                "MenuItem.disabledForeground", new Color(180, 180, 180),
                "ScrollPane.background", Color.WHITE,
                "ScrollBarUI", ProtegeScrollBarUI.class.getName(),
                "scrollbar", getSecondary3(),
                "Scrollbar.maxBumpsWidth", 0,
                "ScrollBar.squareButtons", false,
                "ScrollBar.border", BorderFactory.createEmptyBorder(),
                "ScrollBar.highlight", scrollBarThumbColor,
                "ScrollBar.foreground", Color.WHITE,
                "ScrollBar.darkShadow", scrollBarThumbColor,
                "ScrollBar.shadow", scrollBarThumbColor,
                "ScrollBar.thumb", scrollBarThumbColor,
                "ScrollBar.thumbDarkShadow", scrollBarThumbColor,
                "ScrollBar.thumbHighlight", scrollBarThumbColor,
                "ScrollBar.thumbShadow", scrollBarThumbColor,
                "ScrollBar.trackForeground", Color.WHITE,
                "ScrollBar.trackHighlight", Color.WHITE,
                "ScrollBar.trackHighlightForeground", Color.WHITE,
                "ScrollBar.width", 12,
                "ScrollBar.track", Color.WHITE,
                "ToolTip.background", new Color(250, 250, 250),
                "ToolTip.border", lineBorder,
                "TitledBorder.titleColor", getMenuForeground(),
                
                // Add comprehensive light theme color definitions to match original Protégé appearance
                
                // Annotation-specific colors for light theme (matching original)
                "Annotation.propertyForeground", new ColorUIResource(0, 80, 160),     // Dark blue for properties
                "Annotation.labelForeground", new ColorUIResource(80, 80, 80),        // Dark grey for labels
                "Annotation.contentForeground", new ColorUIResource(0, 0, 0),         // Black for content
                
                // Import dialog colors (matching original)
                "Imports.valueForeground", new ColorUIResource(0, 0, 0),              // Black text
                "Imports.iriForeground", new ColorUIResource(0, 0, 0),                // Black for IRIs (original style)
                "Imports.labelForeground", new ColorUIResource(128, 128, 128),        // Grey for labels
                
                // Ontology URI colors - black in light theme for readability
                "OntologyURI.foreground", new ColorUIResource(0, 0, 0),               // Black for ontology URIs in light theme
                
                // Link colors for light theme
                "Link.foreground", new ColorUIResource(0, 100, 200),                  // Standard blue links
                "Link.hoverForeground", new ColorUIResource(0, 80, 160),              // Darker blue on hover
                
                // Metrics panel colors (matching original light theme)
                "Metrics.titleForeground", new ColorUIResource(0, 0, 0),              // Black titles
                "Metrics.labelForeground", new ColorUIResource(0, 0, 0),              // Black labels (original)
                "Metrics.valueForeground", new ColorUIResource(0, 0, 0),              // Black values (original)
                "Metrics.zeroValueForeground", new ColorUIResource(160, 160, 160),    // Light grey for zero values
                "Metrics.gridColor", new ColorUIResource(220, 220, 220),              // Light grey grid lines (original)
                
                // Standard Swing component colors for light theme
                "Panel.background", Color.WHITE,
                "Panel.foreground", Color.BLACK,
                "Label.foreground", Color.BLACK,
                "List.background", Color.WHITE,
                "List.foreground", Color.BLACK,
                "Table.background", Color.WHITE,
                "Table.foreground", Color.BLACK,
                "Table.gridColor", new ColorUIResource(220, 220, 220),                // Light grey grid
                "Tree.background", Color.WHITE,
                "Tree.foreground", Color.BLACK,
                "Tree.textForeground", Color.BLACK,
                "TextPane.background", Color.WHITE,
                "TextPane.foreground", Color.BLACK,
                "TextField.background", Color.WHITE,
                "TextField.foreground", Color.BLACK,
                "TextArea.background", Color.WHITE,
                "TextArea.foreground", Color.BLACK,
                
                // Container backgrounds - important for avoiding black rectangles
                "Container.background", Color.WHITE,
                "Container.foreground", Color.BLACK,
                "JComponent.background", Color.WHITE,
                "JComponent.foreground", Color.BLACK,
                "Component.background", Color.WHITE,
                "Component.foreground", Color.BLACK,
                "Viewport.background", Color.WHITE,
                "Viewport.foreground", Color.BLACK,
                "ScrollPane.background", Color.WHITE,
                "ScrollPane.foreground", Color.BLACK,
                
                // Ensure text color consistency
                "textText", Color.BLACK,
                "controlText", Color.BLACK,
                "text", Color.BLACK,
                
                // Force override any potential dark backgrounds
                "background", Color.WHITE,
                "Background", Color.WHITE,
                "BACKGROUND", Color.WHITE,
                
                // OptionPane colors - critical for preferences dialog background
                "OptionPane.background", Color.WHITE,
                "OptionPane.foreground", Color.BLACK,
                "OptionPane.messageForeground", Color.BLACK,
                "OptionPane.border", lineBorder,
                "OptionPane.buttonAreaBorder", BorderFactory.createEmptyBorder(),
                "OptionPane.messageAreaBorder", BorderFactory.createEmptyBorder(),
                
                // Dialog colors
                "Dialog.background", Color.WHITE,
                "Dialog.foreground", Color.BLACK,
                
                // Root pane and content pane
                "RootPane.background", Color.WHITE,
                "RootPane.foreground", Color.BLACK
        };
        table.putDefaults(uiDefaults);
    }

    // Enabled control lines
    protected ColorUIResource getSecondary1() {
        return new ColorUIResource(200, 200, 200);
    }

    // Disabled control lines
    protected ColorUIResource getSecondary2() {
        return new ColorUIResource(220, 220, 220);
    }

    protected ColorUIResource getSecondary3() {
        return new ColorUIResource(244, 244, 244);
    }

    /**
     * Returns the menu background color. This
     * returns the value of {@code getSecondary3()}.
     *
     * @return the menu background color
     */
    @Override
    public ColorUIResource getMenuBackground() {
        return new ColorUIResource(237, 237, 237);
    }

    @Override
    public ColorUIResource getMenuItemBackground() {
        return getMenuBackground();
    }

    @Override
    public FontUIResource getTitleTextFont() {
        return new FontUIResource("Dialog", Font.BOLD, 14);
    }

    @Override
    public FontUIResource getControlTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 13);
    }

    @Override
    public FontUIResource getMenuTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 13);
    }

    @Override
    public FontUIResource getSubTextFont() {
        return new FontUIResource("Dialog", Font.BOLD, 12);
    }

    @Override
    public FontUIResource getSystemTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getUserTextFont() {
        return new FontUIResource("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getWindowTitleFont() {
        return new FontUIResource("Dialog", Font.BOLD, 12);
    }
}