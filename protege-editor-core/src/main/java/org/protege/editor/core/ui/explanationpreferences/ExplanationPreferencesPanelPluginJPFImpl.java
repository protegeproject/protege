package org.protege.editor.core.ui.explanationpreferences;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.core.ui.preferences.PreferencesPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanelPlugin;

public class ExplanationPreferencesPanelPluginJPFImpl extends AbstractProtegePlugin<PreferencesPanel>
		implements PreferencesPanelPlugin {

    public static final String ID = "explanationpreferencespanel";

    public static final String LABEL_PARAM = "label";

    private EditorKit editorKit;


    public ExplanationPreferencesPanelPluginJPFImpl(IExtension extension, EditorKit editorKit) {
        super(extension);
        this.editorKit = editorKit;
    }


    public String getLabel() {
        return getPluginProperty(LABEL_PARAM);
    }

    public PreferencesPanel newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                 InstantiationException {
    	PreferencesPanel panel = super.newInstance();
        panel.setup(getLabel(), editorKit);
        return panel;
    }
    
}
