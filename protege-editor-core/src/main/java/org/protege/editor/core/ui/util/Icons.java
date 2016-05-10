package org.protege.editor.core.ui.util;


import org.osgi.framework.Bundle;
import org.protege.editor.core.plugin.PluginUtilities;

import javax.swing.*;
import java.net.URL;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 23, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class Icons {


    public static final String ALTERNATIVE_ICONS_PATH = "icons/";

    /**
     * Gets an Icon by its file name.  e.g. add.png would return the icon depicted in the add.png file on the
     * class path.
     * @param name The name of the icon file in the class path.  May be {@code null}.
     * @return The Icon for the specified file name, or {@code null} if the file does not exist in the class path, or
     * {@code null} if the specified file name is {@code null}.
     */
    public static Icon getIcon(String name) {
        if(name == null) {
            return null;
        }
        try {
            URL url = getIconURLFromIconFileName(name);
            if(url == null) {
                return null;
            }
            return new ImageIcon(url);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the URL of the specified (icon) file fileName.
     * @param fileName The file name. May be {@code null}.
     * @return The URL of the specified file name in the class path.  May be {@code null}.
     */
    private static URL getIconURLFromIconFileName(String fileName) {
        if(fileName == null) {
            return null;
        }
        Bundle b = PluginUtilities.getInstance().getApplicationBundle();
        URL url = b.getResource(fileName);
        if(url != null) {
            return url;
        }
        if(!isNameAbsolute(fileName)) {
            return b.getResource(ALTERNATIVE_ICONS_PATH + fileName);
        }
        return null;
    }

    /**
     * Determines whether or not the specified name is absolute.
     * @param name The name to be tested
     * @return {@code true} if {@code name} is absolute, otherwise {@code false}.
     */
    private static boolean isNameAbsolute(String name) {
        return name.startsWith("/");
    }
}
