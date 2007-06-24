package org.protege.editor.core.ui.util;



import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.osgi.framework.Bundle;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.PluginUtilities;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 23, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class Icons {


    public static Icon getIcon(String name) {
        try {
            Bundle b = PluginUtilities.getInstance().getApplicationBundle();
            URL url = b.getResource(name);
            if (url != null) {
                return new ImageIcon(url);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}
