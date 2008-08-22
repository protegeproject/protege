package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.OWLEntityComparator;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityCollector;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.OWLEntitySetProvider;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br>
 * <br>
 * <p/> This definitely needs a rethink - it is a totally inefficient hack!
 */
public class OWLIndividualListViewComponent extends
		AbstractOWLIndividualViewComponent implements Findable<OWLIndividual>,
		Deleteable, CreateNewTarget, RefreshableComponent {
	private OWLObjectList<OWLIndividual> list;
	private OWLOntologyChangeListener listener;
	private ChangeListenerMediator changeListenerMediator;
	private Set<OWLIndividual> individualsInList;
	private OWLModelManagerListener modelManagerListener;
	private int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

	@Override
	public void initialiseIndividualsView() throws Exception {
		this.list = new OWLObjectList<OWLIndividual>(this.getOWLEditorKit());
		this.list.setSelectionMode(this.selectionMode);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(this.list));
		this.list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (OWLIndividualListViewComponent.this.list
							.getSelectedValue() != null) {
						OWLIndividualListViewComponent.this
								.setSelectedEntity((OWLIndividual) OWLIndividualListViewComponent.this.list
										.getSelectedValue());
					}
					OWLIndividualListViewComponent.this.changeListenerMediator
							.fireStateChanged(OWLIndividualListViewComponent.this);
				}
			}
		});
		this.list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				OWLIndividualListViewComponent.this
						.setSelectedEntity((OWLIndividual) OWLIndividualListViewComponent.this.list
								.getSelectedValue());
			}
		});
		this.listener = new OWLOntologyChangeListener() {
			public void ontologiesChanged(
					List<? extends OWLOntologyChange> changes) {
				OWLIndividualListViewComponent.this.processChanges(changes);
			}
		};
		this.getOWLModelManager().addOntologyChangeListener(this.listener);

        setupActions();
		this.changeListenerMediator = new ChangeListenerMediator();
		this.individualsInList = new TreeSet<OWLIndividual>(
				new OWLEntityComparator<OWLIndividual>(this
						.getOWLModelManager()));
		this.refill();
		this.modelManagerListener = new OWLModelManagerListener() {
			public void handleChange(OWLModelManagerChangeEvent event) {
				if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
					OWLIndividualListViewComponent.this.refill();
				}
			}
		};
		this.getOWLModelManager().addListener(this.modelManagerListener);
	}


    protected void setupActions() {
        this.addAction(new AddIndividualAction(), "A", "A");
		this.addAction(new DeleteIndividualAction(this.getOWLEditorKit(),
				new OWLEntitySetProvider<OWLIndividual>() {
					public Set<OWLIndividual> getEntities() {
						return OWLIndividualListViewComponent.this
								.getSelectedIndividuals();
					}
				}), "B", "A");
    }


    @Override
	public void refreshComponent() {
		this.refill();
	}

	private void refill() {
		// Initial fill
		this.individualsInList.clear();
		for (OWLOntology ont : getOntologies()) {
			this.individualsInList.addAll(ont.getReferencedIndividuals());
		}
		this.reset();
	}

    protected Set<OWLOntology> getOntologies() {
        return getOWLModelManager().getActiveOntologies();
    }

    public void setSelectedIndividual(OWLIndividual individual) {
		this.list.setSelectedValue(individual, true);
	}

	private void reset() {
		this.list.setListData(this.individualsInList.toArray());
		OWLEntity entity = this.getSelectedOWLIndividual();
		if (entity instanceof OWLIndividual) {
			this.list.setSelectedValue(entity, true);
		}
	}

	@Override
	protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
		if (!this.isPinned()) {
			this.list.setSelectedValue(selelectedIndividual, true);
		}
		return (OWLIndividual) this.list.getSelectedValue();
	}

	@Override
	public void disposeView() {
		this.getOWLModelManager().removeOntologyChangeListener(this.listener);
		this.getOWLModelManager().removeListener(this.modelManagerListener);
	}

	public OWLIndividual getSelectedIndividual() {
		return (OWLIndividual) this.list.getSelectedValue();
	}

	public Set<OWLIndividual> getSelectedIndividuals() {
		Set<OWLIndividual> inds = new HashSet<OWLIndividual>();
		for (Object obj : this.list.getSelectedValues()) {
			inds.add((OWLIndividual) obj);
		}
		return inds;
	}

	private void processChanges(List<? extends OWLOntologyChange> changes) {
		OWLEntityCollector addedCollector = new OWLEntityCollector();
		OWLEntityCollector removedCollector = new OWLEntityCollector();
		for (OWLOntologyChange chg : changes) {
			if (chg.isAxiomChange()) {
				OWLAxiomChange axChg = (OWLAxiomChange) chg;
				if (axChg instanceof AddAxiom) {
					axChg.getAxiom().accept(addedCollector);
				} else {
					axChg.getAxiom().accept(removedCollector);
				}
			}
		}
		boolean mod = false;
		for (OWLEntity ent : addedCollector.getObjects()) {
			if (ent instanceof OWLIndividual) {
				if (this.individualsInList.add((OWLIndividual) ent)) {
					mod = true;
				}
			}
		}
		for (OWLEntity ent : removedCollector.getObjects()) {
			if (ent instanceof OWLIndividual) {
				boolean stillReferenced = false;
				for (OWLOntology ont : getOntologies()) {
					if (ont.containsIndividualReference(ent.getURI())) {
						stillReferenced = true;
						break;
					}
				}
				if (!stillReferenced) {
					if (this.individualsInList.remove(ent)) {
						mod = true;
					}
				}
			}
		}
		if (mod) {
			this.reset();
		}
	}

	private void addIndividual() {
		OWLEntityCreationSet<OWLIndividual> set = this.getOWLWorkspace().createOWLIndividual();
		if (set == null) {
			return;
		}
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		changes.addAll(set.getOntologyChanges());
		this.getOWLModelManager().applyChanges(changes);
		OWLIndividual ind = set.getOWLEntity();
		if (ind != null) {
			this.list.setSelectedValue(ind, true);
			if (!this.isPinned()) {
				this.getOWLWorkspace().getOWLSelectionModel()
						.setSelectedEntity(ind);
			}
		}
	}

	public List<OWLIndividual> find(String match) {
        return new ArrayList<OWLIndividual>(getOWLModelManager().getEntityFinder().getMatchingOWLIndividuals(match));
	}

	public void show(OWLIndividual owlEntity) {
		list.setSelectedValue(owlEntity, true);
	}


    public void setSelectedIndividuals(Set<OWLIndividual> individuals) {
        list.setSelectedValues(individuals, true);
    }


    private class AddIndividualAction extends DisposableAction {
		public AddIndividualAction() {
			super("Add individual", OWLIcons.getIcon("individual.add.png"));
		}

		public void actionPerformed(ActionEvent e) {
			OWLIndividualListViewComponent.this.addIndividual();
		}

		@Override
		public void dispose() {
		}
	}

	public void addChangeListener(ChangeListener listener) {
		this.changeListenerMediator.addChangeListener(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		this.changeListenerMediator.removeChangeListener(listener);
	}

	public void handleDelete() {
		OWLEntityRemover entityRemover = new OWLEntityRemover(this
				.getOWLModelManager().getOWLOntologyManager(), this
				.getOWLModelManager().getOntologies());
		for (OWLIndividual ind : this.getSelectedIndividuals()) {
			ind.accept(entityRemover);
		}
		this.getOWLModelManager().applyChanges(entityRemover.getChanges());
	}

	public boolean canDelete() {
		return !this.getSelectedIndividuals().isEmpty();
	}

	public boolean canCreateNew() {
		return true;
	}

	public void createNewObject() {
		this.addIndividual();
	}

	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
		if (this.list != null) {
			this.list.setSelectionMode(selectionMode);
		}
	}
}
