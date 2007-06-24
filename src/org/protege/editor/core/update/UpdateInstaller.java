package org.protege.editor.core.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class UpdateInstaller {

    private static final Logger logger = Logger.getLogger(UpdateInstaller.class);


    public static final String CONTINUE = "OK";

    public static final String WEBSITE = "Open website in browser";

    public static final String DOWNLOAD = "Download";

    private List<UpdateInfo> updates;


    public UpdateInstaller(List<UpdateInfo> updates) {
        this.updates = updates;
    }


    public void run() {
        for (UpdateInfo info : updates) {
            try {
                File tempPluginFile = File.createTempFile(info.getCurrentVersion().getQualifier(), ".zip");
                URLConnection conn = info.getDownloadURL().toURL().openConnection();
                int len = conn.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempPluginFile));
                int totalRead = 0;
                while (true) {
                    byte [] buffer = new byte [4068];
                    int read = bis.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    totalRead += read;
                    System.out.println("Downloaded " + ((int) (((totalRead * 100.0) / len)) + "%"));
                    bos.write(buffer, 0, read);
                }
                bis.close();
                bos.flush();
                bos.close();

                // Extract
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


    private static void copyPluginToInstallLocation(File pluginFile, UpdateInfo info) throws URISyntaxException {
        File existingPluginXMLFile = new File(new URI(info.getPluginDescriptor().getLocation()));
        File installLocation = existingPluginXMLFile.getParentFile();
//          pluginFile.renameTo(new File(installLocation, pluginFile.getName()));
    }


    /**
     * Extracts the contents of a zip file, which is assumed to contain a plugin,
     * and finds the directory that contains the plugin.
     * @param pluginArchive The zip file that contains the plugin
     * @return The directory that contains the plugin
     */
    private static File extractPlugin(File pluginArchive) throws IOException {
        File tempDir = new File(pluginArchive.getParentFile(), pluginArchive.getName() + "-extracted");
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
//            System.out.println("Extracting " + entry.getName());
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


    private static File getPluginDir(File startDir) {
        if (!startDir.isDirectory()) {
            return null;
        }
        File pluginXMLFile = new File(startDir, "plugin.xml");
        if (pluginXMLFile.exists()) {
            return startDir;
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
