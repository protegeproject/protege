package org.protege.editor.owl.ui.explanation.io;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

public class InconsistentOntologyPlugin extends AbstractProtegePlugin<InconsistentOntologyPluginInstance> {
	public static final String ID = "inconsistentOntologyExplanation";
	
	private OWLEditorKit editorKit;
	
	public InconsistentOntologyPlugin(OWLEditorKit editorKit, IExtension extension) {
		super(extension);
		this.editorKit = editorKit;
	}
	
	@Override
	public InconsistentOntologyPluginInstance newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		InconsistentOntologyPluginInstance i = super.newInstance();
		i.setup(editorKit);
		return i;
	}
	
}
