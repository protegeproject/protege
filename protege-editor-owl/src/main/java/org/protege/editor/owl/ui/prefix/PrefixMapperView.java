package org.protege.editor.owl.ui.prefix;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;

public class PrefixMapperView extends AbstractActiveOntologyViewComponent {
	private static final long serialVersionUID = 787248046135787437L;
	private PrefixMapperTables tables;
	private PrefixMapperTable currentTable;

	
	private OWLModelManagerListener entitiesChangedListener = new OWLModelManagerListener() {
		
		public void handleChange(OWLModelManagerChangeEvent event) {
			// Hacky...
			// I am really trying to detect the case where the user changed the prefixes from 
			// within the preferences pane.  At that point we change the renderer to ensure
			// that the new prefixes are seen.
			//                                            I don't like this.
			if (event.getType() == EventType.ENTITY_RENDERER_CHANGED) {
				tables.refill();
			}
		}
	};

	@Override
	protected void initialiseOntologyView() throws Exception {
		setLayout(new BorderLayout());
		tables = new PrefixMapperTables(getOWLModelManager());
		add(createButtons(), BorderLayout.NORTH);
        add(tables, BorderLayout.CENTER);
		updateView(getOWLModelManager().getActiveOntology());
		getOWLModelManager().addListener(entitiesChangedListener);
	}
	
	private JToolBar createButtons() {
		JToolBar panel = new JToolBar();
		panel.setFloatable(false);
		panel.setBorderPainted(false);
		panel.add(new AddPrefixMappingAction(tables));
		panel.add(new GeneratePrefixFromOntologyAction(getOWLEditorKit(), tables));
		panel.add(new RemovePrefixMappingAction(tables));
		return panel;
	}

	@Override
	protected void disposeOntologyView() {
		if (currentTable != null) {
			getOWLModelManager().removeListener(entitiesChangedListener);
		}
	}

	@Override
	protected void updateView(OWLOntology activeOntology) throws Exception {
		tables.setOntology(activeOntology);
		currentTable = tables.getPrefixMapperTable();
	}
	


}
