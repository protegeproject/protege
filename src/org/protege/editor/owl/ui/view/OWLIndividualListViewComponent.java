package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 * <p/>
 * This definitely needs a rethink - it is a totally inefficient hack!
 */
public class OWLIndividualListViewComponent extends AbstractOWLIndividualViewComponent implements Findable<OWLIndividual>, Deleteable, CreateNewTarget {

    private OWLObjectList list;

    private OWLOntologyChangeListener listener;

    private ChangeListenerMediator changeListenerMediator;


    public void initialiseIndividualsView() throws Exception {
        list = new OWLObjectList(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.setFixedCellHeight(20);
        reset();
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
    }


    private void reset() {
        Set<OWLIndividual> inds = new HashSet<OWLIndividual>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            inds.addAll(ont.getReferencedIndividuals());
        }
        list.setListData(inds.toArray());
    }


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
        list.setSelectedValue(selelectedIndividual, true);
        return selelectedIndividual;
    }


    public void disposeView() {
        getOWLModelManager().removeOntologyChangeListener(listener);
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
        for (OWLOntologyChange change : changes) {
            for (OWLEntity ent : ((OWLAxiomChange) change).getEntities()) {
                if (ent instanceof OWLIndividual) {
                    reset();
                    break;
                }
            }
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
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(ind);
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
