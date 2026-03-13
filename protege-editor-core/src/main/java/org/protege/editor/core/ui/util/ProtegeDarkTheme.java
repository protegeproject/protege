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
 * Dark theme for Protégé based on the Plastic Look and Feel.
 * Provides a modern dark color scheme for improved readability in low-light environments.
 * 
 * @author Protégé Dark Theme Implementation
 */
public class ProtegeDarkTheme extends ExperienceBlue {
    
    // Dark theme color palette
    private static final ColorUIResource DARK_BACKGROUND = new ColorUIResource(45, 45, 45);
    private static final ColorUIResource DARKER_BACKGROUND = new ColorUIResource(35, 35, 35);
    private static final ColorUIResource DARKEST_BACKGROUND = new ColorUIResource(25, 25, 25);
    private static final ColorUIResource DARK_FOREGROUND = new ColorUIResource(230, 230, 230);  // Brighter text for better visibility
    private static final ColorUIResource DARK_BORDER = new ColorUIResource(70, 70, 70);
    private static final ColorUIResource DARK_SELECTION = new ColorUIResource(75, 110, 175);
    private static final ColorUIResource DARK_SELECTION_FOREGROUND = new ColorUIResource(255, 255, 255);
    private static final ColorUIResource DARK_DISABLED = new ColorUIResource(140, 140, 140);  // Slightly brighter disabled text
    private static final ColorUIResource DARK_SCROLLBAR = new ColorUIResource(80, 80, 80);
    private static final ColorUIResource DARK_TOOLTIP = new ColorUIResource(60, 60, 60);
    private static final ColorUIResource TAB_BACKGROUND = new ColorUIResource(55, 55, 55);     // Better tab background
    private static final ColorUIResource TAB_FOREGROUND = new ColorUIResource(200, 200, 200);  // Good contrast for tabs
    private static final ColorUIResource TAB_SELECTED_BACKGROUND = new ColorUIResource(70, 70, 70);  // Selected tab background
    private static final ColorUIResource TREE_LINE_COLOR = new ColorUIResource(235, 235, 235);
    
    @Override
    public void addCustomEntriesToTable(UIDefaults table) {
        // Don't call super to avoid light theme defaults interfering
        // super.addCustomEntriesToTable(table);
        
        Border lineBorder = BorderFactory.createLineBorder(getDarkBorder(), 1, true);
        Border controlBorder = BorderFactory.createCompoundBorder(lineBorder,
                                                                  BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        Object[] uiDefaults = {
            // Tree icons
            "Tree.expandedIcon", Icons.getIcon("hierarchy.expanded.gif"), 
            "Tree.collapsedIcon", Icons.getIcon("hierarchy.collapsed.gif"),
            
            // Button styling
            "Button.is3DEnabled", false,
            "Button.background", DARK_BACKGROUND,
            "Button.foreground", DARK_FOREGROUND,
            "Button.border", controlBorder,
            "Button.darkShadow", DARK_BORDER,
            "Button.light", DARK_BACKGROUND,
            "Button.shadow", DARK_BORDER,
            
            // Menu items
            "CheckBoxMenuItem.checkIcon", new CheckBoxMenuItemIcon(),
            "RadioButtonMenuItem.checkIcon", new RadioButtonMenuItemIcon(),
            "Menu.background", DARKER_BACKGROUND,
            "Menu.foreground", DARK_FOREGROUND,
            "Menu.borderPainted", false,
            "MenuItem.background", DARKER_BACKGROUND,
            "MenuItem.foreground", DARK_FOREGROUND,
            "MenuItem.disabledForeground", DARK_DISABLED,
            "MenuItem.selectionBackground", DARK_SELECTION,
            "MenuItem.selectionForeground", DARK_SELECTION_FOREGROUND,
            "MenuBar.background", DARKER_BACKGROUND,
            "MenuBar.foreground", DARK_FOREGROUND,
            
            // ComboBox
            "ComboBox.background", DARK_BACKGROUND,
            "ComboBox.foreground", DARK_FOREGROUND,
            "ComboBox.border", lineBorder,
            "ComboBox.arrowButtonBorder", BorderFactory.createEmptyBorder(),
            "ComboBox.selectionBackground", DARK_SELECTION,
            "ComboBox.selectionForeground", DARK_SELECTION_FOREGROUND,
            "ComboBox.buttonBackground", DARK_BACKGROUND,
            "ComboBox.buttonShadow", DARK_BORDER,
            "ComboBox.buttonDarkShadow", DARK_BORDER,
            "ComboBox.buttonHighlight", DARK_BACKGROUND,
            
            // Text components
            "TextField.background", DARK_BACKGROUND,
            "TextField.foreground", DARK_FOREGROUND,
            "TextField.border", lineBorder,
            "TextField.selectionBackground", DARK_SELECTION,
            "TextField.selectionForeground", DARK_SELECTION_FOREGROUND,
            "TextField.inactiveBackground", DARK_BACKGROUND,
            "TextField.inactiveForeground", DARK_DISABLED,
            "TextArea.background", DARK_BACKGROUND,
            "TextArea.foreground", DARK_FOREGROUND,
            "TextArea.selectionBackground", DARK_SELECTION,
            "TextArea.selectionForeground", DARK_SELECTION_FOREGROUND,
            "TextArea.inactiveBackground", DARK_BACKGROUND,
            "TextArea.inactiveForeground", DARK_DISABLED,
            "TextPane.background", DARK_BACKGROUND,
            "TextPane.foreground", DARK_FOREGROUND,
            "EditorPane.background", DARK_BACKGROUND,
            "EditorPane.foreground", DARK_FOREGROUND,
            
            // Lists and Tables
            "List.background", DARK_BACKGROUND,
            "List.foreground", DARK_FOREGROUND,
            "List.selectionBackground", DARK_SELECTION,
            "List.selectionForeground", DARK_SELECTION_FOREGROUND,
            "Table.background", DARK_BACKGROUND,
            "Table.foreground", DARK_FOREGROUND,
            "Table.selectionBackground", DARK_SELECTION,
            "Table.selectionForeground", DARK_SELECTION_FOREGROUND,
            "Table.gridColor", DARK_BORDER,
            "Table.focusCellBackground", DARK_SELECTION,
            "Table.focusCellForeground", DARK_SELECTION_FOREGROUND,
            "TableHeader.background", DARKER_BACKGROUND,
            "TableHeader.foreground", DARK_FOREGROUND,
            
            // Tree - Enhanced for better text visibility
            "Tree.background", DARK_BACKGROUND,
            "Tree.foreground", DARK_FOREGROUND,
            "Tree.selectionBackground", DARK_SELECTION,
            "Tree.selectionForeground", DARK_SELECTION_FOREGROUND,
            "Tree.line", TREE_LINE_COLOR,
            "Tree.hash", TREE_LINE_COLOR,
            "Tree.textBackground", DARK_BACKGROUND,
            "Tree.textForeground", DARK_FOREGROUND,
            "Tree.selectionBorderColor", TREE_LINE_COLOR,
            "Tree.dropLineColor", TREE_LINE_COLOR,
            "Tree.editorBorder", lineBorder,
            "Tree.leftChildIndent", 7,
            "Tree.rightChildIndent", 13,
            "Tree.rowHeight", 0,
            "Tree.scrollsOnExpand", true,
            "Tree.openIcon", Icons.getIcon("hierarchy.expanded.gif"),
            "Tree.closedIcon", Icons.getIcon("hierarchy.collapsed.gif"),
            "Tree.leafIcon", null,
            
            // Panel and containers - IMPORTANT for fixing white backgrounds
            "Panel.background", DARK_BACKGROUND,
            "Panel.foreground", DARK_FOREGROUND,
            "Container.background", DARK_BACKGROUND,
            "Container.foreground", DARK_FOREGROUND,
            "JComponent.background", DARK_BACKGROUND,
            "JComponent.foreground", DARK_FOREGROUND,
            "Component.background", DARK_BACKGROUND,
            "Component.foreground", DARK_FOREGROUND,
            
            // Viewport (used in scroll panes)
            "Viewport.background", DARK_BACKGROUND,
            "Viewport.foreground", DARK_FOREGROUND,
            
            // Window and Frame
            "window", DARK_BACKGROUND,
            "activeCaption", DARK_SELECTION,
            "activeCaptionText", DARK_SELECTION_FOREGROUND,
            "activeCaptionBorder", DARK_BORDER,
            "inactiveCaption", DARKER_BACKGROUND,
            "inactiveCaptionText", DARK_FOREGROUND,
            "inactiveCaptionBorder", DARK_BORDER,
            
            // Desktop and internal frames
            "desktop", DARK_BACKGROUND,
            "InternalFrame.background", DARK_BACKGROUND,
            "InternalFrame.border", lineBorder,
            "InternalFrame.titleBackground", DARKER_BACKGROUND,
            "InternalFrame.titleForeground", DARK_FOREGROUND,
            
            // Toolbar
            "Toolbar.background", DARKER_BACKGROUND,
            "Toolbar.foreground", DARK_FOREGROUND,
            "ToolBar.background", DARKER_BACKGROUND,
            "ToolBar.foreground", DARK_FOREGROUND,
            "ToolBar.border", lineBorder,
            "ToolBar.dockingBackground", DARKER_BACKGROUND,
            "ToolBar.dockingForeground", DARK_FOREGROUND,
            "ToolBar.floatingBackground", DARKER_BACKGROUND,
            "ToolBar.floatingForeground", DARK_FOREGROUND,
            
            // Separator
            "Separator.ui", "javax.swing.plaf.basic.BasicSeparatorUI",
            "Separator.background", DARK_BORDER,
            "Separator.foreground", DARK_BORDER,
            "Separator.shadow", DARK_BORDER,
            "Separator.highlight", DARK_BACKGROUND,
            
            // ScrollPane and ScrollBar
            "ScrollPane.background", DARK_BACKGROUND,
            "ScrollPane.foreground", DARK_FOREGROUND,
            "ScrollPane.border", lineBorder,
            "ScrollBarUI", ProtegeScrollBarUI.class.getName(),
            "scrollbar", DARK_SCROLLBAR,
            "Scrollbar.maxBumpsWidth", 0,
            "ScrollBar.squareButtons", false,
            "ScrollBar.border", BorderFactory.createEmptyBorder(),
            "ScrollBar.background", DARK_BACKGROUND,
            "ScrollBar.foreground", DARK_BACKGROUND,
            "ScrollBar.highlight", DARK_SCROLLBAR,
            "ScrollBar.darkShadow", DARK_SCROLLBAR,
            "ScrollBar.shadow", DARK_SCROLLBAR,
            "ScrollBar.thumb", DARK_SCROLLBAR,
            "ScrollBar.thumbDarkShadow", DARK_SCROLLBAR,
            "ScrollBar.thumbHighlight", DARK_SCROLLBAR,
            "ScrollBar.thumbShadow", DARK_SCROLLBAR,
            "ScrollBar.trackForeground", DARK_BACKGROUND,
            "ScrollBar.trackHighlight", DARK_BACKGROUND,
            "ScrollBar.trackHighlightForeground", DARK_BACKGROUND,
            "ScrollBar.width", 12,
            "ScrollBar.track", DARK_BACKGROUND,
            
            // CheckBox and RadioButton
            "CheckBox.background", DARK_BACKGROUND,
            "CheckBox.foreground", DARK_FOREGROUND,
            "CheckBox.select", DARK_SELECTION,
            "RadioButton.background", DARK_BACKGROUND,
            "RadioButton.foreground", DARK_FOREGROUND,
            "RadioButton.select", DARK_SELECTION,
            
            // Labels
            "Label.background", DARK_BACKGROUND,
            "Label.foreground", DARK_FOREGROUND,
            "Label.disabledForeground", DARK_DISABLED,
            
            // TabbedPane - Enhanced for better visibility
            "TabbedPane.background", TAB_BACKGROUND,
            "TabbedPane.foreground", TAB_FOREGROUND,
            "TabbedPane.selected", TAB_SELECTED_BACKGROUND,
            "TabbedPane.selectedForeground", DARK_FOREGROUND,
            "TabbedPane.tabAreaBackground", TAB_BACKGROUND,
            "TabbedPane.contentAreaColor", DARK_BACKGROUND,
            "TabbedPane.contentBorderInsets", new java.awt.Insets(1, 1, 1, 1),
            "TabbedPane.contentOpaque", true,
            "TabbedPane.tabsOpaque", true,
            "TabbedPane.unselectedBackground", TAB_BACKGROUND,
            "TabbedPane.unselectedTabBackground", TAB_BACKGROUND,
            "TabbedPane.unselectedTabForeground", TAB_FOREGROUND,
            "TabbedPane.unselectedTabHighlight", toUIResource(TAB_BACKGROUND.brighter()),
            "TabbedPane.unselectedTabShadow", DARK_BORDER,
            "TabbedPane.selectedTabPadInsets", new java.awt.Insets(2, 2, 2, 1),
            "TabbedPane.tabAreaInsets", new java.awt.Insets(2, 2, 0, 2),
            "TabbedPane.tabInsets", new java.awt.Insets(1, 4, 1, 4),
            "TabbedPane.focus", DARK_SELECTION,
            "TabbedPane.borderHightlightColor", DARK_BORDER,
            "TabbedPane.darkShadow", DARK_BORDER,
            "TabbedPane.light", toUIResource(TAB_BACKGROUND.brighter()),
            "TabbedPane.highlight", toUIResource(TAB_BACKGROUND.brighter()),
            "TabbedPane.shadow", DARK_BORDER,
            "TabbedPane.selectHighlight", DARK_SELECTION,
            // Force tab text colors
            "TabbedPane.tabText", TAB_FOREGROUND,
            "TabbedPane.selectedTabText", DARK_FOREGROUND,
            
            // SplitPane
            "SplitPane.background", DARK_BACKGROUND,
            "SplitPane.highlight", DARK_BORDER,
            "SplitPane.shadow", DARK_BORDER,
            "SplitPane.darkShadow", DARK_BORDER,
            "SplitPaneDivider.background", DARK_BORDER,
            "SplitPaneDivider.border", BorderFactory.createEmptyBorder(),
            
            // ToolTip
            "ToolTip.background", DARK_TOOLTIP,
            "ToolTip.foreground", DARK_FOREGROUND,
            "ToolTip.border", lineBorder,
            
            // TitledBorder
            "TitledBorder.titleColor", DARK_FOREGROUND,
            "TitledBorder.border", lineBorder,
            
            // OptionPane
            "OptionPane.background", DARK_BACKGROUND,
            "OptionPane.foreground", DARK_FOREGROUND,
            "OptionPane.messageForeground", DARK_FOREGROUND,
            "OptionPane.border", lineBorder,
            
            // ProgressBar
            "ProgressBar.background", DARK_BACKGROUND,
            "ProgressBar.foreground", DARK_SELECTION,
            "ProgressBar.selectionBackground", DARK_FOREGROUND,
            "ProgressBar.selectionForeground", DARK_BACKGROUND,
            "ProgressBar.border", lineBorder,
            
            // Slider
            "Slider.background", DARK_BACKGROUND,
            "Slider.foreground", DARK_FOREGROUND,
            "Slider.highlight", DARK_SELECTION,
            "Slider.shadow", DARK_BORDER,
            
            // Spinner
            "Spinner.background", DARK_BACKGROUND,
            "Spinner.foreground", DARK_FOREGROUND,
            "Spinner.border", lineBorder,
            
            // Control colors - these are very important for overall theming
            "control", DARK_BACKGROUND,
            "controlText", DARK_FOREGROUND,
            "controlHighlight", toUIResource(DARK_BACKGROUND.brighter()),
            "controlLtHighlight", toUIResource(DARK_BACKGROUND.brighter().brighter()),
            "controlShadow", DARK_BORDER,
            "controlDkShadow", toUIResource(DARK_BORDER.darker()),
            
            // General text colors
            "text", DARK_FOREGROUND,
            "textText", DARK_FOREGROUND,
            "textHighlight", DARK_SELECTION,
            "textHighlightText", DARK_SELECTION_FOREGROUND,
            "textInactiveText", DARK_DISABLED,
            
            // General background
            "info", DARK_TOOLTIP,
            "infoText", DARK_FOREGROUND,
            
            // Additional components that might show white
            "FormattedTextField.background", DARK_BACKGROUND,
            "FormattedTextField.foreground", DARK_FOREGROUND,
            "FormattedTextField.inactiveBackground", DARK_BACKGROUND,
            "FormattedTextField.inactiveForeground", DARK_DISABLED,
            
            // PopupMenu
            "PopupMenu.background", DARKER_BACKGROUND,
            "PopupMenu.foreground", DARK_FOREGROUND,
            "PopupMenu.border", lineBorder,
            
            // FileChooser
            "FileChooser.background", DARK_BACKGROUND,
            "FileChooser.foreground", DARK_FOREGROUND,
            
            // ColorChooser
            "ColorChooser.background", DARK_BACKGROUND,
            "ColorChooser.foreground", DARK_FOREGROUND,
            
            // RootPane
            "RootPane.background", DARK_BACKGROUND,
            "RootPane.foreground", DARK_FOREGROUND,
            
            // Border styling for various components
            "Border.background", DARK_BACKGROUND,
            "Border.foreground", DARK_BORDER,
            
            // Additional panel types
            "TitledBorder.background", DARK_BACKGROUND,
            "TitledBorder.foreground", DARK_FOREGROUND,
            
            // Focus colors
            "focus", DARK_SELECTION,
            "focusInputMap", DARK_SELECTION,
            
            // White area fixes - targeting potential Protégé-specific components
            "white", DARK_BACKGROUND,
            "White", DARK_BACKGROUND,
            "nimbusBase", DARK_BACKGROUND,
            "nimbusFocus", DARK_SELECTION,
            "nimbusLightBackground", DARK_BACKGROUND,
            "nimbusSelectionBackground", DARK_SELECTION,
            
            // HTML and Rich Text components
            "html.background", DARK_BACKGROUND,
            "html.foreground", DARK_FOREGROUND,
            
            // Additional text components
            "EditorPane.inactiveBackground", DARK_BACKGROUND,
            "EditorPane.inactiveForeground", DARK_DISABLED,
            "TextPane.inactiveBackground", DARK_BACKGROUND,
            "TextPane.inactiveForeground", DARK_DISABLED,
            
            // More comprehensive component coverage
            "ArrowButton.background", DARK_BACKGROUND,
            "ArrowButton.foreground", DARK_FOREGROUND,
            
            // Custom UI defaults that might be used by Protégé
            "OWL.background", DARK_BACKGROUND,
            "OWL.foreground", DARK_FOREGROUND,
            "Protege.background", DARK_BACKGROUND,
            "Protege.foreground", DARK_FOREGROUND,
            
            // Annotation-specific colors for better readability
            "Annotation.propertyForeground", new ColorUIResource(190, 170, 255),  // Soft purple for properties
            "Annotation.labelForeground", new ColorUIResource(200, 200, 200),     // Light gray for labels
            "Annotation.contentForeground", new ColorUIResource(230, 230, 230),   // Brighter for content
            
            // Import dialog colors
            "Imports.valueForeground", new ColorUIResource(220, 220, 220),        // Standard text
            "Imports.iriForeground", new ColorUIResource(200, 180, 255),          // Purple tint for IRIs
            "Imports.labelForeground", new ColorUIResource(180, 180, 180),        // Dimmer for labels
            
            // Ontology URI colors
            "OntologyURI.foreground", new ColorUIResource(180, 180, 180),         // Dimmed for URIs in dark theme
            
            // Link colors for consistency
            "Link.foreground", new ColorUIResource(190, 170, 255),                // Purple links
            "Link.hoverForeground", new ColorUIResource(225, 205, 255),           // Brighter on hover
            
            // Metrics panel colors for better contrast
            "Metrics.titleForeground", new ColorUIResource(215, 205, 240),        // Soft purple for titles
            "Metrics.labelForeground", new ColorUIResource(200, 200, 200),        // Standard for labels
            "Metrics.valueForeground", new ColorUIResource(235, 235, 235),        // Bright for values
            "Metrics.zeroValueForeground", new ColorUIResource(140, 140, 140),    // Dimmed for zero values
            "Metrics.gridColor", new ColorUIResource(70, 70, 70),                 // Subtle grid lines
            
            // Force override common white-causing properties
            "contentAreaColor", DARK_BACKGROUND,
            "contentBackground", DARK_BACKGROUND,
            "activeBackground", DARK_BACKGROUND,
            "inactiveBackground", DARK_BACKGROUND,
            "defaultBackground", DARK_BACKGROUND,
            
            // Browser and web components (if any)
            "Browser.background", DARK_BACKGROUND,
            "Browser.foreground", DARK_FOREGROUND,
            
            // Additional Swing components that might show white
            "InternalFrame.inactiveTitleBackground", DARKER_BACKGROUND,
            "InternalFrame.inactiveTitleForeground", DARK_FOREGROUND,
            "InternalFrame.activeTitleBackground", DARK_SELECTION,
            "InternalFrame.activeTitleForeground", DARK_SELECTION_FOREGROUND,
            
            // Dialog components
            "Dialog.background", DARK_BACKGROUND,
            "Dialog.foreground", DARK_FOREGROUND,
            
            // Additional panel and container types
            "ContentPane.background", DARK_BACKGROUND,
            "ContentPane.foreground", DARK_FOREGROUND,
            "LayeredPane.background", DARK_BACKGROUND,
            "LayeredPane.foreground", DARK_FOREGROUND,
            "GlassPane.background", DARK_BACKGROUND,
            "GlassPane.foreground", DARK_FOREGROUND,
            
            // Force all possible white backgrounds to dark
            "background", DARK_BACKGROUND,
            "Background", DARK_BACKGROUND,
            "BACKGROUND", DARK_BACKGROUND
        };
        table.putDefaults(uiDefaults);
        
        // Force override any remaining white or light colors that might slip through
        java.util.Enumeration<?> keys = table.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = table.get(key);
            
            // Override any Color values that are white or very light
            if (value instanceof java.awt.Color) {
                java.awt.Color color = (java.awt.Color) value;
                if (isLightColor(color)) {
                    // Replace light colors with appropriate dark equivalents
                    if (key.toString().toLowerCase().contains("background") || 
                        key.toString().toLowerCase().contains("base") ||
                        color.equals(java.awt.Color.WHITE)) {
                        table.put(key, DARK_BACKGROUND);
                    } else if (key.toString().toLowerCase().contains("foreground") || 
                               key.toString().toLowerCase().contains("text") ||
                               color.equals(java.awt.Color.BLACK)) {
                        table.put(key, DARK_FOREGROUND);
                    } else if (key.toString().toLowerCase().contains("selection")) {
                        table.put(key, DARK_SELECTION);
                    }
                }
            }
        }
    }
    
    /**
     * Helper method to determine if a color is too light for dark theme
     */
    private boolean isLightColor(java.awt.Color color) {
        // Calculate brightness using standard formula
        double brightness = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
        return brightness > 0.7; // Colors with brightness > 70% are considered light
    }

    private static ColorUIResource toUIResource(Color color) {
        return color instanceof ColorUIResource ? (ColorUIResource) color : new ColorUIResource(color);
    }

    private Color getDarkBorder() {
        return DARK_BORDER;
    }

    // Override primary colors for dark theme
    @Override
    protected ColorUIResource getPrimary1() {
        return new ColorUIResource(DARK_SELECTION);
    }

    @Override
    protected ColorUIResource getPrimary2() {
        return new ColorUIResource(DARK_SELECTION.brighter());
    }

    @Override
    protected ColorUIResource getPrimary3() {
        return new ColorUIResource(DARK_SELECTION.darker());
    }

    // Override secondary colors for dark theme
    @Override
    protected ColorUIResource getSecondary1() {
        return new ColorUIResource(DARK_BORDER);
    }

    @Override
    protected ColorUIResource getSecondary2() {
        return new ColorUIResource(DARK_BORDER.brighter());
    }

    @Override
    protected ColorUIResource getSecondary3() {
        return new ColorUIResource(DARKER_BACKGROUND);
    }

    @Override
    public ColorUIResource getMenuBackground() {
        return new ColorUIResource(DARKER_BACKGROUND);
    }

    @Override
    public ColorUIResource getMenuItemBackground() {
        return getMenuBackground();
    }

    @Override
    public ColorUIResource getMenuForeground() {
        return new ColorUIResource(DARK_FOREGROUND);
    }

    @Override
    public ColorUIResource getControlTextColor() {
        return new ColorUIResource(DARK_FOREGROUND);
    }

    @Override
    public ColorUIResource getSystemTextColor() {
        return new ColorUIResource(DARK_FOREGROUND);
    }

    @Override
    public ColorUIResource getUserTextColor() {
        return new ColorUIResource(DARK_FOREGROUND);
    }

    @Override
    public ColorUIResource getMenuItemSelectedBackground() {
        return new ColorUIResource(DARK_SELECTION);
    }

    @Override
    public ColorUIResource getMenuItemSelectedForeground() {
        return new ColorUIResource(DARK_SELECTION_FOREGROUND);
    }

    @Override
    public ColorUIResource getWindowBackground() {
        return new ColorUIResource(DARK_BACKGROUND);
    }

    @Override
    public ColorUIResource getDesktopColor() {
        return new ColorUIResource(DARK_BACKGROUND);
    }

    @Override
    public ColorUIResource getControl() {
        return new ColorUIResource(DARK_BACKGROUND);
    }

    @Override
    public ColorUIResource getControlShadow() {
        return new ColorUIResource(DARK_BORDER);
    }

    @Override
    public ColorUIResource getControlDarkShadow() {
        return new ColorUIResource(DARK_BORDER.darker());
    }

    @Override
    public ColorUIResource getControlInfo() {
        return new ColorUIResource(DARK_TOOLTIP);
    }

    @Override
    public ColorUIResource getControlHighlight() {
        return new ColorUIResource(DARK_BACKGROUND.brighter());
    }

    @Override
    public ColorUIResource getControlDisabled() {
        return new ColorUIResource(DARK_DISABLED);
    }


    @Override
    public ColorUIResource getInactiveControlTextColor() {
        return new ColorUIResource(DARK_DISABLED);
    }

    @Override
    public ColorUIResource getInactiveSystemTextColor() {
        return new ColorUIResource(DARK_DISABLED);
    }

    @Override
    public ColorUIResource getSeparatorBackground() {
        return new ColorUIResource(DARK_BORDER);
    }

    @Override
    public ColorUIResource getSeparatorForeground() {
        return new ColorUIResource(DARK_BORDER);
    }

    @Override
    public ColorUIResource getTextHighlightColor() {
        return new ColorUIResource(DARK_SELECTION);
    }

    @Override
    public ColorUIResource getHighlightedTextColor() {
        return new ColorUIResource(DARK_SELECTION_FOREGROUND);
    }

    @Override
    public ColorUIResource getFocusColor() {
        return new ColorUIResource(DARK_SELECTION);
    }

    @Override
    public ColorUIResource getMenuSelectedBackground() {
        return new ColorUIResource(DARK_SELECTION);
    }

    @Override
    public ColorUIResource getMenuSelectedForeground() {
        return new ColorUIResource(DARK_SELECTION_FOREGROUND);
    }

    @Override
    public ColorUIResource getAcceleratorForeground() {
        return new ColorUIResource(DARK_FOREGROUND);
    }

    @Override
    public ColorUIResource getAcceleratorSelectedForeground() {
        return new ColorUIResource(DARK_SELECTION_FOREGROUND);
    }

    // Override base theme methods to ensure dark backgrounds everywhere
    @Override
    public ColorUIResource getWhite() {
        return new ColorUIResource(DARK_BACKGROUND);
    }

    @Override
    public ColorUIResource getBlack() {
        return new ColorUIResource(DARK_FOREGROUND);
    }

    // Font definitions (same as light theme)
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