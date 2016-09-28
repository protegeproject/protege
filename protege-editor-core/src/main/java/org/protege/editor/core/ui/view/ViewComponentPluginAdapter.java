package org.protege.editor.core.ui.view;

import java.awt.*;
import java.util.Collections;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class ViewComponentPluginAdapter implements ViewComponentPlugin {

    public String getDefaultState() {
        return ViewComponentPlugin.VISIBLE;
    }


    public Color getBackgroundColor() {
        return Color.GRAY;
    }


    public boolean isUserCreatable() {
        return false;
    }


    public String getId() {
        return null;
    }


    public String getDocumentation() {
        return null;
    }


    public Set<String> getCategorisations() {
        return Collections.emptySet();
    }

    public Set<String> getNavigates() {
        return Collections.emptySet();
    }
    
    public boolean isEager() { return false; }

}
