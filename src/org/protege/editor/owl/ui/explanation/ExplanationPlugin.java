package org.protege.editor.owl.ui.explanation;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

public class ExplanationPlugin extends AbstractProtegePlugin<ExplanationService> {
	
	public static final String ID = "explanation";
	public static final String NAME = "name";
	
	private OWLEditorKit editorKit;

	public ExplanationPlugin(OWLEditorKit editorKit, IExtension extension) {
		super(extension);
		this.editorKit = editorKit;
	}
	
	public String getName() {
		return getPluginProperty(NAME);
	}

	@Override
	public ExplanationService newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		ExplanationService teacher = super.newInstance();
		teacher.setup(editorKit, getId(), getName());
		return teacher;
	}
}
