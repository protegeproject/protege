package org.protege.editor.owl.ui;

import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLIcons {

    private static HashMap<String, Icon> iconMap;


    static {
        iconMap = new HashMap<String, Icon>();
    }


    public static Icon getIcon(String name) {
        Icon icon = iconMap.get(name);
        if (icon != null) {
            return icon;
        }
        else {
            ClassLoader loader = OWLIcons.class.getClassLoader();
            URL url = loader.getResource(name);
            if (url != null) {
                Icon loadedIcon = new ImageIcon(url);
                iconMap.put(name, loadedIcon);
                return loadedIcon;
            }
            else {
                return null;
            }
        }
    }
}
