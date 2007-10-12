package org.protege.common.log;

import java.io.File;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;

public class ProtegeRotatingAppender extends FileAppender {
    private static String LOG_PREFIX = "logs/protege_";
    private int rotationCount;
    
    public ProtegeRotatingAppender() {
        super();
    }
    
    ProtegeRotatingAppender(Layout layout, int rotationCount) {
        super();
        setLayout(layout);
        setRotationCount(rotationCount);
    }
    
    public void setRotationCount(int rotationCount) {
        this.rotationCount = rotationCount;
        String fileName = getNextLogFile();
        setFile(fileName);
    }
    
    public int getRotationCount() {
        return rotationCount;
    }
    
    private String getNextLogFile() {
        int i = getAvailableFileCounter();
        File nextEmpty = new File(makeFileName((i+1) % (rotationCount + 1)));
        if (nextEmpty.exists()) {
            nextEmpty.delete();
        }
        return makeFileName(i);
    }
    
    private int getAvailableFileCounter() {
        for (int i = 0; i <= rotationCount; i++) {
            File f = new File(makeFileName(i));
            if (!f.exists()) {
                return i;
            }
        }
        return 0;
    }
    
    private String makeFileName(int i) {
        return "logs/protege_" + (i % (rotationCount + 1)) + ".log";
    }

}
