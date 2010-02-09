package org.protege.editor.core.update;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 6, 2008<br><br>
 */
public class UpdatesProvider implements DownloadsProvider{

    private static final Logger logger = Logger.getLogger(UpdatesProvider.class);

    private static final String UPDATE_URL = "Update-Url";

    List<PluginInfo> updates = new ArrayList<PluginInfo>();

    public UpdatesProvider() {
        DownloadsProvider downloadsRegistry = PluginManager.getInstance().getPluginRegistry();
        BundleContext context = PluginUtilities.getInstance().getApplicationContext();
        for (final Bundle b : context.getBundles()) {
            try {
                String updateLocation = (String) b.getHeaders().get(UPDATE_URL);
                if (updateLocation != null) {
                    URL url = new URL(updateLocation);
                    testUrl(b, url);
                }
                else {
                    for (PluginInfo pluginInfo : downloadsRegistry.getAvailableDownloads()) {
                        if (b.getSymbolicName().equals(pluginInfo.getId())) {
                            testUrl(b, pluginInfo.getDownloadURL());
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.debug(b.getHeaders().get("Bundle-Name") + ": " + e.getMessage());
            }
        }
    }
    
    public void testUrl(Bundle b, URL url) throws IOException, UpdateException {
        UpdateChecker checker = new UpdateChecker(url, b);
        PluginInfo info = checker.run();
        if (info != null) {
            updates.add(info);
        }
    }


    public List<PluginInfo> getAvailableDownloads() {
        return updates;
    }


    public boolean isSelected(PluginInfo download) {
        return true;
    }
}
