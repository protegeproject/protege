package org.protege.editor.core.ui.util;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utility class for managing theme switching in Protégé.
 * Handles loading and applying light/dark themes based on user preferences.
 * Supports both startup theme loading and runtime theme switching.
 * 
 * @author Protégé Theme Manager Implementation
 */
public class ThemeManager {
    
    // Theme preference constants
    public static final String THEME_KEY = "THEME_KEY";
    public static final String LIGHT_THEME = "LIGHT_THEME";
    public static final String DARK_THEME = "DARK_THEME";
    
    // Theme change listeners
    private static final CopyOnWriteArrayList<ThemeChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    /**
     * Interface for components that need to be notified of theme changes.
     */
    public interface ThemeChangeListener {
        /**
         * Called when the theme changes.
         * 
         * @param newTheme the new theme identifier (LIGHT_THEME or DARK_THEME)
         * @param oldTheme the previous theme identifier
         */
        void onThemeChanged(String newTheme, String oldTheme);
    }
    
    /**
     * Adds a theme change listener.
     * 
     * @param listener the listener to add
     */
    public static void addThemeChangeListener(ThemeChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a theme change listener.
     * 
     * @param listener the listener to remove
     */
    public static void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Loads and applies the theme based on user preferences.
     * Should be called during application startup.
     */
    public static void loadTheme() {
        try {
            Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
            String selectedTheme = appPrefs.getString(THEME_KEY, LIGHT_THEME);

            if (DARK_THEME.equals(selectedTheme)) {
                applyDarkTheme();
            } else {
                applyLightTheme();
            }
        } catch (Exception e) {
            // Fall back to light theme if there's any error
            try {
                applyLightTheme();
            } catch (Exception fallbackException) {
                // If even the fallback fails, log and continue with default LAF
                System.err.println("Failed to apply theme: " + fallbackException.getMessage());
            }
        }
    }
    
    /**
     * Switches to the specified theme and updates all components.
     * This method supports runtime theme switching without requiring a restart.
     * 
     * @param newTheme the theme to switch to (LIGHT_THEME or DARK_THEME)
     * @return true if the theme was successfully applied, false otherwise
     */
    public static boolean switchToTheme(String newTheme) {
        String oldTheme = getCurrentTheme();
        
        // Don't switch if it's already the current theme
        if (newTheme.equals(oldTheme)) {
            return true;
        }
        
        try {
            // Apply the new theme
            if (DARK_THEME.equals(newTheme)) {
                applyDarkTheme();
            } else {
                applyLightTheme();
            }
            
            // Save the preference
            Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
            appPrefs.putString(THEME_KEY, newTheme);
            
            // Clear color cache
            ProtegeColorProvider.clearCache();
            
            // Update all components
            refreshAllComponents();
            
            // Notify listeners
            notifyThemeChangeListeners(newTheme, oldTheme);
            
            return true;
        } catch (Exception e) {
            System.err.println("Failed to switch theme: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Applies the dark theme to the application.
     * @throws Exception if theme application fails
     */
    public static void applyDarkTheme() throws Exception {
        PlasticLookAndFeel.setCurrentTheme(new ProtegeDarkTheme());
        UIManager.put("ClassLoader", PlasticLookAndFeel.class.getClassLoader());
        
        // Create and set the Plastic Look and Feel with dark theme
        PlasticLookAndFeel plasticLAF = new PlasticLookAndFeel();
        UIManager.setLookAndFeel(plasticLAF);
    }
    
    /**
     * Applies the light theme to the application.
     * @throws Exception if theme application fails
     */
    public static void applyLightTheme() throws Exception {
        PlasticLookAndFeel.setCurrentTheme(new ProtegePlasticTheme());
        UIManager.put("ClassLoader", PlasticLookAndFeel.class.getClassLoader());
        
        // Create and set the Plastic Look and Feel with light theme
        PlasticLookAndFeel plasticLAF = new PlasticLookAndFeel();
        UIManager.setLookAndFeel(plasticLAF);
    }
    
    /**
     * Gets the currently selected theme from preferences.
     * @return the theme identifier (LIGHT_THEME or DARK_THEME)
     */
    public static String getCurrentTheme() {
        Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        return appPrefs.getString(THEME_KEY, LIGHT_THEME);
    }
    
    /**
     * Checks if dark theme is currently selected.
     * @return true if dark theme is selected, false otherwise
     */
    public static boolean isDarkTheme() {
        return DARK_THEME.equals(getCurrentTheme());
    }
    
    /**
     * Checks if light theme is currently selected.
     * @return true if light theme is selected, false otherwise
     */
    public static boolean isLightTheme() {
        return LIGHT_THEME.equals(getCurrentTheme());
    }
    
    /**
     * Refreshes all Swing components to reflect the new theme.
     * This method updates the UI of all visible windows and their components.
     */
    private static void refreshAllComponents() {
        // Update all windows
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            if (window.isDisplayable()) {
                window.repaint();
            }
        }
        
        // Force a garbage collection to help with any cached color references
        System.gc();
    }
    
    /**
     * Notifies all registered theme change listeners.
     * 
     * @param newTheme the new theme identifier
     * @param oldTheme the previous theme identifier
     */
    private static void notifyThemeChangeListeners(String newTheme, String oldTheme) {
        for (ThemeChangeListener listener : listeners) {
            try {
                listener.onThemeChanged(newTheme, oldTheme);
            } catch (Exception e) {
                System.err.println("Error notifying theme change listener: " + e.getMessage());
            }
        }
    }
}