package org.protege.editor.core.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.common.CommonProtegeProperties;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginInstaller {

    private static final Logger logger = Logger.getLogger(PluginInstaller.class);

    private List<PluginInfo> updates;
    
    public enum InstallerResult {
        DOWNLOADED, ERROR, INSTALLED; 
    }


    public PluginInstaller(List<PluginInfo> updates) {
        this.updates = updates;
    }


    public void run() {
        final BackgroundTask installAllTask = ProtegeApplication.getBackgroundTaskManager().startTask("installing plugins");

        Runnable r = new Runnable(){

            public void run() {
                boolean errorsFound = false;
                boolean someInstalled = false;
                try {
                	for (PluginInfo info : updates) {
                		InstallerResult result = install(info);
                		switch (result) {
                		case ERROR:
                			errorsFound = true;
                			break;
                		case INSTALLED:
                			someInstalled = true;
                			break;
                		}
                	}
                }
                finally {
                	ProtegeApplication.getBackgroundTaskManager().endTask(installAllTask);
                }
                if (errorsFound) {
                    JOptionPane.showMessageDialog(null, "Some errors found downloading plugins - look at the console log");
                }
                else if (someInstalled) {
                    JOptionPane.showMessageDialog(null, "Updates will take effect when you next start Protege.");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Updates will take effect when you next start Protege."); 
                }
            }
        };

        Thread t = new Thread(r, "Installing plugins thread");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }


    private InstallerResult install(PluginInfo info) {
        BackgroundTask downloading = ProtegeApplication.getBackgroundTaskManager().startTask("downloading " + info.getLabel());
        logger.info("Downloading " + info.getLabel());
        try {
            File tempPluginFile = downloadPlugin(info);
            if (tempPluginFile == null) {
                logger.error("Could not download plugin");
                return InstallerResult.ERROR;
            }
            File installedPluginFile = copyPluginToInstallLocation(tempPluginFile, info);
            if (installedPluginFile == null) {
                logger.error("Could not install plugin");
                return InstallerResult.ERROR;
            }
            if (installPlugin(installedPluginFile, info)) {
                return InstallerResult.INSTALLED;
            }
            else {
                return InstallerResult.DOWNLOADED;
            }
        }
        catch (Throwable t) {
            logger.error("Exception caught installing plugins",  t);
            return InstallerResult.ERROR;
        }
        finally{
            ProtegeApplication.getBackgroundTaskManager().endTask(downloading);
        }
    }
    
    private File downloadPlugin(PluginInfo info) throws IOException {
        URL downloadURL = info.getDownloadURL();
        File tempPluginFile = File.createTempFile(info.getId(), ".jar");
        tempPluginFile.deleteOnExit();

        logger.debug("Download URL: " + downloadURL.toString());
        logger.debug("Temp file: " + tempPluginFile.getAbsolutePath());

        URLConnection conn = downloadURL.openConnection();
        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempPluginFile));
        while (true) {
            byte [] buffer = new byte [4068];
            int read = bis.read(buffer);
            if (read == -1) {
                break;
            }
            bos.write(buffer, 0, read);
        }
        bis.close();
        bos.flush();
        bos.close();

        // Extract if a zip file
        if (downloadURL.getFile().endsWith(".zip")){
            tempPluginFile = extractPlugin(tempPluginFile, info);
        }
        return tempPluginFile;
    }


    private static File copyPluginToInstallLocation(File pluginFile, PluginInfo info) throws URISyntaxException {
    	logger.info("Copying " + info.getLabel());
    	File pluginsFolder = new File(System.getProperty(ProtegeApplication.BUNDLE_DIR_PROP));
    	File oldPluginFile = null;
    	File newPluginFile = null;

    	if (info.getPluginDescriptor() != null) {
    		String location = info.getPluginDescriptor().getLocation();
    		location = location.substring(location.indexOf(":")+1, location.length());
    		File existingPlugin = new File(location);
    		if (existingPlugin.exists()) {
    			oldPluginFile = new File(existingPlugin.getAbsolutePath() + "-old");
    			if (!existingPlugin.renameTo(oldPluginFile)) {
    				oldPluginFile = null;
    			}
    			newPluginFile = existingPlugin;
    		}
    	}
    	if (newPluginFile == null) {
    		newPluginFile = new File(pluginsFolder, info.getId() + ".jar");
    	}
    	try{
    		FileUtils.copyFileToDirectory(pluginFile, newPluginFile);
    	}
    	catch(IOException e){
    		logger.error("Could not save plugin to system directory, trying user plugin directory. (" + e.getMessage() + ")");
    		newPluginFile = new File(CommonProtegeProperties.getUserPluginDirectory(), info.getId() + ".jar");
    		try {
    			FileUtils.copyFileToDirectory(pluginFile, newPluginFile);
    			logger.info("Save of plugin to user plugin directory succeeded");
    			logger.info("Update only seen by invoking user");
    		}
    		catch (IOException ioe) {
    			logger.error("Could not save plugin", ioe);
    		}
    	}
    	if (oldPluginFile != null && oldPluginFile.exists()){
    		FileUtils.deleteRecursively(oldPluginFile);
    	}
    	return newPluginFile;
    }

    /**
     * Extracts the contents of a zip file, which is assumed to contain a plugin,
     * and finds the directory that contains the plugin.
     * @param pluginArchive The zip file that contains the plugin
     * @return The jar or directory that contains the plugin
     */
    private static File extractPlugin(File pluginArchive, PluginInfo info) throws IOException {
        logger.info("Extracting " + info.getLabel());
        File tempDir = new File(pluginArchive.getParentFile(), pluginArchive.getName() + "-extracted");
        tempDir.deleteOnExit();
        tempDir.mkdir();
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(pluginArchive)));
        ZipEntry entry;
        byte [] buffer = new byte[4068];
        while ((entry = zis.getNextEntry()) != null) {
            // Skip Mac rubbish!
            if (entry.getName().indexOf(".DS_Store") != -1) {
                continue;
            }
            if (entry.getName().indexOf("__MACOSX") != -1) {
                continue;
            }
            File curFile = new File(tempDir, entry.getName());
            if (entry.isDirectory()) {
                curFile.mkdirs();
            }
            else {
                OutputStream os = new BufferedOutputStream(new FileOutputStream(curFile));
                int count = 0;
                while ((count = zis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                os.flush();
                os.close();
            }
        }
        return getPluginDir(tempDir);
    }


    /**
     * Find a jar or a folder containing a plugin.xml file
     * @param startDir the directory to search in
     * @return the first jar or plugin folder found
     */
    private static File getPluginDir(File startDir) {
        if (!startDir.isDirectory()) {
            if (startDir.getName().endsWith(".jar")){
                return startDir;
            }
            return null;
        }
        for (File f : startDir.listFiles()) {
            File pluginDir = getPluginDir(f);
            if (pluginDir != null) {
                return pluginDir;
            }
        }
        return null;
    }
    
    private boolean installPlugin(File pluginLocation, PluginInfo info) throws BundleException {
        if (info.getPluginDescriptor() == null) {  // download not an update...
            logger.info("Loading " + info.getLabel());
            Bundle b = ProtegeApplication.getContext().installBundle("file:" + pluginLocation.getPath());
            b.start();
            return true;
        }
        else {
            logger.info("Plugin " + info.getLabel() + " will be loaded when " + ProtegeProperties.PROTEGE + " is restarted");
            return false;
        }
    }
}
