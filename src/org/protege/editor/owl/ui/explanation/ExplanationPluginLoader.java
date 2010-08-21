package org.protege.editor.owl.ui.explanation;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;

public class ExplanationPluginLoader extends AbstractPluginLoader<ExplanationPlugin> {
	private OWLEditorKit editorKit;
	
	public ExplanationPluginLoader(OWLEditorKit editorKit) {
		super(ProtegeOWL.ID, ExplanationPlugin.ID);
		this.editorKit = editorKit;
	}

	protected ExplanationPlugin createInstance(IExtension extension) {
		return new ExplanationPlugin(editorKit, extension);
	}

}
