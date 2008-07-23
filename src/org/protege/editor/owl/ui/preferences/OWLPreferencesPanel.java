package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.ui.preferences.PreferencesPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLPreferencesPanel extends PreferencesPanel {

    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getEditorKit();
    }


    public OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }
}
