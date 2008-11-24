package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.model.description.anonymouscls.AnonymousClassManager;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLEntityRemover;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
 * Date: Nov 24, 2008<br><br>
 */
public class AnonymousClassesView extends AbstractActiveOntologyViewComponent implements Deleteable {

    private MList list;

    private OWLEntityRemover remover;

    private java.util.List<ChangeListener> listeners = new ArrayList<ChangeListener>();


    protected void initialiseOntologyView() throws Exception {
        setLayout(new BorderLayout());

        list = new MList();
        final MList.MListCellRenderer ren = (MList.MListCellRenderer)list.getCellRenderer();
        ren.setContentRenderer(new OWLCellRenderer(getOWLEditorKit(), true, true){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof AnonymousClassItem){
                    value = ((AnonymousClassItem)value).getOWLClass();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        add(list, BorderLayout.CENTER);

        list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                for (ChangeListener l : listeners){
                    l.stateChanged(new ChangeEvent(AnonymousClassesView.this));
                }
                Object item = list.getSelectedValue();
                if (item != null){
                    getOWLEditorKit().getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(((AnonymousClassItem)item).getOWLClass());
                }
            }
        });


        remover = new OWLEntityRemover(getOWLModelManager().getOWLOntologyManager(),
                                       getOWLModelManager().getOntologies());
    }


    protected void disposeOntologyView() {
        // do nothing
    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        Set<AnonymousClassItem> clses = new HashSet<AnonymousClassItem>();
        AnonymousClassManager anonClassManager = getOWLModelManager().get(AnonymousClassManager.ID);
        if (anonClassManager != null){
            for (OWLClass cls : activeOntology.getReferencedClasses()){
                if (anonClassManager.isAnonymous(cls)){
                    clses.add(new AnonymousClassItem(cls));
                }
            }
        }
        list.setListData(clses.toArray());
    }


    public boolean canDelete() {
        return list.getSelectedIndex() >= 0;
    }


    public void handleDelete() {
        remover.reset();
        for (Object clsItem : list.getSelectedValues()){
            ((AnonymousClassItem)clsItem).getOWLClass().accept(remover);
        }
        getOWLModelManager().applyChanges(remover.getChanges());
    }


    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }


    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }


    private class AnonymousClassItem implements MListItem {

        private OWLClass cls;


        public AnonymousClassItem(OWLClass cls) {
            this.cls = cls;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
            // do nothing
        }


        public boolean isDeleteable() {
            return true;
        }


        public boolean handleDelete() {
            remover.reset();
            cls.accept(remover);
            getOWLModelManager().applyChanges(remover.getChanges());
            return true;
        }


        public String getTooltip() {
            return "";
        }


        public OWLClass getOWLClass() {
            return cls;
        }
    }
}
