package org.protege.editor.owl.ui.view.ontology;

import org.protege.editor.owl.model.OntologyAnnotationContainer;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.ontology.annotation.OWLOntologyAnnotationList;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationViewComponent extends AbstractOWLViewComponent {

//    private static final Logger logger = Logger.getLogger(OWLOntologyAnnotationViewComponent.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1252038674995535772L;

    private OWLModelManagerListener listener;

    private OWLOntologyAnnotationList list;


    protected void initialiseOWLView() throws Exception {

        list = new OWLOntologyAnnotationList(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.setRootObject(new OntologyAnnotationContainer(getOWLModelManager().getActiveOntology()));
        listener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    updateView();
                }
            }
        };
        getOWLModelManager().addListener(listener);
    }


    private void updateView() {
        list.setRootObject(new OntologyAnnotationContainer(getOWLModelManager().getActiveOntology()));
    }


    protected void disposeOWLView() {
        list.dispose();
        getOWLModelManager().removeListener(listener);
    }
}
