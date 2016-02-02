package org.protege.editor.owl.ui;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLIcons {

    private static HashMap<String, Icon> iconMap;

    public static final String ALTERNATIVE_ICONS_DIRECTORY = "icons/";


    static {
        iconMap = new HashMap<>();
    }

    /**
     * Gets the Icon with the specified file name.
     * @param fileName The file name.  May be {@code null}.
     * @return The Icon that is depicted by the specified file name, or {@code null} if there is no file in the class
     * path with the specified file name, of {@code fileName} is {@code null}.
     */
    public static Icon getIcon(String fileName) {
        if(fileName == null) {
            return null;
        }
        Icon icon = iconMap.get(fileName);
        if (icon != null) {
            return icon;
        }
        URL url = getIconURL(fileName);
        if (url == null) {
            return null;
        }
        Icon loadedIcon = new ImageIcon(url);
        iconMap.put(fileName, loadedIcon);
        return loadedIcon;
    }

    /**
     * Gets the URL of the icon file denoted by the specified file name.
     * @param fileName The file name.  May be {@code null}.
     * @return The URL of the specified file name, or {@code null} if {@code fileName} is {@code null}, or {@code null}
     *         if there is not file with the specified file name in the bundle class path.
     */
    private static URL getIconURL(String fileName) {
        if (fileName == null) {
            return null;
        }
        ClassLoader loader = OWLIcons.class.getClassLoader();
        URL url = loader.getResource(fileName);
        if (url == null && !isFileNameAbsolute(fileName)) {
            url = loader.getResource(ALTERNATIVE_ICONS_DIRECTORY + fileName);
        }
        return url;
    }

    /**
     * Determines if the specified file name is absolute.
     * @param fileName The file name.
     * @return {@code true} if the specified file name is absolute, otherwise {@code false}.
     */
    private static boolean isFileNameAbsolute(String fileName) {
        return fileName.startsWith("/");
    }
}
