package org.protege.editor.core.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Centralized color provider for Protégé that ensures consistent color usage
 * across all components regardless of the current theme.
 * 
 * This class provides theme-aware color access with proper fallbacks,
 * eliminating the need for hardcoded colors throughout the application.
 * 
 * @author Protégé Color Provider Implementation
 */
public class ProtegeColorProvider {
    
    // Cache colors to avoid repeated UIManager lookups
    private static Color cachedTextColor = null;
    private static Color cachedBackgroundColor = null;
    private static Color cachedSelectionColor = null;
    private static String lastTheme = null;
    
    /**
     * Gets the primary text color for the current theme.
     * This should be used for all standard text rendering.
     * 
     * @return Color suitable for text in the current theme
     */
    public static Color getTextColor() {
        if (shouldUpdateCache()) {
            cachedTextColor = null;
        }
        
        if (cachedTextColor == null) {
            cachedTextColor = resolveTextColor();
        }
        return cachedTextColor;
    }
    
    /**
     * Gets the primary background color for the current theme.
     * This should be used for all panel and container backgrounds.
     * 
     * @return Color suitable for backgrounds in the current theme
     */
    public static Color getBackgroundColor() {
        if (shouldUpdateCache()) {
            cachedBackgroundColor = null;
        }
        
        if (cachedBackgroundColor == null) {
            cachedBackgroundColor = resolveBackgroundColor();
        }
        return cachedBackgroundColor;
    }
    
    /**
     * Gets the selection color for the current theme.
     * This should be used for highlighting selected items.
     * 
     * @return Color suitable for selections in the current theme
     */
    public static Color getSelectionColor() {
        if (shouldUpdateCache()) {
            cachedSelectionColor = null;
        }
        
        if (cachedSelectionColor == null) {
            cachedSelectionColor = resolveSelectionColor();
        }
        return cachedSelectionColor;
    }
    
    /**
     * Gets a color suitable for disabled text in the current theme.
     * 
     * @return Color for disabled text
     */
    public static Color getDisabledTextColor() {
        Color color = UIManager.getColor("textInactiveText");
        if (color == null) {
            color = UIManager.getColor("Label.disabledForeground");
        }
        if (color == null) {
            // Create a dimmed version of the text color
            Color textColor = getTextColor();
            return new Color(textColor.getRed() / 2, textColor.getGreen() / 2, textColor.getBlue() / 2);
        }
        return color;
    }
    
    /**
     * Gets a color suitable for borders in the current theme.
     * 
     * @return Color for borders
     */
    public static Color getBorderColor() {
        Color color = UIManager.getColor("controlShadow");
        if (color == null) {
            color = UIManager.getColor("Separator.foreground");
        }
        if (color == null) {
            // Create a border color based on theme
            if (ThemeManager.isDarkTheme()) {
                return new Color(70, 70, 70);
            } else {
                return new Color(220, 220, 220);  // Light grey for light theme (original)
            }
        }
        return color;
    }
    
    /**
     * Gets annotation property color with proper theme awareness.
     * 
     * @return Color for annotation properties
     */
    public static Color getAnnotationPropertyColor() {
        Color color = UIManager.getColor("Annotation.propertyForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(190, 170, 255) : new Color(0, 80, 160);  // Dark blue for light theme
        }
        return color;
    }
    
    /**
     * Gets annotation label color with proper theme awareness.
     * 
     * @return Color for annotation labels
     */
    public static Color getAnnotationLabelColor() {
        Color color = UIManager.getColor("Annotation.labelForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(200, 200, 200) : new Color(80, 80, 80);  // Dark grey for light theme
        }
        return color;
    }
    
    /**
     * Gets annotation content color with proper theme awareness.
     * 
     * @return Color for annotation content
     */
    public static Color getAnnotationContentColor() {
        Color color = UIManager.getColor("Annotation.contentForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(230, 230, 230) : Color.BLACK;  // Black for light theme
        }
        return color;
    }
    
    /**
     * Gets link color with proper theme awareness.
     * 
     * @return Color for links
     */
    public static Color getLinkColor() {
        Color color = UIManager.getColor("Link.foreground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(190, 170, 255) : new Color(0, 100, 200);  // Standard blue for light theme
        }
        return color;
    }
    
    /**
     * Gets metrics title color with proper theme awareness.
     * 
     * @return Color for metrics titles
     */
    public static Color getMetricsTitleColor() {
        Color color = UIManager.getColor("Metrics.titleForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(215, 205, 240) : Color.BLACK;  // Black for light theme (original)
        }
        return color;
    }
    
    /**
     * Gets metrics value color with proper theme awareness.
     * 
     * @return Color for metrics values
     */
    public static Color getMetricsValueColor() {
        Color color = UIManager.getColor("Metrics.valueForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(235, 235, 235) : Color.BLACK;  // Black for light theme (original)
        }
        return color;
    }
    
    /**
     * Gets metrics grid color with proper theme awareness.
     * 
     * @return Color for metrics grid lines
     */
    public static Color getMetricsGridColor() {
        Color color = UIManager.getColor("Metrics.gridColor");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(70, 70, 70) : new Color(220, 220, 220);  // Light grey for light theme (original)
        }
        return color;
    }
    
    /**
     * Gets import value color with proper theme awareness.
     * 
     * @return Color for import values
     */
    public static Color getImportValueColor() {
        Color color = UIManager.getColor("Imports.valueForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(220, 220, 220) : Color.BLACK;  // Black for light theme
        }
        return color;
    }
    
    /**
     * Gets import IRI color with proper theme awareness.
     * 
     * @return Color for import IRIs
     */
    public static Color getImportIriColor() {
        Color color = UIManager.getColor("Imports.iriForeground");
        if (color == null) {
            return ThemeManager.isDarkTheme() ? 
                new Color(200, 180, 255) : Color.BLACK;  // Black for light theme (original style)
        }
        return color;
    }
    
    /**
     * Clears the color cache to force re-resolution from UIManager.
     * Should be called when themes change.
     */
    public static void clearCache() {
        cachedTextColor = null;
        cachedBackgroundColor = null;
        cachedSelectionColor = null;
        lastTheme = null;
    }
    
    /**
     * Determines if the color cache should be updated due to theme change.
     * 
     * @return true if cache should be updated
     */
    private static boolean shouldUpdateCache() {
        String currentTheme = ThemeManager.getCurrentTheme();
        if (lastTheme == null || !lastTheme.equals(currentTheme)) {
            lastTheme = currentTheme;
            return true;
        }
        return false;
    }
    
    /**
     * Resolves the text color from UIManager with fallback logic.
     * 
     * @return Resolved text color
     */
    private static Color resolveTextColor() {
        // Try multiple UIManager keys in order of preference
        Color color = UIManager.getColor("Label.foreground");
        if (color == null) {
            color = UIManager.getColor("textText");
        }
        if (color == null) {
            color = UIManager.getColor("controlText");
        }
        if (color == null) {
            color = UIManager.getColor("text");
        }
        if (color == null) {
            // Final fallback based on theme
            color = ThemeManager.isDarkTheme() ? Color.WHITE : Color.BLACK;
        }
        return color;
    }
    
    /**
     * Resolves the background color from UIManager with fallback logic.
     * 
     * @return Resolved background color
     */
    private static Color resolveBackgroundColor() {
        // Try multiple UIManager keys in order of preference
        Color color = UIManager.getColor("Panel.background");
        if (color == null) {
            color = UIManager.getColor("control");
        }
        if (color == null) {
            color = UIManager.getColor("window");
        }
        if (color == null) {
            color = UIManager.getColor("background");
        }
        if (color == null) {
            // Final fallback based on theme
            color = ThemeManager.isDarkTheme() ? new Color(45, 45, 45) : Color.WHITE;
        }
        return color;
    }
    
    /**
     * Resolves the selection color from UIManager with fallback logic.
     * 
     * @return Resolved selection color
     */
    private static Color resolveSelectionColor() {
        // Try multiple UIManager keys in order of preference
        Color color = UIManager.getColor("List.selectionBackground");
        if (color == null) {
            color = UIManager.getColor("textHighlight");
        }
        if (color == null) {
            color = UIManager.getColor("focus");
        }
        if (color == null) {
            // Final fallback based on theme
            color = ThemeManager.isDarkTheme() ? new Color(75, 110, 175) : new Color(184, 207, 229);
        }
        return color;
    }
}