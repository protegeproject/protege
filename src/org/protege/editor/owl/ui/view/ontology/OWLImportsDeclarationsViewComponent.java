package org.protege.editor.owl.ui.view.ontology;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationsViewComponent extends AbstractOWLViewComponent {

    private static final Logger logger = Logger.getLogger(OWLImportsDeclarationsViewComponent.class);

    private OWLFrameList2<OWLOntology> list;

    private OWLModelManagerListener listener;


    protected void initialiseOWLView() throws Exception {
// @@TODO v3 port        
//        list = new OWLFrameList2<OWLOntology>(getOWLEditorKit(), new OWLImportsDeclarationsFrame(getOWLEditorKit()));
//        setLayout(new BorderLayout());
//        add(new JScrollPane(list));
//        list.setRootObject(getOWLModelManager().getActiveOntology());
//        listener = new OWLModelManagerListener() {
//            public void handleChange(OWLModelManagerChangeEvent event) {
//                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
//                    list.setRootObject(getOWLModelManager().getActiveOntology());
//                }
//            }
//        };
//        getOWLModelManager().addListener(listener);
    }


    protected void disposeOWLView() {
// @@TODO v3 port
//        list.dispose();
//        getOWLModelManager().removeListener(listener);
    }
}
