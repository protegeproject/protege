package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLEntityComparator;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
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

    private Set<OWLIndividual> individualsInList;

    private ChangeProcessor changeProcessor;


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
        changeProcessor = new ChangeProcessor();
        changeListenerMediator = new ChangeListenerMediator();
        individualsInList = new TreeSet<OWLIndividual>(new OWLEntityComparator(getOWLModelManager()));
        // Initial fill
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
        changeProcessor.reset();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                changeProcessor.setAdd(change instanceof AddAxiom);
                change.getAxiom().accept(changeProcessor);
            }
        }
        if (changeProcessor.isModified()) {
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


    private class ChangeProcessor extends OWLAxiomVisitorAdapter {

        private boolean add;

        private boolean modified;


        public void setAdd(boolean add) {
            this.add = add;
        }


        public void reset() {
            modified = false;
        }


        private void checkIndividual(OWLIndividual ind) {
            if (add) {
                if (individualsInList.add(ind)) {
                    modified = true;
                }
            }
            else {
                if (individualsInList.remove(ind)) {
                    modified = true;
                }
            }
        }


        public boolean isModified() {
            return modified;
        }


        public void visit(OWLClassAssertionAxiom owlClassAssertionAxiom) {
            checkIndividual(owlClassAssertionAxiom.getIndividual());
        }


        public void visit(OWLDeclarationAxiom owlDeclarationAxiom) {
            if (owlDeclarationAxiom.getEntity() instanceof OWLIndividual) {
                checkIndividual((OWLIndividual) owlDeclarationAxiom.getEntity());
            }
        }
    }


    ;
}
