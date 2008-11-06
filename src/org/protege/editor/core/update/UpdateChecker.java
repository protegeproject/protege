package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.protege.editor.core.plugin.PluginUtilities;

import java.io.IOException;
import java.net.URL;

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

    private URL url;

    private Bundle b;


    public UpdateChecker(URL updateFileURL, Bundle pluginDescriptor) {
        this.url = updateFileURL;
        this.b = pluginDescriptor;
    }


    public PluginInfo run() throws IOException, UpdateException {
        final PluginInfoDocument pluginInfoDocument = new PluginInfoDocument(url);
        if (pluginInfoDocument.isValid(b)) {
            PluginInfo info = pluginInfoDocument.getPluginInfo();
            if (b == null || info.getAvailableVersion().compareTo(PluginUtilities.getBundleVersion(b)) > 0) {
                // New version available!
                info.setPluginDescriptor(b);
                return info;
            }
        }
        return null;
    }
}
