package org.protege.editor.owl.ui;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Oct-2007<br><br>
 */
public class OWLIndividualListComponent extends JPanel {

    private OWLEditorKit owlEditorKit;

    private OWLObjectList list;

    private OWLOntologyChangeListener listener;

    private ChangeListenerMediator changeListenerMediator;

    private Set<OWLIndividual> individualsInList;

    private OWLModelManagerListener modelManagerListener;


    public OWLIndividualListComponent(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        initialiseIndividualsView();
    }


    public OWLObjectList getList() {
        return list;
    }


    private OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }

    private OWLWorkspace getOWLWorkspace() {
        return getOWLEditorKit().getWorkspace();
    }

    private OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }


    public void initialiseIndividualsView() {
        list = new OWLObjectList(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (list.getSelectedValue() != null) {
//                        setSelectedEntity((OWLIndividual) list.getSelectedValue());
                    }
//                    changeListenerMediator.fireStateChanged(OWLIndividualListViewComponent.this);
                }
            }
        });

        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(java.util.List<? extends OWLOntologyChange> changes) {
                processChanges(changes);
            }
        };
        getOWLModelManager().addOntologyChangeListener(listener);
        changeListenerMediator = new ChangeListenerMediator();
        individualsInList = new TreeSet<OWLIndividual>(getOWLModelManager().getOWLObjectComparator());

        modelManagerListener = new OWLModelManagerListener() {

            public void handleChange(OWLModelManagerChangeEvent event) {
                if(event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    refill();
                }
            }
        };
        getOWLModelManager().addListener(modelManagerListener);

        refill();
        
    }


    private void refill() {
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
        OWLEntity entity = getSelectedIndividual();
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
        getOWLModelManager().removeListener(modelManagerListener);
    }


    public OWLNamedIndividual getSelectedIndividual() {
        return (OWLNamedIndividual) list.getSelectedValue();
    }


    public Set<OWLNamedIndividual> getSelectedIndividuals() {
        Set<OWLNamedIndividual> inds = new HashSet<OWLNamedIndividual>();
        for (Object obj : list.getSelectedValues()) {
            inds.add((OWLNamedIndividual) obj);
        }
        return inds;
    }


    private void processChanges(java.util.List<? extends OWLOntologyChange> changes) {
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
            if(ent instanceof OWLNamedIndividual) {
                if(individualsInList.remove(ent)) {
                    mod = true;
                }
            }
        }
        if(mod) {
            reset();
        }
    }


    private void addIndividual() {
        OWLEntityCreationSet<OWLNamedIndividual> set = getOWLWorkspace().createOWLIndividual();
        if (set == null) {
            return;
        }
        java.util.List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(set.getOntologyChanges());
        getOWLModelManager().applyChanges(changes);
        OWLNamedIndividual ind = set.getOWLEntity();
        if (ind != null) {
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(ind);
        }
    }


    public java.util.List<OWLIndividual> find(String match) {
        return new ArrayList<OWLIndividual>(getOWLModelManager().getEntityFinder().getMatchingOWLIndividuals(match));
    }


    public void show(OWLIndividual owlEntity) {
        list.setSelectedValue(owlEntity, true);
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
        for (OWLNamedIndividual ind : getSelectedIndividuals()) {
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
