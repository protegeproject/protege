package org.protege.common.log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.protege.common.ProtegeProperties;

public class ProtegeRotatingAppender extends FileAppender {
    private static final String ROTATION_COUNT_MARKER = "%u";
    private Integer rotationCount;
    private File directory;
    private String prefix;
    private String suffix;
    
    public ProtegeRotatingAppender() {
        super();
    }
    
    ProtegeRotatingAppender(Layout layout, 
                            String directory,
                            String filename,
                            int rotationCount) {
        super();
        setLayout(layout);
        setDirectory(directory);
        setRelativeFile(filename);
        setRotationCount(rotationCount);
    }
    
    public void setDirectory(String dirName) {
        this.directory = new File(ProtegeProperties.getApplicationDirectory(), dirName);
        initializeIfReady();
    }
    
    public void setRelativeFile(String filename) {
        int marker = filename.lastIndexOf(ROTATION_COUNT_MARKER);
        prefix = filename.substring(0, marker);
        suffix = filename.substring(marker + ROTATION_COUNT_MARKER.length());
        initializeIfReady();
    }
    
    public void setRotationCount(int rotationCount) {
        this.rotationCount = rotationCount;
        initializeIfReady();
    }
    
    public int getRotationCount() {
        return rotationCount;
    }
    
    private boolean configured() {
        return directory != null && prefix != null && rotationCount != null;
    }
    
    private void initializeIfReady() {
        if (configured()) {
            setFile(getNextLogFile());
        }
    }
    
    private String getNextLogFile() {
        Map<Integer, File> matchingLogFiles = new HashMap<Integer, File>();
        int lastMatch = -1;
        for (File f : directory.getAbsoluteFile().listFiles()) {
            Integer count = matchingFile(f);
            if (count == null) continue;
            matchingLogFiles.put(count, f);
            if (lastMatch < count) {
                lastMatch = count;
            }
        }
        for (Entry<Integer, File> entry : matchingLogFiles.entrySet()) {
            if (entry.getKey() <= lastMatch - rotationCount + 1) {
                entry.getValue().delete();
            }
        }
        return new File(directory, prefix + (lastMatch + 1) + suffix).getAbsolutePath();
    }
    

    private Integer matchingFile(File f) {
        String relativeName = f.getName();
        if (!relativeName.startsWith(prefix) || !relativeName.endsWith(suffix) || 
                relativeName.length() < prefix.length() + suffix.length()) {
            return null;
        }
        String countString = relativeName.substring(prefix.length(), relativeName.length() - suffix.length());
        try {
            return Integer.parseInt(countString);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

}
