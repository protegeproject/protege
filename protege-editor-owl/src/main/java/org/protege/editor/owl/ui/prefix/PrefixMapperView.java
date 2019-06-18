package org.protege.editor.owl.ui.prefix;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PrefixMapperView extends AbstractActiveOntologyViewComponent {

	private final PrefixList prefixList = new PrefixList();
	
	private OWLModelManagerListener entitiesChangedListener = event -> {
		// Hacky...
		// I am really trying to detect the case where the user changed the prefixes from
		// within the preferences pane.  At that point we change the renderer to ensure
		// that the new prefixes are seen.
		//                                            I don't like this.
		if (event.getType() == EventType.ENTITY_RENDERER_CHANGED) {
			updateView(getOWLModelManager().getActiveOntology());
		}
	};

	@Override
	protected void initialiseOntologyView() {
		setLayout(new BorderLayout());
        add(new JScrollPane(prefixList), BorderLayout.CENTER);
		updateView(getOWLModelManager().getActiveOntology());
		getOWLModelManager().addListener(entitiesChangedListener);
	}

	@Override
	protected void disposeOntologyView() {
		getOWLModelManager().removeListener(entitiesChangedListener);
	}

	@Override
	protected void updateView(OWLOntology activeOntology) {
		PrefixDocumentFormat prefixDocumentFormat = PrefixUtilities.getPrefixOWLOntologyFormat(activeOntology);
		List<PrefixMapping> list = prefixDocumentFormat.getPrefixNames().stream().map(pn -> {
			String p = prefixDocumentFormat.getPrefix(pn);
			return PrefixMapping.get(pn, p);
		}).collect(Collectors.toList());
		prefixList.setPrefixMappings(list);
	}
	


}
