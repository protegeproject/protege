package org.protege.editor.core;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);


    /**
     * Copies a file to the specified directory.
     * @param file      The file to be copied.
     * @param directory The directory the file should be copied to.
     */
    public static void copyFileToDirectory(File file, File directory) throws IOException {

        File outputFile = new File(directory, file.getName());

        FileInputStream in = new FileInputStream(file);
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
        if (System.getProperty("os.name").indexOf("Mac") != -1) {
            showInFinder(file);
        }
        else if (System.getProperty("os.name").indexOf("Windows") != -1) {
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
        if (path.indexOf(" ") != -1) {
            // The path contains spaces, so we must surround it with quotes
            path = "\"" + path + "\"";
        }
        String [] params = new String []{"explorer", "/n,/select," + path};

        Runtime.getRuntime().exec(params);
    }
}
