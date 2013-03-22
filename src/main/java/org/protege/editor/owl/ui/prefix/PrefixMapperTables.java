package org.protege.editor.owl.ui.prefix;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.prefix.PrefixBasedRenderer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class PrefixMapperTables extends JPanel {
	private static final long serialVersionUID = -7430862544150495635L;
	
	private PrefixMapperTable table;
	private OWLOntology ontology;
	private OWLModelManager modelManager;
	
	private Set<SelectedOntologyListener> listeners = new HashSet<SelectedOntologyListener>();
	
	
	private TableModelListener editListener = new TableModelListener() {
		
		public void tableChanged(TableModelEvent e) {
			if (table != null && table.getModel().commitPrefixes()) {
				modelManager.setDirty(ontology);
				OWLModelManagerEntityRenderer renderer = modelManager.getOWLEntityRenderer();
				if (renderer instanceof PrefixBasedRenderer) {
					modelManager.refreshRenderer();
				}
			}
		}
	};

	public PrefixMapperTables(OWLModelManager modelManager) {
		this.modelManager = modelManager;
		setLayout(new BorderLayout());
		setOntology(modelManager.getActiveOntology());
	}
	
	public void refill() {
		table.getModel().refill();
	}

	public void setOntology(OWLOntology ontology)  {
		if (table != null) {
			table.getModel().removeTableModelListener(editListener);
		}
		this.ontology = ontology;
		PrefixOWLOntologyFormat prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(ontology);
		table = new PrefixMapperTable(prefixManager);
		table.getModel().addTableModelListener(editListener);
		removeAll();
		add(new JScrollPane(table));
		for (SelectedOntologyListener listener : listeners) {
			listener.selectedOntologyChanged();
		}
	}

	public void addListener(SelectedOntologyListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SelectedOntologyListener listener) {
		listeners.remove(listener);
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public PrefixMapperTable getPrefixMapperTable() {
		return table;
	}

	public interface SelectedOntologyListener {
		void selectedOntologyChanged();
	}

}
