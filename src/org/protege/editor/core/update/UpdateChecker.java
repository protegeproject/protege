package org.protege.editor.core.update;

import org.apache.log4j.Logger;



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

    private PluginDescriptor pluginDescriptor;


    public UpdateChecker(URI updateFileURL, PluginDescriptor pluginDescriptor) {
        this.uri = updateFileURL;
        this.pluginDescriptor = pluginDescriptor;
    }


    public UpdateInfo run() {
        try {

            final UpdateDocument updateDocument = new UpdateDocument(uri.toURL());
            if (updateDocument.isValid()) {
                final Version version = updateDocument.getVersion();
                if (version.isGreaterThan(pluginDescriptor.getVersion())) {
                    // New version available!
                    return new UpdateInfo(pluginDescriptor,
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
