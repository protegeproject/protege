package org.protege.common;

import java.io.File;

public class CommonProtegeProperties {
    
    public static final String APPLICATION_DIR_PROP = "protege.dir";
    public static final String DATA_DIR_PROP = "protege.data.dir";

    public static final String CURRENT_WORKING_DIRECTORY_PROP = "user.dir";
    
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
