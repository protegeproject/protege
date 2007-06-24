package org.protege.editor.core;

import java.awt.Color;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PropertyUtil {


    public static Color getColor(String color, Color defaultColor) {
        try {
            String parseVal = color;
            if (parseVal.length() == 8) {
                parseVal = parseVal.substring(2, parseVal.length());
            }
            return new Color(Integer.parseInt(parseVal, 16));
        }
        catch (Exception e) {
            return defaultColor;
        }
    }


    public static boolean getBoolean(String value, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(value);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
}
