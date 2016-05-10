package org.protege.editor.core.ui.preferences;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractProtegePlugin;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesPanelPluginJPFImpl extends AbstractProtegePlugin<PreferencesPanel> implements PreferencesPanelPlugin {

    public static final String ID = "preferencespanel";

    public static final String LABEL_PARAM = "label";

    private EditorKit editorKit;


    public PreferencesPanelPluginJPFImpl(IExtension extension, EditorKit editorKit) {
        super(extension);
        this.editorKit = editorKit;
    }


    public String getLabel() {
        return getPluginProperty(LABEL_PARAM);
    }

    public PreferencesPanel newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                 InstantiationException {
        PreferencesPanel preferencesPanel = super.newInstance();
        preferencesPanel.setup(getLabel(), editorKit);
        return preferencesPanel;
    }
}
