package org.protege.osgi.framework;


//TODO - switch to an xml based configuration.
public interface LaunchConfiguration {
	
	String[][] getSystemProperties();
	String[][] getFrameworkProperties();
	String[]   getCoreBundles();
	String[]   getPluginBundles();

}
