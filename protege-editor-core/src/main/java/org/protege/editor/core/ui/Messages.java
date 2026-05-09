package org.protege.editor.core.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "org.protege.editor.core.ui.messages";
    
    private static ResourceBundle resourceBundle;
    
    static {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (MissingResourceException e) {
            resourceBundle = null;
        }
    }
    
    private Messages() {
    }
    
    public static String getString(String key) {
        if (resourceBundle == null) {
            return key;
        }
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
    
    public static String getString(String key, String defaultValue) {
        if (resourceBundle == null) {
            return defaultValue;
        }
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }
}
