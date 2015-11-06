package org.protege.editor.owl.ui.inference;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;

public class InferencePreferencePluginLoader extends AbstractPluginLoader<InferencePreferencePlugin> {
    private OWLEditorKit editorKit;

    public InferencePreferencePluginLoader(OWLEditorKit editorKit) {
        super(ProtegeOWL.ID, InferencePreferencePlugin.ID);
        this.editorKit = editorKit;
    }

    @Override
    protected InferencePreferencePlugin createInstance(IExtension extension) {
        return new InferencePreferencePlugin(editorKit, extension);
    }

}
