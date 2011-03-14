package org.protege.osgi.framework;

public class ServerLaunchConfiguration implements LaunchConfiguration {
	
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
    	"org.apache.felix.configadmin-1.2.4.jar",
    	"org.apache.felix.http.api-2.0.4.jar",
    	"org.apache.felix.http.bundle-2.0.4.jar",
    	"org.apache.felix.http.jetty-2.0.4.jar",
    	"org.apache.felix.log-1.0.0.jar",
    	"org.apache.felix.scr-1.4.0.jar",
    	"org.protege.common.jar",
    	"protege-owlapi-extensions.jar",
    	"org.protege.owl.server.db.jar",
    	"org.protege.owl.server.servlet.jar",
    	"org.protege.owl.server.junit.jar"
    };
    
    private static final String[] pluginBundles = {
    	"org.semanticweb.owl.owlapi.jar",
    	"org.protege.osgi.jdbc.jar",
    	"org.protege.owl.database.jar",
    	"org.protege.owl.server.jar"
    };

	@Override
	public String[][] getSystemProperties() {
		return systemProperties;
	}

	@Override
	public String[][] getFrameworkProperties() {
		return frameworkProperties;
	}

	@Override
	public String[] getCoreBundles() {
		return coreBundles;
	}
	
	@Override
	public String[] getPluginBundles() {
		return pluginBundles;
	}

}
