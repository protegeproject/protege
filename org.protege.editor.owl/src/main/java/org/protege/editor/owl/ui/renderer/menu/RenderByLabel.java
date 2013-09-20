package org.protege.editor.owl.ui.renderer.menu;

import java.util.Collections;
import java.util.List;

import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

public class RenderByLabel extends AbstractByRendererMenu {
	private static final long serialVersionUID = 7364664042805363135L;
	
	private static final OWLAnnotationProperty LABEL = OWLManager.getOWLDataFactory().getRDFSLabel();
	
	protected boolean isMyRendererPlugin(RendererPlugin plugin) {
		return plugin.getRendererClassName().equals(OWLEntityAnnotationValueRenderer.class.getName());
	}
	
	protected boolean isConfigured(RendererPlugin plugin) {
		List<IRI> annotations = OWLRendererPreferences.getInstance().getAnnotationIRIs();
		return (annotations.size() == 1 && annotations.contains(LABEL.getIRI()));
	}

	@Override
	protected void configure(RendererPlugin plugin) {
		OWLRendererPreferences.getInstance().setAnnotations(Collections.singletonList(LABEL.getIRI()));
	}

}
