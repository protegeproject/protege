package org.protege.editor.owl.ui.view.ontology;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.ontology.imports.OntologyImportsList;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationsViewComponent extends AbstractOWLViewComponent {

//    private static final Logger logger = Logger.getLogger(OWLImportsDeclarationsViewComponent.class);

    /**
     * 
     */
    private static final long serialVersionUID = -173934611566107945L;

    private OntologyImportsList list;

    private OWLModelManagerListener listener;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        list = new OntologyImportsList(getOWLEditorKit());
        list.setOntology(getOWLModelManager().getActiveOntology());
        listener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    list.setOntology(getOWLModelManager().getActiveOntology());
                }
            }
        };
        getOWLModelManager().addListener(listener);

        add(new JScrollPane(list));
    }


    protected void disposeOWLView() {
        list.dispose();
        getOWLModelManager().removeListener(listener);
    }
}
