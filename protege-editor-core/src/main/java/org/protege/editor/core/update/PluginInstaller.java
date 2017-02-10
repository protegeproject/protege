package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.core.ui.util.ErrorMessage;
import org.protege.editor.core.util.ProtegeDirectories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>
 */
public class PluginInstaller {

    private static final Logger logger = LoggerFactory.getLogger(PluginInstaller.class);

    private final List<PluginInfo> updates;

    public enum InstallerResult {
        DOWNLOADED, ERROR, INSTALLED;
    }


    public PluginInstaller(List<PluginInfo> updates) {
        this.updates = new ArrayList<>(updates);
    }


    public void run() {
        final BackgroundTask installAllTask = ProtegeApplication.getBackgroundTaskManager().startTask("installing plugins");

        Runnable r = () -> {
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
            } finally {
                ProtegeApplication.getBackgroundTaskManager().endTask(installAllTask);
            }
            if (errorsFound) {
                ErrorMessage.showErrorMessage("Plugin Installer",
                        "Some errors occurred whilst installing the downloaded plugins.");
            }
            else if (someInstalled) {
                JOptionPane.showMessageDialog(null, "Updates will take effect when you next start Protege.");
            }
            else {
                JOptionPane.showMessageDialog(null, "Updates will take effect when you next start Protege.");
            }
        };

        Thread t = new Thread(r, "Installing plugins thread");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }


    private InstallerResult install(PluginInfo info) {
        logger.info(LogBanner.start("Downloading and Installing Plugin"));
        BackgroundTask downloading = ProtegeApplication.getBackgroundTaskManager().startTask("downloading " + info.getLabel());
        logger.info("Downloading the {} plugin (Version {})", info.getLabel(), info.getAvailableVersion());
        try {
            Optional<File> downloadedPlugin = downloadPlugin(info);
            if (!downloadedPlugin.isPresent()) {
                logger.error("An error occurred whilst downloading the {} plugin.  The plugin has not been installed.", info.getLabel());
                return InstallerResult.ERROR;
            }
            Optional<File> installedPluginFile = copyPluginToInstallLocation(downloadedPlugin.get(), info);
            if (!installedPluginFile.isPresent()) {
                logger.error("There was an error whilst trying to install the {} plugin. It has not been installed", info.getLabel());
                return InstallerResult.ERROR;
            }
            if (installPlugin(installedPluginFile.get(), info)) {
                return InstallerResult.INSTALLED;
            }
            else {
                return InstallerResult.DOWNLOADED;
            }
        } catch (IOException | URISyntaxException  t) {
            logger.error("An error occurred whilst downloading and installing the {} plugin: {}", info.getLabel(), t.getMessage(), t);
            return InstallerResult.ERROR;
        }
        finally {
            logger.info(LogBanner.end());
            ProtegeApplication.getBackgroundTaskManager().endTask(downloading);
        }
    }

    private Optional<File> downloadPlugin(PluginInfo info) throws IOException {

        URL downloadURL = info.getDownloadURL();
        logger.info("Downloading the {} plugin from: {}", info.getLabel(), downloadURL);

        File tempPluginFile = File.createTempFile(info.getId(), ".jar");
        tempPluginFile.deleteOnExit();

        URLConnection conn = downloadURL.openConnection();
        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempPluginFile));
        while (true) {
            byte[] buffer = new byte[4068];
            int read = bis.read(buffer);
            if (read == -1) {
                break;
            }
            bos.write(buffer, 0, read);
        }
        bis.close();
        bos.flush();
        bos.close();

        logger.info("The {} plugin has been downloaded to {}", info.getLabel(), tempPluginFile.getAbsolutePath());

        // Extract if a zip file
        if (downloadURL.getFile().endsWith(".zip")) {
            return extractPlugin(tempPluginFile, info);
        }
        else {
            return Optional.of(tempPluginFile);
        }
    }

    private static Optional<File> getPluginFileName(PluginInfo info) {
        Bundle pluginDescriptor = info.getPluginDescriptor();
        if (pluginDescriptor == null) {
            return Optional.empty();
        }
        final String locationURL = pluginDescriptor.getLocation();
        File existingPluginLocation = new File(locationURL.substring(locationURL.indexOf(":") + 1, locationURL.length()));
        return Optional.of(existingPluginLocation);
    }
//
//    private static void movePluginToOldPluginFileName(File pluginLocation) {
//        checkNotNull(pluginLocation);
//        File pluginLocationDestination = getPluginBackupFileName(pluginLocation);
//        pluginLocation.renameTo(pluginLocationDestination);
//
//    }

    private static File getPluginBackupFileName(File pluginFileName) {
        return new File(pluginFileName.getAbsolutePath() + ".old");
    }

    private static Optional<File> moveExistingPluginToBackupLocation(PluginInfo pluginInfo) {
        Optional<File> existingPluginFileName = getPluginFileName(pluginInfo);
        if(!existingPluginFileName.isPresent()) {
            return Optional.empty();
        }
        File backupFileName = getPluginBackupFileName(existingPluginFileName.get());
        if(existingPluginFileName.get().renameTo(backupFileName)) {
            return Optional.of(backupFileName);
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<File> copyPluginToInstallLocation(File downloadedPlugin, PluginInfo info) throws URISyntaxException {

        final Optional<File> backupFileName = moveExistingPluginToBackupLocation(info);
        final File pluginsFolder = new File(System.getProperty(ProtegeApplication.BUNDLE_DIR_PROP));
        final String destinationFileName = String.format("%s-%s.jar", info.getId(), info.getAvailableVersion());
        final File downloadedPluginDestination = new File(pluginsFolder, destinationFileName);

        try {
            FileUtils.copyFile(downloadedPlugin, downloadedPluginDestination);
            logger.info("Copied the {} plugin to {} in the plugins directory", info.getLabel(), downloadedPluginDestination.getName());
            deletePluginBackup(backupFileName);
            return Optional.of(downloadedPluginDestination);
        }
        catch (IOException e) {
            try {
                File userPluginDirectory = ProtegeDirectories.getUserPluginDirectory();
                logger.info("Could not copy the plugin to Protege plugins directory: {}", e.getMessage());
                File userDirectoryPluginFile = new File(userPluginDirectory, info.getId() + ".jar");
                FileUtils.copyFileToDirectory(downloadedPlugin, userDirectoryPluginFile);
                logger.info("Copied the {} plugin to the user plugin directory at {}.  " +
                                "This plugin will only be use-able by the current user.",
                        info.getLabel(),
                        userPluginDirectory);
                deletePluginBackup(backupFileName);
                return Optional.of(userDirectoryPluginFile);
            } catch (IOException ioe) {
                logger.error("An error occurred whilst attempting to save the plugin: {}", ioe.getMessage(), ioe);
                return Optional.empty();
            }
        }
    }

    private static void deletePluginBackup(Optional<File> existingPluginLocation) {
        if (existingPluginLocation.isPresent() && existingPluginLocation.get().exists()) {
            FileUtils.deleteRecursively(existingPluginLocation.get());
        }
    }

    /**
     * Extracts the contents of a zip file, which is assumed to contain a plugin,
     * and finds the directory that contains the plugin.
     *
     * @param pluginArchive The zip file that contains the plugin
     * @return The jar or directory that contains the plugin
     */
    private static Optional<File> extractPlugin(File pluginArchive, PluginInfo info) throws IOException {
        logger.info("Extracting {} plugin from zip file ", info.getLabel());
        File tempDir = new File(pluginArchive.getParentFile(), pluginArchive.getName() + "-extracted");
        tempDir.deleteOnExit();
        tempDir.mkdir();
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(pluginArchive)));
        ZipEntry entry;
        byte[] buffer = new byte[4068];
        while ((entry = zis.getNextEntry()) != null) {
            // Skip Mac rubbish!
            if (entry.getName().contains(".DS_Store")) {
                continue;
            }
            if (entry.getName().contains("__MACOSX")) {
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
     *
     * @param startDir the directory to search in
     * @return the first jar or plugin folder found.  Optional.empty() if nothing is found.
     */
    private static Optional<File> getPluginDir(File startDir) {
        if (!startDir.isDirectory()) {
            if (startDir.getName().endsWith(".jar")) {
                return Optional.of(startDir);
            }
            return Optional.empty();
        }
        File[] files = startDir.listFiles();
        if (files == null) {
            return Optional.empty();
        }
        for (File f : files) {
            Optional<File> pluginDir = getPluginDir(f);
            if (pluginDir.isPresent()) {
                return pluginDir;
            }
        }
        return Optional.empty();
    }

    private boolean installPlugin(File pluginLocation, PluginInfo info) {
        if (info.getPluginDescriptor() == null) {  // download not an update...
            logger.info("Installing the {} plugin", info.getLabel());
            BundleContext context = ProtegeApplication.getContext();
            try {
                Bundle b = context.installBundle("file:" + pluginLocation.getPath());
                b.start();
                return true;
            } catch (BundleException e) {
                logger.info("The {} plugin requires a restart of Protégé (Reason: {})", info.getLabel(), e.getMessage());
                return false;
            }
        }
        else {
            logger.info("The {} plugin requires a restart of Protégé", info.getLabel());
            return false;
        }
    }
}
