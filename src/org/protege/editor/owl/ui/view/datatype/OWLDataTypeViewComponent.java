package org.protege.editor.owl.ui.view.datatype;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.Findable;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 5, 2009<br><br>
 */
public class OWLDataTypeViewComponent extends AbstractOWLDataTypeViewComponent
        implements Findable<OWLDatatype> {

    private OWLObjectList<OWLDatatype> list;

    private ChangeListenerMediator changeListenerMediator;

    private ListSelectionListener selListener = new ListSelectionListener(){

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                if (list.getSelectedValue() != null) {
                    setGlobalSelection((OWLDatatype)list.getSelectedValue());
                }
                changeListenerMediator.fireStateChanged(OWLDataTypeViewComponent.this);
            }
        }
    };

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            handleChanges(changes);
        }
    };


    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());

        changeListenerMediator = new ChangeListenerMediator();

        list = new OWLObjectList<OWLDatatype>(getOWLEditorKit());
        list.addListSelectionListener(selListener);

        reload();

        setupActions();

        getOWLModelManager().addOntologyChangeListener(ontChangeListener);

        add(ComponentFactory.createScrollPane(list));
    }


    private void setupActions() {
        final DisposableAction addDatatypeAction = new DisposableAction("Add datatype", OWLIcons.getIcon("datarange.add.png")) {
            public void actionPerformed(ActionEvent event) {
                createNewDatatype();
            }

            public void dispose() {
                // do nothing
            }
        };

        final OWLSelectionViewAction deleteDatatypeAction = new OWLSelectionViewAction("Delete datatype", OWLIcons.getIcon("datarange.remove.png")) {

            public void actionPerformed(ActionEvent event) {
                deleteDatatype();
            }


            public void updateState() {
                // @@TODO should check if this is a built in datatype
                setEnabled(list.getSelectedIndex() != -1);
            }


            public void dispose() {
                // do nothing
            }
        };

        addAction(addDatatypeAction, "A", "A");
        addAction(deleteDatatypeAction, "B", "A");
    }


    private void deleteDatatype() {
        OWLEntityRemover remover = new OWLEntityRemover(getOWLModelManager().getOWLOntologyManager(),
                                                        getOWLModelManager().getOntologies());
        for (OWLDatatype datatype : list.getSelectedOWLObjects()) {
            datatype.accept(remover);
        }
        getOWLModelManager().applyChanges(remover.getChanges());
    }


    private void createNewDatatype() {
        OWLEntityCreationSet<OWLDatatype> set = getOWLWorkspace().createOWLDatatype();
        if (set == null) {
            return;
        }
        getOWLModelManager().applyChanges(set.getOntologyChanges());
        OWLDatatype datatype = set.getOWLEntity();
        if (datatype != null) {
            list.setSelectedValue(datatype, true);
        }
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes){
            if (change.isAxiomChange()){
                for (OWLEntity entity : change.getAxiom().getReferencedEntities()){
                    if (entity.isOWLDatatype()){
                        reload();
                        return;
                    }
                }
            }
        }
    }


    public void disposeView() {
        getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }


    protected OWLDatatype updateView(OWLDatatype dt) {
        if (dt != null){
            list.setSelectedValue(dt, true);
        }
        else{
            list.clearSelection();
        }
        return dt;
    }


    private void reload(){
        // Add all known datatypes including built in ones
        final OWLOntologyManager mngr = getOWLModelManager().getOWLOntologyManager();
        java.util.List<OWLDatatype> datatypeList = new ArrayList<OWLDatatype>(new OWLDataTypeUtils(mngr).getKnownDatatypes(getOWLModelManager().getActiveOntologies()));
        Collections.sort(datatypeList, getOWLModelManager().getOWLObjectComparator());

        list.setListData(datatypeList.toArray());
        final OWLDatatype sel = getOWLWorkspace().getOWLSelectionModel().getLastSelectedDatatype();
        if (datatypeList.contains(sel)){
            list.setSelectedValue(sel, true);
        }
    }


    public List<OWLDatatype> find(String match) {
        return new ArrayList<OWLDatatype>(getOWLModelManager().getEntityFinder().getMatchingOWLDatatypes(match));
    }


    public void show(OWLDatatype dt) {
        updateView(dt);
    }
}
