package org.protege.editor.owl.ui.inference;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.core.ui.preferences.PreferencesPanelPluginJPFImpl;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class InferencePreferencePlugin extends AbstractProtegePlugin<OWLPreferencesPanel> {
    public static final String ID = "inference_preferences";
    
    private OWLEditorKit editorKit;
    
    public InferencePreferencePlugin(OWLEditorKit editorKit, IExtension  extension) {
        super(extension);
        this.editorKit = editorKit;
    }
    
    public String getLabel() {
        return getPluginProperty(PreferencesPanelPluginJPFImpl.LABEL_PARAM);
    }
    
    @Override
    public OWLPreferencesPanel newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        OWLPreferencesPanel panel =  super.newInstance();
        panel.setup(getLabel(), editorKit);
        return panel;
    }

}
