package org.protege.editor.core.platform.apple;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.protege.editor.core.ui.util.UIUtil;

public class MacUIUtil {

    public static File openFile(Window parent, String title, final Set<String> extensions) {
        FileDialog fileDialog;
        if (parent instanceof Frame) {
            fileDialog = new FileDialog((Frame) parent, title, FileDialog.LOAD);
        }
        else {
            fileDialog = new FileDialog((Dialog) parent, title, FileDialog.LOAD);
        }
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (extensions.isEmpty()) {
                    return true;
                }
                else {
                    for (String ext : extensions) {
                        if (name.toLowerCase().endsWith(ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        });
        fileDialog.setDirectory(UIUtil.getCurrentFileDirectory());
        fileDialog.setVisible(true);
        String fileName = fileDialog.getFile();
        if (fileName != null) {
            UIUtil.setCurrentFileDirectory(fileDialog.getDirectory());
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }
    
    public static File saveFile(Window parent, String title, final Set<String> extensions, String initialName) {
        FileDialog fileDialog;
        if (parent instanceof Frame) {
            fileDialog = new FileDialog((Frame) parent, title, FileDialog.SAVE);
        }
        else {
            fileDialog = new FileDialog((Dialog) parent, title, FileDialog.SAVE);
        }
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (extensions.isEmpty()) {
                    return true;
                }
                else {
                    for (String ext : extensions) {
                        if (name.toLowerCase().endsWith(ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        });
        fileDialog.setDirectory(UIUtil.getCurrentFileDirectory());
        if (initialName != null) {
            fileDialog.setFile(initialName);
        }
        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        if (fileName != null) {
            UIUtil.setCurrentFileDirectory(fileDialog.getDirectory());
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }

	public static File chooseOSXFolder(Component parent, String title) {
	    String prop = null;
	    File file = null;
	    try {
	        prop = "apple.awt.fileDialogForDirectories";
	        System.setProperty(prop, "true");
	        file = UIUtil.openFile((Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent),
	                        title,
	                        "Folder",
	                        Collections.singleton(""));
	    }
	    finally {
	        System.setProperty(prop, "false");
	    }
	    return file;
	}
}
