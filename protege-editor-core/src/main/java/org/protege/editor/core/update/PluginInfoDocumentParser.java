package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoDocumentParser {

    public static final String ID_PROPERTY_NAME = "id";

    private static final String LABEL_PROPERTY_NAME = "name";

    public static final String VERSION_PROPERTY_NAME = "version";

    public static final String DOWNLOAD_PROPERTY_NAME = "download";

    public static final String DOC_PROPERTY_NAME = "readme";

    private static final String LICENSE_PROPERTY_NAME = "license";

    private static final String AUTHOR_PROPERTY_NAME = "author";

    private final URL updateFileLocation;


    public PluginInfoDocumentParser(URL updateFileLocation) {
        this.updateFileLocation = checkNotNull(updateFileLocation);
    }

    public PluginInfo parseDocument(Optional<Bundle> bundle) throws PluginDocumentParseException {
        try {
            Properties properties = new Properties();
            BufferedInputStream inputStream = new BufferedInputStream(updateFileLocation.openStream());
            properties.load(inputStream);
            inputStream.close();

            String id = properties.getProperty(ID_PROPERTY_NAME);
            if(id == null) {
                throw new PluginDocumentParseException(
                        String.format("The plugin update document at %s is not valid.  Reason: No plugin Id given.", updateFileLocation)
                );
            }

            if(bundle.isPresent() && !id.equals(bundle.get().getSymbolicName())) {
                throw new PluginDocumentParseException(
                        String.format("The plugin update document at %s has a plugin id (%s) that does not match the expected id (%s)",
                                updateFileLocation,
                                id,
                                bundle.get().getSymbolicName())
                );
            }

            final String versionString = properties.getProperty(VERSION_PROPERTY_NAME);
            if(versionString == null) {
                throw new PluginDocumentParseException(
                        String.format(
                                "The plugin update document at %s is missing a version string.",
                                updateFileLocation)
                );
            }
            final Version version;
            try {
                   version = new Version(versionString);
            } catch (IllegalArgumentException e) {
                    throw new PluginDocumentParseException(
                            String.format(
                                    "The plugin update document at %s contains an illegal version string (%s).",
                                    updateFileLocation,
                                    versionString),
                            e
                    );
            }

            final String downloadURLStr = properties.getProperty(DOWNLOAD_PROPERTY_NAME);
            if(downloadURLStr == null) {
                throw new PluginDocumentParseException(
                        String.format(
                                "The plugin update document at %s does not contain a download URL",
                                updateFileLocation
                        )
                );
            }

            URL downloadURL = new URL(downloadURLStr);

            PluginInfo info = new PluginInfo(id, version, downloadURL);

            final String readmeStr = properties.getProperty(DOC_PROPERTY_NAME);
            if (readmeStr != null){
                info.setReadmeURI(new URL(readmeStr));
            }
            info.setLicense(properties.getProperty(LICENSE_PROPERTY_NAME));
            info.setAuthor(properties.getProperty(AUTHOR_PROPERTY_NAME));
            info.setLabel(properties.getProperty(LABEL_PROPERTY_NAME));

            return info;
        } catch (IOException e) {
            throw new PluginDocumentParseException("The plugin document could not be loaded due to a network error: " + e.getClass().getSimpleName() + ", " + e.getMessage(), e);
        }
    }


//    public boolean isValid(Bundle bundle) throws UpdateException {
//        // MH:  This is a really, really odd method and implementation.  It only returns if valid, otherwise
//        // it throws and exception.
//        if (info.getId() == null){
//            logger.warn();
//            throw new UpdateException(info.getId(), updateFileLocation, "No plugin ID given");
//        }
//        if (bundle != null){
//            final String pluginID = bundle.getSymbolicName();
//            if (!info.getId().equals(pluginID)){
//                throw new UpdateException(info.getId(), updateFileLocation, "ID does not match the plugin (" + pluginID + ")");
//            }
//        }
//        if (info.getAvailableVersion() == null){
//            throw new UpdateException(info.getId(), updateFileLocation, "Cannot find version");
//        }
//        if (info.getDownloadURL() == null){
//            throw new UpdateException(info.getId(), updateFileLocation, "No download location given");
//        }
//        return true;
//    }


//    public PluginInfo getPluginInfo() {
//        return info;
//    }
}
