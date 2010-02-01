package org.protege.editor.core.ui.util;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UIUtil {

    public static final String FILE_PREFERENCES_KEY = "FILE_PREFERENCES_KEY";

    public static final String CURRENT_FILE_DIRECTORY_KEY = "CURRENT_FILE_DIRECTORY_KEY";

    public static final String ENABLE_TEMP_DIRECTORIES_KEY = "ENABLE_TEMP_DIRECTORIES_KEY";    

    public static String getCurrentFileDirectory() {
        String dir = "~";
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(FILE_PREFERENCES_KEY);
        dir = p.getString(CURRENT_FILE_DIRECTORY_KEY, dir);
        return dir;
    }


    public static void setCurrentFileDirectory(String dir) {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(FILE_PREFERENCES_KEY);
        p.putString(CURRENT_FILE_DIRECTORY_KEY, dir);
    }


    public static File openFile(Window parent, String title, final Set<String> extensions) {
        JFileChooser fileDialog = new JFileChooser(getCurrentFileDirectory());
        if (extensions != null && !extensions.isEmpty()) {
            fileDialog.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return "";
                }

                @Override
                public boolean accept(File f) {
                    if (extensions.isEmpty() || f.isDirectory()) {
                        return true;
                    }
                    else {
                        String name = f.getName();
                        for (String ext : extensions) {
                            if (name.toLowerCase().endsWith(ext.toLowerCase())) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
            });
        }
        fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
        fileDialog.showOpenDialog(parent);
        File f = fileDialog.getSelectedFile();
        if (f != null) {
            if (f.getParent() != null) {
                setCurrentFileDirectory(f.getParent());
            }
            return f;
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
        fileDialog.setDirectory(getCurrentFileDirectory());
        if (initialName != null) {
            fileDialog.setFile(initialName);
        }
        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        if (fileName != null) {
            setCurrentFileDirectory(fileDialog.getDirectory());
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }


    public static File saveFile(Window parent, String title, final Set<String> extensions) {
        return saveFile(parent, title, extensions, null);
    }


    public static File chooseFolder(Component parent, String title) {
        if (System.getProperty("os.name").indexOf("OS X") != -1) {
            return chooseOSXFolder(parent, title);
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }


    private static File chooseOSXFolder(Component parent, String title) {
        String prop = null;
        File file = null;
        try {
            prop = "apple.awt.fileDialogForDirectories";
            System.setProperty(prop, "true");
            file = openFile((Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent),
                            title,
                            Collections.singleton(""));
        }
        finally {
            System.setProperty(prop, "false");
        }
        return file;
    }


    public static void openRequest(OpenRequestHandler handler) throws Exception {

        int ret = JOptionPane.showConfirmDialog(handler.getCurrentWorkspace(),
                                      "Do you want to open the ontology in the current window?",
                                      "Open in current window",
                                      JOptionPane.YES_NO_CANCEL_OPTION,
                                      JOptionPane.QUESTION_MESSAGE);

        if (ret == JOptionPane.YES_OPTION){
            handler.openInCurrentWorkspace();
        }
        else if (ret == JOptionPane.NO_OPTION){
            handler.openInNewWorkspace();
        }
    }
}
