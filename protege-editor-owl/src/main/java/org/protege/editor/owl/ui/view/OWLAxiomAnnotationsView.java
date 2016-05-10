package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelAdapter;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.model.util.OWLAxiomInstance;
import org.protege.editor.owl.ui.axiom.AxiomAnnotationPanel;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 2, 2008<br><br>
 */
public class OWLAxiomAnnotationsView extends AbstractOWLViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -6288372193137254393L;

    private OWLSelectionModelListener selListener = new OWLSelectionModelAdapter(){
        public void selectionChanged() throws Exception {
            final OWLSelectionModel selModel = getOWLWorkspace().getOWLSelectionModel();
            if (selModel.getLastSelectedAxiomInstance() == null || 
                !selModel.getLastSelectedAxiomInstance().equals(axiomAnnotationPanel.getAxiom())){
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

    private AxiomAnnotationPanel axiomAnnotationPanel;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(6, 6));

        axiomAnnotationPanel = new AxiomAnnotationPanel(getOWLEditorKit());
        add(axiomAnnotationPanel, BorderLayout.CENTER);

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
            title = getOWLModelManager().getRendering(axiom).replace('\n', ' ');
            if (title.length() > 53){
                title = title.substring(0, 50) + "...";
            }
        }
        getView().setHeaderText(title);
    }


    private OWLAxiom updateView() {
        OWLAxiomInstance axiomInstance = getOWLWorkspace().getOWLSelectionModel().getLastSelectedAxiomInstance();

        axiomAnnotationPanel.setAxiomInstance(axiomInstance);

        return axiomInstance != null ? axiomInstance.getAxiom() : null;
    }


    protected void disposeOWLView() {
        getOWLWorkspace().getOWLSelectionModel().removeListener(selListener);
        removeHierarchyListener(hierarchyListener);

        axiomAnnotationPanel.dispose();
    }
}
