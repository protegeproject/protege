package org.protege.editor.owl.ui.list;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.AnnotationContainer;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.renderer.OWLAnnotationCellRenderer2;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
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
 * Date: Jun 8, 2009<br><br>
 *
 * Don't want to write this again - Matthew is adding an interface that Axiom and OWLOntology can implement
 * that allows us to get the annotations.
 */
public abstract class AbstractAnnotationsList<O extends AnnotationContainer> extends MList {

    private static final long serialVersionUID = -2246627783362209148L;


    private static final String HEADER_TEXT = "Annotations";


    private OWLEditorKit editorKit;

    private OWLAnnotationEditor editor;

    private O root;

    private MListSectionHeader header = new MListSectionHeader() {

        public String getName() {
            return HEADER_TEXT;
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



    public AbstractAnnotationsList(OWLEditorKit eKit) {
        this.editorKit = eKit;
        delegate = getCellRenderer();
        setCellRenderer(new OWLAnnotationCellRenderer2(eKit));
        addMouseListener(mouseListener);
        eKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }


    protected abstract List<OWLOntologyChange> getAddChanges(OWLAnnotation annot);

    protected abstract List<OWLOntologyChange> getReplaceChanges(OWLAnnotation oldAnnotation, OWLAnnotation newAnnotation);

    protected abstract List<OWLOntologyChange> getDeleteChanges(OWLAnnotation annot);

    protected abstract void handleOntologyChanges(List<? extends OWLOntologyChange> changes);


    protected void handleAdd() {
        // don't need to check the section as only the direct imports can be added
        if (editor == null){
            editor = new OWLAnnotationEditor(editorKit);
        }

        editor.setEditedObject(null);

        UIHelper uiHelper = new UIHelper(editorKit);
        int ret = uiHelper.showValidatingDialog("Create Annotation", editor.getEditorComponent(), null);

        if (ret == JOptionPane.OK_OPTION) {
            OWLAnnotation annot = editor.getEditedObject();
            if (annot != null) {
            	editorKit.getModelManager().applyChanges(getAddChanges(annot));
            }
        }
    }


    public void setRootObject(O root){
        this.root = root;

        java.util.List<Object> data = new ArrayList<Object>();

        data.add(header);

        if (root != null){
            // @@TODO ordering
            for (OWLAnnotation annot : root.getAnnotations()){
                data.add(new AnnotationsListItem(annot));
            }
        }

        setListData(data.toArray());
        revalidate();
    }


    public O getRoot(){
        return root;
    }


    protected void refresh() {
        setRootObject(root);
    }


    protected void updateGlobalSelection(OWLObject owlObject){
        editorKit.getOWLWorkspace().getOWLSelectionModel().setSelectedObject(owlObject);
    }


    public void dispose() {
        editorKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
        if (editor != null) {
        	editor.dispose();
        	editor = null;
        }
    }


    public class AnnotationsListItem implements MListItem {

        private OWLAnnotation annot;

        public AnnotationsListItem(OWLAnnotation annot) {
            this.annot = annot;
        }


        public OWLAnnotation getAnnotation() {
            return annot;
        }


        public boolean isEditable() {
            return true;
        }


        public void handleEdit() {
            // don't need to check the section as only the direct imports can be added
            if (editor == null){
                editor = new OWLAnnotationEditor(editorKit);
            }
            editor.setEditedObject(annot);
            UIHelper uiHelper = new UIHelper(editorKit);
            int ret = uiHelper.showValidatingDialog("Ontology Annotation", editor.getEditorComponent(), null);

            if (ret == JOptionPane.OK_OPTION) {
                OWLAnnotation newAnnotation = editor.getEditedObject();
                if (newAnnotation != null && !newAnnotation.equals(annot)){
                    List<OWLOntologyChange> changes = getReplaceChanges(annot, newAnnotation);
                    editorKit.getModelManager().applyChanges(changes);
                }
            }
        }


        public boolean isDeleteable() {
            return true;
        }


        public boolean handleDelete() {
            List<OWLOntologyChange> changes = getDeleteChanges(annot);
            editorKit.getModelManager().applyChanges(changes);
            return true;
        }


        public String getTooltip() {
            return "";
        }
    }
}
