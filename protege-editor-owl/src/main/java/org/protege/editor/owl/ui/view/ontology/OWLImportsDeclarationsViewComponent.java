package org.protege.editor.owl.ui.view.ontology;

import org.protege.editor.core.ui.workspace.TabbedWorkspace;
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

//    private static final Logger logger = LoggerFactory.getLogger(OWLImportsDeclarationsViewComponent.class);

    /**
     * 
     */
    private static final long serialVersionUID = -173934611566107945L;

    private OntologyImportsList list;

    private OWLModelManagerListener listener;
    
    private boolean read_only = false;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        if (((TabbedWorkspace) getWorkspace()).isReadOnly(this.getView().getPlugin())) {
        	read_only = true;
        }

        list = new OntologyImportsList(getOWLEditorKit(), read_only);
        list.setOntology(getOWLModelManager().getActiveOntology());
        listener = event -> {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ONTOLOGY_RELOADED)) {
                list.setOntology(getOWLModelManager().getActiveOntology());
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
