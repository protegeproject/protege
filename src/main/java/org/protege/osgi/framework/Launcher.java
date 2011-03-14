package org.protege.osgi.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;


public class Launcher {
	public static final String PROTEGE_PROPERTIES_PROPERTY = "protege.properties";
	
    public static final String ARG_PROPERTY = "command.line.arg.";
    

    private LaunchConfiguration launchConfiguration;
    private Properties launchProperties = new Properties();
    private String     factoryClass;
    private String     bundleDir;
    private String     pluginDir;

    
    public Launcher(LaunchConfiguration launchConfiguration) throws IOException {
    	this.launchConfiguration = launchConfiguration;
    	setSystemProperties();
		Properties buildProperties = loadProtegeProperties();
		
		bundleDir    = buildProperties.getProperty("protege.common");
		if (bundleDir == null) {
			bundleDir = "bundles";
		}
		pluginDir = buildProperties.getProperty("protege.plugins");
		if (pluginDir == null) {
			pluginDir = "plugins";
		}
		System.setProperty("org.protege.plugin.dir", pluginDir);
			
			
		BufferedReader factoryReader = new BufferedReader(
				new InputStreamReader(
						getClass().getClassLoader().getResourceAsStream("META-INF/services/org.osgi.framework.launch.FrameworkFactory")));
		factoryClass = factoryReader.readLine();
		factoryClass = factoryClass.trim();
		factoryReader.close();
		
		String[][] frameworkProperties = launchConfiguration.getFrameworkProperties();
		for (int i = 0; i < frameworkProperties.length; i++) {
			launchProperties.setProperty(frameworkProperties[i][0], frameworkProperties[i][1]);
		}
		
		File frameworkDir = new File(System.getProperty("java.io.tmpdir"), "ProtegeCache-" + UUID.randomUUID().toString());
		launchProperties.setProperty("org.osgi.framework.storage", frameworkDir.getCanonicalPath());
		frameworkDir.deleteOnExit();
    }
    
    private void setSystemProperties() {
		String[][] systemProperties = launchConfiguration.getSystemProperties();
		for (int i = 0; i < systemProperties.length; i++) {
			System.setProperty(systemProperties[i][0], systemProperties[i][1]);
		}
	}

	public Properties loadProtegeProperties() throws IOException {
    	Properties buildProperties = new Properties();
 		File propertiesFile = null;
 		String specifiedPropertiesLocation = System.getProperty(PROTEGE_PROPERTIES_PROPERTY);
 		if (specifiedPropertiesLocation != null) {
 			propertiesFile = new File(specifiedPropertiesLocation);
 		}
 		else {
 			propertiesFile = new File("build.properties");
 		}
 		String protegeHome = propertiesFile.getAbsoluteFile().getParent();
 		FileInputStream fis = new FileInputStream("build.properties");
 		buildProperties.load(fis);
 		fis.close();
 		for (Entry<Object, Object> e : new HashSet<Entry<Object, Object>>(buildProperties.entrySet())) {
 			String key = (String) e.getKey();
 			String value = (String) e.getValue();
 			buildProperties.put(key, value.replace("${protege.home}", protegeHome));
 		}
 		return buildProperties;
    }
    
    public void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, IOException {
    	FrameworkFactory factory = (FrameworkFactory) Class.forName(factoryClass).newInstance();
    	Framework framework = factory.newFramework(launchProperties);
    	framework.start();
    	BundleContext context = framework.getBundleContext();
    	List<Bundle> bundles = installBundles(context, bundleDir, launchConfiguration.getCoreBundles());
    	bundles.addAll(installBundles(context, pluginDir, launchConfiguration.getPluginBundles()));
    	startBundles(context, bundles);
    	startBundles(context, bundles);
    }

    private List<Bundle> installBundles(BundleContext context, String dir, String[] bundles) throws BundleException {
    	List<Bundle> core = new ArrayList<Bundle>();
    	for (String bundleName :  bundles) {
    		boolean success = false;
    		try {
    			core.add(context.installBundle(new File(dir, bundleName).toURI().toString()));
    			success = true;
    		}
    		finally {
    			if (!success) {
    				System.out.println("Core Bundle " + bundleName + " failed to install.");
    			}
    		}
    	}
    	return core;
    }
    
    private void startBundles(BundleContext context, List<Bundle> bundles) throws BundleException {
    	for (Bundle b : bundles) {
    		boolean success = false;
    		try {
    			b.start();
    			success = true;
    		}
    		finally {
    			if (!success) {
    				System.out.println("Core Bundle " + b.getBundleId() + " failed to start.");
    			}
    		}
    	}
    }
    

	/**
	 * @param args
	 */
    public static void main(String[] args) {
    	try {
    		setArguments(args);
    		new Launcher(new ProtegeLaunchConfiguration()).start();
    	}
    	catch (Throwable t) {
    		System.out.println("Fatal Exception Caught trying to start Protege");
    		t.printStackTrace();
    	}
    }
    
    public static void setArguments(String[] args) {
		if (args != null) {
			int counter = 0;
			for (String arg : args) {
				System.setProperty(ARG_PROPERTY + (counter++), arg);
			}
		}    	
    }
	
}
