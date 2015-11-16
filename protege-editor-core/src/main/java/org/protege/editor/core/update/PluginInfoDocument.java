package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoDocument {
	
    public static final String ID_PROPERTY_NAME = "id";
    private static final String LABEL_PROPERTY_NAME = "name";
    public static final String VERSION_PROPERTY_NAME = "version";
    public static final String DOWNLOAD_PROPERTY_NAME = "download";
    public static final String DOC_PROPERTY_NAME = "readme";
    private static final String LICENSE_PROPERTY_NAME = "license";
    private static final String AUTHOR_PROPERTY_NAME = "author";

    private PluginInfo info;

    private URL updateFileLocation;


    public PluginInfoDocument(URL updateFileLocation) throws IOException {
        this.updateFileLocation = updateFileLocation;
        Properties properties = new Properties();
        properties.load(new BufferedInputStream(updateFileLocation.openStream()));
        String id = properties.getProperty(ID_PROPERTY_NAME);

        String versionString = properties.getProperty(VERSION_PROPERTY_NAME);
        Version version = null;
        if (versionString != null){
            try {
				version = new Version(versionString);
			} catch (java.lang.IllegalArgumentException e) {
				System.out.println("Check for updates found invalid version number for "
								+ properties.getProperty(LABEL_PROPERTY_NAME)
								+ ": " + e.getMessage());
			}
        }
        final String downloadURLStr = properties.getProperty(DOWNLOAD_PROPERTY_NAME);
        URL downloadURL = null;
        if (downloadURLStr != null){
            downloadURL = new URL(downloadURLStr);
        }

        info = new PluginInfo(id, version, downloadURL);

        final String readmeStr = properties.getProperty(DOC_PROPERTY_NAME);
        if (readmeStr != null){
            info.setReadmeURI(new URL(readmeStr));
        }

        info.setLicense(properties.getProperty(LICENSE_PROPERTY_NAME));
        info.setAuthor(properties.getProperty(AUTHOR_PROPERTY_NAME));
        info.setLabel(properties.getProperty(LABEL_PROPERTY_NAME));
    }


    public boolean isValid(Bundle bundle) throws UpdateException {

        if (info.getId() == null){
            throw new UpdateException(info.getId(), updateFileLocation, "No plugin ID given");
        }
        if (bundle != null){
            final String pluginID = bundle.getSymbolicName();
            if (!info.getId().equals(pluginID)){
                throw new UpdateException(info.getId(), updateFileLocation, "ID does not match the plugin (" + pluginID + ")");
            }
        }
        if (info.getAvailableVersion() == null){
            throw new UpdateException(info.getId(), updateFileLocation, "Cannot find version");
        }
        if (info.getDownloadURL() == null){
            throw new UpdateException(info.getId(), updateFileLocation, "No download location given");
        }
        return true;
    }


    public PluginInfo getPluginInfo() {
        return info;
    }
}
