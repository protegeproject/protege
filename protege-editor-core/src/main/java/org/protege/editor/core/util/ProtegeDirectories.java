package org.protege.editor.core.util;

import java.io.File;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/10/15
 */
public class ProtegeDirectories {

    public static final String DATA_DIR_PROP = "protege.data.dir";

    private static File dataDirectory = null;

    private static File userPluginDirectory = null;

    public static File getDataDirectory() {
        if (dataDirectory == null) {
            dataDirectory  = new  File(System.getProperty(DATA_DIR_PROP, System.getProperty("user.home") + File.separator + ".Protege"));
            dataDirectory.mkdir();
        }
        return dataDirectory;
    }

    public static File getUserPluginDirectory() {
        if (userPluginDirectory == null) {
            userPluginDirectory = new File(getDataDirectory(), "plugins");
            userPluginDirectory.mkdir();
        }
        return userPluginDirectory;
    }
}
