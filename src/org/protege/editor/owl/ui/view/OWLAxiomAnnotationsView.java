package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelAdapter;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.frame.OWLAxiomAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
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
 * Date: Sep 2, 2008<br><br>
 */
public class OWLAxiomAnnotationsView extends AbstractOWLViewComponent {

    private OWLSelectionModelListener selListener = new OWLSelectionModelAdapter(){
        public void selectionChanged() throws Exception {
            final OWLSelectionModel selModel = getOWLWorkspace().getOWLSelectionModel();
            OWLObject lastSelection = selModel.getSelectedObject();
            
            if (lastSelection instanceof OWLAxiom || selModel.getLastSelectedAxiom() == null){
                updateViewContentAndHeader();
            }
        }
    };

    private HierarchyListener hierarchyListener = new HierarchyListener() {
        public void hierarchyChanged(HierarchyEvent e) {
            if (needsRefresh && isShowing()) {
                updateViewContentAndHeader();
            }
        }
    };

    private boolean needsRefresh = true;

    private boolean initialUpdatePerformed = false;

    private OWLAxiom lastDisplayedObject;

    private OWLFrameList2<OWLAxiom> axiomAnnotationComponent;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(6, 6));

        axiomAnnotationComponent = new OWLFrameList2<OWLAxiom>(getOWLEditorKit(),
                                                               new OWLAxiomAnnotationsFrame(getOWLEditorKit()));

        add(new JScrollPane(axiomAnnotationComponent), BorderLayout.CENTER);

        getOWLWorkspace().getOWLSelectionModel().addListener(selListener);
        addHierarchyListener(hierarchyListener);
    }

    
    protected void updateViewContentAndHeader() {
        if (!isShowing()) {
            needsRefresh = true;
            return;
        }
        needsRefresh = false;
        if (isPinned() && initialUpdatePerformed) {
            return;
        }
        initialUpdatePerformed = true;
        if (isSynchronizing()){
            lastDisplayedObject = updateView();
            updateHeader(lastDisplayedObject);
        }
    }


    private void updateHeader(OWLAxiom axiom) {
        String title = "";
        if (axiom != null){
            title = getOWLModelManager().getRendering(axiom);
        }
        getView().setHeaderText(title);
    }


    private OWLAxiom updateView() {
        OWLAxiom ax = getOWLWorkspace().getOWLSelectionModel().getLastSelectedAxiom();

        axiomAnnotationComponent.setRootObject(ax);

        return ax;
    }


    protected void disposeOWLView() {
        getOWLWorkspace().getOWLSelectionModel().removeListener(selListener);
        removeHierarchyListener(hierarchyListener);

        axiomAnnotationComponent.dispose();
    }
}
