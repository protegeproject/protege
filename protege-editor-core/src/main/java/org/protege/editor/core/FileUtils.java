package org.protege.editor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);


    /**
     * Copies a file to the specified directory.
     * @param file      The file to be copied.
     * @param directory The directory the file should be copied to.
     */
    public static void copyFileToDirectory(File file, File directory) throws IOException {
        copyFile(file, directory);
    }

    /**
     * Copies a file to the specified location.
     * @param inputFile      The file to be copied.
     * @param outputFile The directory the file should be copied to.
     */
    public static void copyFile(File inputFile, File outputFile) throws IOException {
        if (inputFile.isDirectory()){
            throw new IOException("Cannot copy file: " + inputFile + " as this is a directory");
        }
        if (outputFile.isDirectory()){
            outputFile = new File(outputFile, inputFile.getName());
        }

        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();

        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);

        FileChannel inc = in.getChannel();
        FileChannel outc = out.getChannel();

        inc.transferTo(0, inc.size(), outc);

        inc.close();
        outc.close();

        in.close();
        out.close();
    }


    /**
     * Attempts to show a File using the native operating
     * system file view, for example the Finder on a Mac,
     * or Explorer on Windows.  This may not work on all
     * platforms.
     * @param file The file to be shown
     */
    public static void showFile(File file) throws IOException {
        if (System.getProperty("os.name").contains("Mac")) {
            showInFinder(file);
        }
        else if (System.getProperty("os.name").contains("Windows")) {
            showInExplorer(file);
        }
        else {
            logger.debug("showFile not implemented for " + System.getProperty("os.name"));
        }
    }


    /**
     * Attempts to show a file in the Finder on a Mac.
     * @param file The file to be shown
     */
    private static void showInFinder(File file) throws IOException {
        // Use applescript to show the item in the Finder
        String[] params = new String[]{"osascript", "-e", "set p to \"" + file.getCanonicalPath() + "\"", "-e", "tell application \"Finder\"", "-e", "reveal (POSIX file p) as alias", "-e", "activate", "-e", "end tell",};
        Runtime.getRuntime().exec(params);
    }


    /**
     * Attempts to show a file in the Explorer on Windows.
     * @param file The file to be shown.
     */
    private static void showInExplorer(File file) throws IOException {
        String path = file.getCanonicalPath();
        if (path.contains(" ")) {
            // The path contains spaces, so we must surround it with quotes
            path = "\"" + path + "\"";
        }
        String [] params = new String []{"explorer", "/n,/select," + path};

        Runtime.getRuntime().exec(params);
    }


    public static void deleteRecursively(File file) {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files != null) {
                for(File f : files){
                    deleteRecursively(f);
                }
            }
        }
        file.delete();
    }


    public static File createTempFile(File targetFile) throws IOException {
        final String targetName = targetFile.getName();
        final int extensionIndex = targetName.lastIndexOf(".");
        String filename = targetName.substring(0, extensionIndex);
        for (int i=filename.length(); i<3; i++){
            filename += "_";
        }
        return Files.createTempFile(filename, targetName.substring(extensionIndex)).toFile();
    }

    public static void showLogFile() {
        try {
            String userHome = System.getProperty("user.home");
            Path logFilePath = Paths.get(userHome, ".Protege", "logs", "protege.log");
            FileUtils.showFile(logFilePath.toFile());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred whilst trying to show the log file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.error("An error occurred whilst trying to show a file in the OS: {}", e.getMessage(), e);
        }
    }
}
