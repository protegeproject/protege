package org.protege.editor.core.update;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;



import java.net.URI;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UpdateChecker {

    private static final Logger logger = Logger.getLogger(UpdateChecker.class);

    private URI uri;

    private Bundle b;


    public UpdateChecker(URI updateFileURL, Bundle pluginDescriptor) {
        this.uri = updateFileURL;
        this.b = pluginDescriptor;
    }


    public UpdateInfo run() {
        try {
            Version pluginVersion = PluginUtilities.getBundleVersion(b);
            final UpdateDocument updateDocument = new UpdateDocument(uri.toURL());
            if (updateDocument.isValid()) {
                final Version version = updateDocument.getVersion();
                if (version.compareTo(pluginVersion) > 0) {
                    // New version available!
                    return new UpdateInfo(b,
                                          version,
                                          updateDocument.getDownloadLocation(),
                                          updateDocument.getReadmeLocation());
                }
            }
            else {
                logger.debug("Invalid plugin update file (" + uri + ")");
            }
        }
        catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return null;
    }
}
