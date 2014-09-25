package org.protege.editor.core.platform;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 19, 2008<br><br>
 */
public class OSUtils {

    private static final String OS_NAME = "os.name";


    public static boolean isOSX() {
        String property = System.getProperty(OS_NAME);
        if(property == null) {
            return false;
        }
        return property.toLowerCase().contains("os x");
    }


    public static boolean isWindows() {
        return System.getProperty(OS_NAME).indexOf("Windows") != -1;
    }
}
