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

import org.apache.log4j.Logger;
import org.protege.editor.core.BundleManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginInstaller {

    private static final Logger logger = Logger.getLogger(PluginInstaller.class);

    private List<PluginInfo> updates;


    public PluginInstaller(List<PluginInfo> updates) {
        this.updates = updates;
    }


    public void run() {
        for (PluginInfo info : updates) {
            logger.info("Downloading " + info.getLabel());
            try {
                URL downloadURL = info.getDownloadURL();
                final String[] path = downloadURL.getFile().split("/");
                String downloadFileName = path[path.length-1];
                String tmpPath = System.getProperty("java.io.tmpdir");
                File tempPluginFile = new File(tmpPath, downloadFileName);
                tempPluginFile.deleteOnExit();
                URLConnection conn = downloadURL.openConnection();
//                int len = conn.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempPluginFile));
//                int totalRead = 0;
                while (true) {
                    byte [] buffer = new byte [4068];
                    int read = bis.read(buffer);
                    if (read == -1) {
                        break;
                    }
//                    totalRead += read;
//                    logger.info("Downloading " + ((int) (((totalRead * 100.0) / len)) + "%"));
                    bos.write(buffer, 0, read);
                }
                bis.close();
                bos.flush();
                bos.close();

                // Extract if a zip file
                if (downloadURL.getFile().endsWith(".zip")){
                    tempPluginFile = extractPlugin(tempPluginFile, info);
                }

                copyPluginToInstallLocation(tempPluginFile, info);
            }
            catch (URISyntaxException e) {
                logger.error(e);
            }
            catch (IOException e) {
                logger.error(e);
            }
        }
    }


    private static void copyPluginToInstallLocation(File pluginFile, PluginInfo info) throws URISyntaxException {
        logger.info("Copying " + info.getLabel());
        boolean doCopy = true;

        File oldPluginFile = null;

        if (info.getPluginDescriptor() != null){
            String location = info.getPluginDescriptor().getLocation();
            location = location.substring(location.indexOf(":")+1, location.length());
            File existingPlugin = new File(location);

            oldPluginFile = new File(existingPlugin.getName() + "-old");
            doCopy = existingPlugin.renameTo(oldPluginFile);
        }

        if (doCopy){
            File pluginsFolder = new File(System.getProperty(BundleManager.BUNDLE_DIR_PROP));
            final File newPluginFile = new File(pluginsFolder, pluginFile.getName());
            if (pluginFile.renameTo(newPluginFile)){
                if (oldPluginFile != null && oldPluginFile.exists()){
                    delete(oldPluginFile);
                }
            }
            else{
                logger.error("Could not create plugin: " + newPluginFile);
            }
        }
    }


    private static void delete(File file) {
        if (file.isDirectory()){
            for(File f : file.listFiles()){
                delete(f);
            }
        }
        file.delete();
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
        else{
            File pluginXMLFile = new File(startDir, "plugin.xml");
            if (pluginXMLFile.exists()) {
                return startDir;
            }
        }
        for (File f : startDir.listFiles()) {
            File pluginDir = getPluginDir(f);
            if (pluginDir != null) {
                return pluginDir;
            }
        }
        return null;
    }
}
