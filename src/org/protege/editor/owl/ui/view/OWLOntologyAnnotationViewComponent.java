package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.frame.OWLOntologyAnnotationFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationViewComponent extends AbstractOWLViewComponent {

    private static final Logger logger = Logger.getLogger(OWLOntologyAnnotationViewComponent.class);


    private OWLModelManagerListener listener;

    private OWLFrameList2<OWLOntology> list;


    protected void initialiseOWLView() throws Exception {

        list = new OWLFrameList2<OWLOntology>(getOWLEditorKit(), new OWLOntologyAnnotationFrame(getOWLEditorKit()));
        setLayout(new BorderLayout());
        add(new JScrollPane(list));
        list.setRootObject(getOWLModelManager().getActiveOntology());
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
        list.setRootObject(getOWLModelManager().getActiveOntology());
    }


    protected void disposeOWLView() {
        list.dispose();
        getOWLModelManager().removeListener(listener);
    }
}
