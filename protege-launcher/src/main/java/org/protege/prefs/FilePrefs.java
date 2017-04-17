package org.protege.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePrefs extends AbstractPreferences {
	private static final Logger log = LoggerFactory.getLogger(FilePrefs.class);

	private Map<String, String> root;
	private Map<String, FilePrefs> children;
	private List<String> children_names;
	private boolean isRemoved = false;

	public FilePrefs(AbstractPreferences parent, String name) {
		super(parent, name);

		log.info("Instantiating node " + name);

		root = new TreeMap<String, String>();
		children = new TreeMap<String, FilePrefs>();
		children_names = new ArrayList<String>();

		try {
			sync();
		}
		catch (BackingStoreException e) {
			log.error("Unable to sync on creation of node " + name, e);
		}
	}

	synchronized protected void putSpi(String key, String value) {
		root.put(key, value);
		try {
			flush();
		}
		catch (BackingStoreException e) {
			log.error("Unable to flush after putting " + key, e);
		}
	}

	synchronized protected String getSpi(String key) {
		return root.get(key);
	}

	synchronized protected void removeSpi(String key) {
		root.remove(key);
		try {
			flush();
		}
		catch (BackingStoreException e) {
			log.error("Unable to flush after removing " + key, e);
		}
	}

	synchronized protected void removeNodeSpi() throws BackingStoreException {
		isRemoved = true;
		flush();
	}

	synchronized protected String[] keysSpi() throws BackingStoreException {
		return root.keySet().toArray(new String[root.keySet().size()]);
	}

	synchronized protected String[] childrenNamesSpi() throws BackingStoreException {
		return children_names.toArray(new String[children_names.size()]);
	}

	synchronized protected FilePrefs childSpi(String name) {
		FilePrefs child = children.get(name);
		if (child == null || child.isRemoved()) {
			child = new FilePrefs(this, name);
			children.put(name, child);
		}
		return child;
	}


	protected synchronized void syncSpi() throws BackingStoreException {

		if (isRemoved()) return;

		final File file = FileBackingStorePrefsFactory.getPreferencesFile();

		if (!file.exists()) return;

		Properties p = new Properties();
		try {
			p.load(new FileInputStream(file));

			StringBuilder sb = new StringBuilder();
			getPath(sb);
			String path = sb.toString();
			
			List<String> propKeys = getPropNamesWithPrefix(p, path);
			
			for(String propKey : propKeys) {
				String subKey = propKey.substring(path.length());
				// Only load immediate descendents
				if (subKey.indexOf('/') == -1) {
					root.put(subKey, p.getProperty(propKey));
				} else {
					children_names.add(subKey.substring(0,subKey.indexOf('/')));
				}				
			}
		}
		catch (IOException e) {
			throw new BackingStoreException(e);
		}
	}


	private void getPath(StringBuilder sb) {
		final FilePrefs parent = (FilePrefs) parent();
		if (parent == null) return;

		parent.getPath(sb);
		sb.append(name()).append('/');
	}

	protected synchronized void flushSpi() throws BackingStoreException {

		final File file = FileBackingStorePrefsFactory.getPreferencesFile();

		try {

			StringBuilder sb = new StringBuilder();
			getPath(sb);
			String path = sb.toString();
			Properties p = new Properties();
			if (file.exists()) {

				p.load(new FileInputStream(file));

				List<String> toRemove = new ArrayList<String>();
				
				List<String> propKeys = getPropNamesWithPrefix(p, path);
				
				for(String propKey : propKeys) {
					String subKey = propKey.substring(path.length());
					// Only do immediate descendants
					if (subKey.indexOf('/') == -1) {
						toRemove.add(propKey);
					}					
				}

				// Remove them now that the enumeration is done with
				for (String propKey : toRemove) {
					p.remove(propKey);
				}


				// If this node hasn't been removed, add back in any values
				if (!isRemoved) {
					for (String s : root.keySet()) {
						//System.out.println("Adding  " + s);
						p.setProperty(path + s, root.get(s));
					}
				}

			}
			if (p.size() > 0) {
				p.store(new FileOutputStream(file), "FilePreferences");
			} else {
				log.warn("what happened");
				p.store(new FileOutputStream(file), "FilePreferences");
				log.warn("wrote empty file");
			}
		}
		catch (IOException e) {
			throw new BackingStoreException(e);
		}
	}
	
	private static List<String> getPropNamesWithPrefix(Properties p, String path) {
		
		List<String> names = new ArrayList<String>();
		
		// Make a list of all direct children of this node to be removed
		final Enumeration<?> pnen = p.propertyNames();
		while (pnen.hasMoreElements()) {
			String propKey = (String) pnen.nextElement();
			if (propKey.startsWith(path)) {
				names.add(propKey);
			}
		}
		return names;		
	}
}
