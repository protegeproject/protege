package org.protege.editor.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleBuilder {
    private static final int BUFFER_SIZE = 10240;
    
    private final transient Logger logger = LoggerFactory.getLogger(BundleBuilder.class);
    public static final char JAR_SEPARATOR = '/';
    
    
    private File topLevelDirectory;
    private int topLevelDirectoryLength;
    private File manifest;
    
    public BundleBuilder(File directory) {
        this.topLevelDirectory = directory;
        String topLevel = topLevelDirectory.getAbsolutePath();
        topLevelDirectoryLength = topLevel.length();
    }
    
    public void createJarFile(File jar) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating jar file " + jar + " from directory " + topLevelDirectory);
        }
        File manifest = getManifest();
        if (manifest == null) {
            throw new IOException("No manifest found");
        }
        FileOutputStream fileOutputStream = null;
        FileInputStream manifestInputStream = null;
        JarOutputStream jarOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(jar);
            manifestInputStream = new FileInputStream(manifest);
            jarOutputStream = new JarOutputStream(fileOutputStream, 
                                                  new Manifest(manifestInputStream));
            addDirectory(jarOutputStream, topLevelDirectory);
        }
        finally {
            if (jarOutputStream != null) {
                jarOutputStream.close();
            }
            else {
                if (fileOutputStream != null) fileOutputStream.close();
                if (manifestInputStream != null) manifestInputStream.close();
            }
        }
    }
    
    private void addDirectory(JarOutputStream jar, File dir) throws IOException {
        for (File f : dir.listFiles()) {
            String path = calculatePath(f);
            if (f.isFile()) {
                if (f.equals(getManifest())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping manifest entry " + f);
                    }
                    continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding entry for file " + f + " with path " + path);
                }
                JarEntry entry = new JarEntry(path);
                jar.putNextEntry(entry);
                byte buffer[] = new byte[BUFFER_SIZE];
                FileInputStream input = new FileInputStream(f);
                try {
                    int readCount = 0;
                    while ((readCount = input.read(buffer)) > 0) {
                        jar.write(buffer, 0, readCount);
                    }
                }
                finally {
                    input.close();
                }
            }
            else {
                addDirectory(jar, f);
            }
        }
    }
    
    private String calculatePath(File f) {
        String path = f.getAbsolutePath().substring(topLevelDirectoryLength + 1);
        path = path.replace(File.separatorChar, JAR_SEPARATOR);
        return path;
    }
    
    private File getManifest() {
        if (manifest == null) {
            manifest = new File(topLevelDirectory, "META-INF/MANIFEST.MF");
            if (manifest.exists()) {
                return manifest;
            }
            manifest = new File(topLevelDirectory, "meta-inf/manifest.mf");
            if (manifest.exists()) {
                return manifest;
            }
        }
        return manifest;
    }

}
