package org.protege.editor.core.update;




import java.net.URI;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.protege.editor.core.plugin.PluginUtilities;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 * <p/>
 * Encapsulates information about a new version of a plugin.
 */
public class UpdateInfo {

    private Bundle b;

    private Version availableVersion;

    private URI downloadURL;

    private URI readmeURI;


    public UpdateInfo(Bundle b, Version availableVersion, URI downloadURL, URI readmeURI) {
        this.b = b;
        this.availableVersion = availableVersion;
        this.downloadURL = downloadURL;
        this.readmeURI = readmeURI;
    }


    public Version getAvailableVersion() {
        return availableVersion;
    }


    public Version getCurrentVersion() {
        return PluginUtilities.getBundleVersion(b);
    }


    public URI getDownloadURL() {
        return downloadURL;
    }


    public Bundle getPluginDescriptor() {
        return b;
    }


    public URI getReadmeURI() {
        return readmeURI;
    }
}
