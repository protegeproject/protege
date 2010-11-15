package org.protege.editor.owl.ui.prefix;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.PrefixBasedRenderer;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

public class PrefixMapperView extends AbstractActiveOntologyViewComponent {
	private static final long serialVersionUID = 787248046135787437L;
	private PrefixMapperTables tables;
	private PrefixMapperTable currentTable;
	private TableModelListener editListener = new TableModelListener() {
		
		public void tableChanged(TableModelEvent e) {
			if (currentTable != null && currentTable.getModel().commitPrefixes()) {
				OWLModelManager modelManager = getOWLModelManager();
				OWLOntology activeOntology = tables.getOntology();
				modelManager.setDirty(activeOntology);
				OWLModelManagerEntityRenderer renderer = modelManager.getOWLEntityRenderer();
				if (renderer instanceof PrefixBasedRenderer) {
					modelManager.setOWLEntityRenderer(renderer);
				}
			}
		}
	};
	
	private OWLModelManagerListener entitiesChangedListener = new OWLModelManagerListener() {
		
		public void handleChange(OWLModelManagerChangeEvent event) {
			// Hacky...
			// I am really trying to detect the case where the user changed the prefixes from 
			// within the preferences pane.  At that point we change the renderer to ensure
			// that the new prefixes are seen.
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
		panel.add(new AddPrefixMappingAction(tables));
		panel.add(new GeneratePrefixFromOntologyAction(getOWLEditorKit(), tables));
		panel.add(new RemovePrefixMappingAction(tables));
		return panel;
	}

	@Override
	protected void disposeOntologyView() {
		if (currentTable != null) {
			currentTable.getModel().removeTableModelListener(editListener);
			getOWLModelManager().removeListener(entitiesChangedListener);
		}
	}

	@Override
	protected void updateView(OWLOntology activeOntology) throws Exception {
		if (currentTable != null) {
			currentTable.getModel().removeTableModelListener(editListener);
		}
		tables.setOntology(activeOntology);
		currentTable = tables.getPrefixMapperTable();
		if (currentTable != null) {
			currentTable.getModel().addTableModelListener(editListener);
		}
	}
	


}
