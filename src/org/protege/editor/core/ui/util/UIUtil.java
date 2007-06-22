package org.protege.editor.core.ui.util;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
        fileDialog.setDirectory(getCurrentFileDirectory());
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
}
