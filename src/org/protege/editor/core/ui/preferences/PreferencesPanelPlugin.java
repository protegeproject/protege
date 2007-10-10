package org.protege.editor.core.ui.preferences;

import org.protege.editor.core.plugin.ProtegePlugin;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface PreferencesPanelPlugin extends ProtegePlugin<PreferencesPanel> {

    /**
     * Gets the label.  This label is typically used in user interfaces
     * @return A <code>String</code> that represents the label.
     */
    public String getLabel();
}
