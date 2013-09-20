package org.protege.editor.owl.ui.explanation.io;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;

public class InconsistentOntologyPluginLoader extends AbstractPluginLoader<InconsistentOntologyPlugin> {
	private OWLEditorKit editorKit;
	
	public InconsistentOntologyPluginLoader(OWLEditorKit editorKit) {
		super(ProtegeOWL.ID, InconsistentOntologyPlugin.ID);
		this.editorKit = editorKit;
	}
	
	@Override
	protected InconsistentOntologyPlugin createInstance(IExtension extension) {
		return new InconsistentOntologyPlugin(editorKit, extension);
	}
}
