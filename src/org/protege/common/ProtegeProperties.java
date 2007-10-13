package org.protege.common;

import java.io.File;

public class ProtegeProperties {
    
    public static final String APPLICATION_DIR_PROP = "protege.dir";

    public static final String CURRENT_WORKING_DIRECTORY_PROP = "user.dir";
    
    private static File applicationDirectory;
    
    public static File getApplicationDirectory() {
        if (applicationDirectory == null) {
            String applicationDir = System.getProperty(APPLICATION_DIR_PROP);
            if (applicationDir == null) {
                applicationDir = System.getProperty(CURRENT_WORKING_DIRECTORY_PROP);
            }
            applicationDirectory = new File(applicationDir);
        }
        return applicationDirectory;
    }

}
