package org.protege.editor.core.update;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Date: Nov 5, 2008<br><br>
 */
public class RemotePluginRegistry implements DownloadsProvider {

    private static final Logger logger = Logger.getLogger(RemotePluginRegistry.class);

    private URL root;

    List<PluginInfo> downloads = null;

    Set<URL> visitedURLs = new HashSet<URL>();

    public RemotePluginRegistry(URL root) {
        this.root = root;
    }


    private boolean alreadyInstalled(PluginInfo info) {
        BundleContext context = PluginUtilities.getInstance().getApplicationContext();
        for (final Bundle b : context.getBundles()) {
            if (b.getSymbolicName().equals(info.getId())){
                return true;
            }
        }
        return false;
    }

    
    public void reload() {
        downloads = new ArrayList<PluginInfo>();
        visitedURLs.clear();
        visit(root);
    }


    private void visit(URL node) {
        if (!visitedURLs.contains(node)){
            visitedURLs.add(node);

            // see if this is a plugin file
            try{
                UpdateChecker checker = new UpdateChecker(node, null);
                PluginInfo info = checker.run();
                if (info != null && !alreadyInstalled(info)) {
                    downloads.add(info);
                }
            }
            catch(MalformedURLException e){
                readRegistry(node);
            }
            catch (UpdateException e){
                readRegistry(node);
            }
            catch(IOException e){
                logger.debug("Cannot open remote plugin file/registry", e);
            }
        }
    }


    private void readRegistry(URL node) {
        // if it fails, then try to read it as a repository
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(node.openStream())));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0 && !line.startsWith("//")){
                    try{
                        URL url = new URL(line);
                        visit(url);
                    }
                    catch(MalformedURLException urlException){
                        logger.debug("Invalid URL in plugin registry: " + line);
                    }
                }
            }
            reader.close();
        }
        catch (IOException ex) {
            logger.debug("Cannot open remote plugin registry", ex);
        }
    }


    public List<PluginInfo> getAvailableDownloads() {
        if (downloads == null){
            reload();
        }
        return downloads;
    }


    public boolean isSelected(PluginInfo download) {
        return false;
    }
}
