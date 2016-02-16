package org.protege.editor.core.platform.apple;

import org.protege.editor.core.ui.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Set;

public class MacUIUtil {

    public static File openFile(Window parent, String title, final Set<String> extensions) {
        FileDialog fileDialog;
        if (parent instanceof Frame) {
            fileDialog = new FileDialog((Frame) parent, title, FileDialog.LOAD);
        }
        else {
            fileDialog = new FileDialog((Dialog) parent, title, FileDialog.LOAD);
        }
        fileDialog.setFilenameFilter((dir, name) -> {
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
        fileDialog.setFilenameFilter((dir, name) -> {
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
