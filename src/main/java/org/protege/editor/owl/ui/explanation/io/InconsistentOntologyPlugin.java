package org.protege.editor.owl.ui.explanation.io;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

public class InconsistentOntologyPlugin extends AbstractProtegePlugin<InconsistentOntologyPluginInstance> {
	public static final String ID = "inconsistentOntologyExplanation";
	public static final String NAME_FIELD = "name";
	
	private OWLEditorKit editorKit;
	
	public InconsistentOntologyPlugin(OWLEditorKit editorKit, IExtension extension) {
		super(extension);
		this.editorKit = editorKit;
	}
	
	public String getName() {
		return getPluginProperty(NAME_FIELD);
	}
	
	@Override
	public InconsistentOntologyPluginInstance newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		InconsistentOntologyPluginInstance i = super.newInstance();
		i.setup(editorKit);
		return i;
	}
	
}
