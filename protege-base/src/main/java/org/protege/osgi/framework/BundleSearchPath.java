package org.protege.osgi.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class BundleSearchPath {
	private Logger logger = Logger.getLogger(BundleSearchPath.class.getCanonicalName());
	
	public static final String USER_HOME     = "user.home";
	public static final String USER_HOME_VAR = "${" + USER_HOME + "}/";

	
	private List<File> path = new ArrayList<File>();
	private List<String> allowedBundles = new ArrayList<String>();
	
	public BundleSearchPath() {
	}
	
	public void addSearchPath(String dir) {
		if (dir.startsWith(USER_HOME_VAR)) {
			String homeDirectory = System.getProperty(USER_HOME);
			dir = dir.substring(USER_HOME_VAR.length());
			path.add(new File(homeDirectory, dir));
		}
		else {
			path.add(new File(dir));
		}
	}


	public List<File> getPath() {
		return path;
	}
	
	public List<String> getAllowedBundles() {
		return allowedBundles;
	}
	
	public void addAllowedBundle(String bundle) {
		allowedBundles.add(bundle);
	}
	
	public Collection<File> search() {
		Map<String, File> nameToFileMap = new LinkedHashMap<String, File>();
		for (File dir : path) {
			if (!dir.exists() || !dir.isDirectory()) {
				continue;
			}
			for (File jar : dir.listFiles()) {
				String jarName = jar.getName();
				if (!jar.getName().endsWith(".jar")) {
					continue;
				}
				if (!allowedBundles.isEmpty() && !allowedBundles.contains(jarName)) {
					continue;
				}
				try {
					addJar(jarName, jar, nameToFileMap);
				}
				catch (Exception e) {
					logger.log(Level.WARNING, "Exception caught loading bundle file: ", jar);
				}
			}
		}
		return nameToFileMap.values();
	}
	
	private void addJar(String jarName, File jar, Map<String, File> nameToFileMap) throws FileNotFoundException, IOException {
		File otherJar = nameToFileMap.get(jarName);
		if (otherJar == null) {
			nameToFileMap.put(jarName, jar);
		}
		else {
			Version jarVersion = obtainVersion(jar);
			Version otherVersion = obtainVersion(otherJar);
			if (jarVersion != null && (otherVersion == null || jarVersion.compareTo(otherVersion) > 0)) {
				nameToFileMap.put(jarName, jar);
			}
		}
	}
	
	private static Version obtainVersion(File f) throws FileNotFoundException, IOException {
		JarInputStream is = new JarInputStream(new FileInputStream(f));
		try {
			Manifest mf = is.getManifest();
			String versionString = mf.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
			return new Version(versionString);
		}
		finally {
			is.close();
		}
	}

}
