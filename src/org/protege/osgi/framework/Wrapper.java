package org.protege.osgi.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;


public class Wrapper {
    public static final String ARG_PROPERTY = "command.line.arg.";
    
    private static final String[][] systemProperties = {
        {"file.encoding", "utf-8"},
        {"apple.laf.useScreenMenuBar", "true"},
        {"com.apple.mrj.application.growbox.intrudes", "true"}
    };

    private static final String[][] frameworkProperties = { 
        {"org.osgi.framework.bootdelegation", "sun.*,com.sun.*,apple.*,com.apple.*"},
        {"org.osgi.framework.system.packages.extra", "javax.xml.parsers,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers"},
        {"org.osgi.framework.storage.clean", "onFirstInit"}
    };

    private static final String[] coreBundles = {
        "org.protege.common.jar",
        "org.eclipse.equinox.common.jar",
        "org.eclipse.equinox.registry.jar",
        "org.eclipse.equinox.supplement.jar",
        "org.protege.editor.core.application.jar"
    };
    
    private Properties launchProperties = new Properties();
    private String     factoryClass;
    private String     bundleDir;
    
    public Wrapper() throws IOException {
		Properties buildProperties = new Properties();
		FileInputStream fis = new FileInputStream("build.properties");
		buildProperties.load(fis);
		fis.close();
		
		bundleDir    = buildProperties.getProperty("protege.common");
		if (bundleDir == null) {
			bundleDir = "bundles";
		}
		String pluginDir = buildProperties.getProperty("protege.plugins");
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
		
		for (int i = 0; i < frameworkProperties.length; i++) {
			launchProperties.setProperty(frameworkProperties[i][0], frameworkProperties[i][1]);
		}
		
		File frameworkDir = new File(System.getProperty("java.io.tmpdir"), "ProtegeCache-" + UUID.randomUUID().toString());
		launchProperties.setProperty("org.osgi.framework.storage", frameworkDir.getCanonicalPath());
		frameworkDir.deleteOnExit();
    }
    
    public void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, IOException {
    	FrameworkFactory factory = (FrameworkFactory) Class.forName(factoryClass).newInstance();
    	Framework framework = factory.newFramework(launchProperties);
    	framework.start();
    	BundleContext context = framework.getBundleContext();
    	List<Bundle> core = new ArrayList<Bundle>();
    	for (String bundleName :  coreBundles) {
    		core.add(context.installBundle(new File(bundleDir, bundleName).toURI().toString()));
    	}
    	for (Bundle b : core) {
    		b.start();
    	}
    }
    

	/**
	 * @param args
	 */
    public static void main(String[] args) {
    	try {
    		setSystemProperties(args);
    		new Wrapper().start();
    	}
    	catch (Throwable t) {
    		System.out.println("Fatal Exception Caught trying to start Protege");
    		t.printStackTrace();
    	}
    }


    private static void setSystemProperties(String[] args) {
		if (args != null) {
			int counter = 0;
			for (String arg : args) {
				System.setProperty(ARG_PROPERTY + (counter++), arg);
			}
		}
		for (int i = 0; i < systemProperties.length; i++) {
			System.setProperty(systemProperties[i][0], systemProperties[i][1]);
		}
    }
	
}
