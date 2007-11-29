package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.ui.OWLEntityComparator;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.OWLEntitySetProvider;
import org.semanticweb.owl.util.OWLEntityCollector;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 * <p/>
 * This definitely needs a rethink - it is a totally inefficient hack!
 */
public class OWLIndividualListViewComponent extends AbstractOWLIndividualViewComponent implements Findable<OWLIndividual>, Deleteable, CreateNewTarget, RefreshableComponent {

    private OWLObjectList list;

    private OWLOntologyChangeListener listener;

    private ChangeListenerMediator changeListenerMediator;

    private Set<OWLIndividual> individualsInList;

    private OWLModelManagerListener modelManagerListener;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLObjectList(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.setFixedCellHeight(20);
        list.setFixedCellWidth(300);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (list.getSelectedValue() != null) {
                        setSelectedEntity((OWLIndividual) list.getSelectedValue());
                    }
                    changeListenerMediator.fireStateChanged(OWLIndividualListViewComponent.this);
                }
            }
        });
        list.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setSelectedEntity((OWLIndividual) list.getSelectedValue());
            }
        });
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                processChanges(changes);
            }
        };
        getOWLModelManager().addOntologyChangeListener(listener);
        addAction(new AddIndividualAction(), "A", "A");
        addAction(new DeleteIndividualAction(getOWLEditorKit(), new OWLEntitySetProvider<OWLIndividual>() {
            public Set<OWLIndividual> getEntities() {
                return getSelectedIndividuals();
            }
        }), "B", "A");
        changeListenerMediator = new ChangeListenerMediator();
        individualsInList = new TreeSet<OWLIndividual>(new OWLEntityComparator<OWLIndividual>(getOWLModelManager()));
        refill();

        modelManagerListener = new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
                if(event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    refill();
                }
            }
        };
        getOWLModelManager().addListener(modelManagerListener);
    }


    public void refreshComponent() {
        refill();
    }


    private void refill() {
        // Initial fill
        individualsInList.clear();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            individualsInList.addAll(ont.getReferencedIndividuals());
        }
        reset();
    }


    public void setSelectedIndividual(OWLIndividual individual) {
        list.setSelectedValue(individual, true);
    }


    private void reset() {

        list.setListData(individualsInList.toArray());
        OWLEntity entity = getSelectedOWLIndividual();
        if (entity instanceof OWLIndividual) {
            list.setSelectedValue(entity, true);
        }
    }


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
        if (!isPinned()) {
            list.setSelectedValue(selelectedIndividual, true);
        }
        return (OWLIndividual) list.getSelectedValue();
    }


    public void disposeView() {
        getOWLModelManager().removeOntologyChangeListener(listener);
        getOWLModelManager().removeListener(modelManagerListener);
    }


    public OWLIndividual getSelectedIndividual() {
        return (OWLIndividual) list.getSelectedValue();
    }


    public Set<OWLIndividual> getSelectedIndividuals() {
        Set<OWLIndividual> inds = new HashSet<OWLIndividual>();
        for (Object obj : list.getSelectedValues()) {
            inds.add((OWLIndividual) obj);
        }
        return inds;
    }


    private void processChanges(List<? extends OWLOntologyChange> changes) {
        OWLEntityCollector addedCollector = new OWLEntityCollector();
        OWLEntityCollector removedCollector = new OWLEntityCollector();

        for(OWLOntologyChange chg : changes) {
            if(chg.isAxiomChange()) {
                OWLAxiomChange axChg = (OWLAxiomChange) chg;
                if(axChg instanceof AddAxiom) {
                    axChg.getAxiom().accept(addedCollector);
                }
                else {
                    axChg.getAxiom().accept(removedCollector);
                }
            }
        }
        boolean mod = false;
        for(OWLEntity ent : addedCollector.getObjects()) {
            if(ent instanceof OWLIndividual) {
                if(individualsInList.add((OWLIndividual) ent)) {
                    mod = true;
                }
            }
        }
        for(OWLEntity ent : removedCollector.getObjects()) {
            if(ent instanceof OWLIndividual) {
                if(individualsInList.remove((OWLIndividual) ent)) {
                    mod = true;
                }
            }
        }
        if(mod) {
            reset();
        }
    }


    private void addIndividual() {
        OWLEntityCreationSet<OWLIndividual> set = getOWLWorkspace().createOWLIndividual();
        if (set == null) {
            return;
        }
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(set.getOntologyChanges());
        getOWLModelManager().applyChanges(changes);
        OWLIndividual ind = set.getOWLEntity();
        if (ind != null) {
            list.setSelectedValue(ind,  true);
            if (!isPinned()) {
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(ind);
            }
        }
    }


    public List<OWLIndividual> find(String match) {
        return getOWLModelManager().getMatchingOWLIndividuals(match);
    }


    public void show(OWLIndividual owlEntity) {
        list.setSelectedValue(owlEntity, true);
    }


    private class AddIndividualAction extends DisposableAction {

        public AddIndividualAction() {
            super("Add individual", OWLIcons.getIcon("individual.add.png"));
        }


        public void actionPerformed(ActionEvent e) {
            addIndividual();
        }


        public void dispose() {
        }
    }


    public void addChangeListener(ChangeListener listener) {
        changeListenerMediator.addChangeListener(listener);
    }


    public void removeChangeListener(ChangeListener listener) {
        changeListenerMediator.removeChangeListener(listener);
    }


    public void handleDelete() {
        OWLEntityRemover entityRemover = new OWLEntityRemover(getOWLModelManager().getOWLOntologyManager(),
                                                              getOWLModelManager().getOntologies());
        for (OWLIndividual ind : getSelectedIndividuals()) {
            ind.accept(entityRemover);
        }
        getOWLModelManager().applyChanges(entityRemover.getChanges());
    }


    public boolean canDelete() {
        return !getSelectedIndividuals().isEmpty();
    }


    public boolean canCreateNew() {
        return true;
    }


    public void createNewObject() {
        addIndividual();
    }
}
