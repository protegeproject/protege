package org.protege.editor.core.update;



import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.Version;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UpdateDocument {

    public static final String ROOT_NAME = "plugin";

    public static final String VERSION_PROPERTY_NAME = "version";

    public static final String DOWNLOAD_PROPERTY_NAME = "download";

    public static final String README_PROPERTY_NAME = "readme";

    private Version version;

    private URI downloadURI;

    private URI readmeURI;


    public UpdateDocument(URL updateFile) {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedInputStream(updateFile.openStream()));
            String versionString = properties.getProperty(VERSION_PROPERTY_NAME);
            version = new Version(versionString);
            downloadURI = URI.create((String) properties.get(DOWNLOAD_PROPERTY_NAME));
            readmeURI = URI.create((String) properties.get(README_PROPERTY_NAME));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Version getVersion() {
        return version;
    }


    public URI getDownloadLocation() {
        return downloadURI;
    }


    public URI getReadmeLocation() {
        return readmeURI;
    }


    public boolean isValid() {
        return version != null && downloadURI != null;
    }
}
