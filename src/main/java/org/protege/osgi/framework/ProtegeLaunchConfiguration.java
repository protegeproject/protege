package org.protege.osgi.framework;

public class ProtegeLaunchConfiguration implements LaunchConfiguration {
	
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
        "org.protege.jaxb.jar",
        "org.protege.editor.core.application.jar"
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
		return new String[0];
	}

}
