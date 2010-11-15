package org.protege.editor.owl.ui.prefix;

import java.awt.CardLayout;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class PrefixMapperTables extends JPanel {
	private static final long serialVersionUID = -7430862544150495635L;
	private Map<OWLOntology, PrefixMapperTable> prefixTableMap = new HashMap<OWLOntology, PrefixMapperTable>();
	private CardLayout cards;
	private OWLOntology ontology;
	private Set<SelectedOntologyListener> listeners = new HashSet<SelectedOntologyListener>();


	public PrefixMapperTables(OWLModelManager modelManager) {
		cards = new CardLayout();
		setLayout(cards);
		for (OWLOntology ontology : modelManager.getOntologies()) {
			PrefixOWLOntologyFormat prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(ontology);
			PrefixMapperTable table = new PrefixMapperTable(prefixManager);
			prefixTableMap.put(ontology, table);
			add(new JScrollPane(table), ontology.getOntologyID().getOntologyIRI().toString());
		}
		setOntology(modelManager.getActiveOntology());
	}
	
	public void refill() {
		for (PrefixMapperTable table : prefixTableMap.values()) {
			table.getModel().refill();
		}
	}

	public CardLayout getCardLayout() {
		return cards;
	}

	public void setOntology(OWLOntology ontology)  {
		cards.show(this, ontology.getOntologyID().getOntologyIRI().toString());
		this.ontology = ontology;
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
		return prefixTableMap.get(ontology);
	}

	public PrefixMapperTable getPrefixMapperTable(OWLOntology ontology) {
		return prefixTableMap.get(ontology);
	}

	public Collection<PrefixMapperTable> getPrefixMapperTables() {
		return prefixTableMap.values();
	}

	public interface SelectedOntologyListener {
		void selectedOntologyChanged();
	}

}
