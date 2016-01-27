package org.protege.editor.owl.ui.view.objectproperty;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 16, 2008<br><br>
 */
public class InferredObjectPropertyHierarchyViewComponent extends OWLObjectPropertyHierarchyViewComponent {

    private OWLModelManagerListener l = event -> {
        if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED ||
            event.getType() == EventType.ONTOLOGY_CLASSIFIED ||
            event.getType() == EventType.REASONER_CHANGED ||
            event.getType() == EventType.ONTOLOGY_RELOADED){
            getHierarchyProvider().setOntologies(getOWLModelManager().getActiveOntologies());
        }
    };


    protected void performExtraInitialisation() throws Exception {
        getOWLModelManager().addListener(l);
        getTree().setBackground(OWLFrameList.INFERRED_BG_COLOR);
    }


    public void disposeView() {
        getOWLModelManager().removeListener(l);
        super.disposeView();
    }


    protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getInferredOWLObjectPropertyHierarchyProvider();
    }
}
