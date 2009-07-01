package org.protege.editor.owl.ui.ontology.annotation;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.renderer.OWLAnnotationCellRenderer;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
 * Date: Jun 1, 2009<br><br>
 */
public class OWLOntologyAnnotationList extends MList {

    private OWLEditorKit eKit;

    private OWLAnnotationEditor editor;

    private OWLOntology ont;

    private MListSectionHeader header = new MListSectionHeader() {

        public String getName() {
            return "Annotations";
        }

        public boolean canAdd() {
            return true;
        }
    };

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            handleOntologyChanges(changes);
        }
    };

    private MouseListener mouseListener = new MouseAdapter(){
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                handleEdit();
            }
        }
    };

    private ListCellRenderer delegate;


    public OWLOntologyAnnotationList(OWLEditorKit eKit) {
        this.eKit = eKit;

        delegate = getCellRenderer();

        setCellRenderer(new OWLAnnotationCellRenderer(eKit){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof OntologyAnnotationItem){
                    value = ((OntologyAnnotationItem)value).getAnnotation();
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
                return delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        addMouseListener(mouseListener);

        eKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected void handleAdd() {
        // don't need to check the section as only the direct imports can be added
        if (editor == null){
            editor = new OWLAnnotationEditor(eKit);
        }
        UIHelper uiHelper = new UIHelper(eKit);
        int ret = uiHelper.showValidatingDialog("Ontology Annotation", editor.getEditorComponent(), null);

        if (ret == JOptionPane.OK_OPTION) {
            OWLAnnotation annot = editor.getEditedObject();
            eKit.getModelManager().applyChange(new AddOntologyAnnotation(ont, annot));
        }
    }


    public void setOntology(OWLOntology ont){
        this.ont = ont;

        java.util.List<Object> data = new ArrayList<Object>();

        data.add(header);

        // @@TODO ordering
        for (OWLAnnotation annot : ont.getAnnotations()){
            data.add(new OntologyAnnotationItem(ont, annot, eKit));
        }

        setListData(data.toArray());
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes){
            if (change instanceof AddOntologyAnnotation ||
                change instanceof RemoveOntologyAnnotation){
                if (change.getOntology().equals(ont)){
                    refresh();
                    return;
                }
            }
        }
    }


    private void refresh() {
        setOntology(ont);
    }


    public void dispose() {
        eKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
    }
}
