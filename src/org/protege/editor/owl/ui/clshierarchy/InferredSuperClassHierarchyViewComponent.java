package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.cls.InferredSuperClassHierarchyProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredSuperClassHierarchyViewComponent extends AbstractSuperClassHierarchyViewComponent {

    private InferredSuperClassHierarchyProvider provider;

    private OWLModelManagerListener l = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType() == EventType.REASONER_CHANGED){
                getOWLClassHierarchyProvider().setReasoner(getOWLModelManager().getReasoner());
            }
        }
    };


    protected void performExtraInitialisation() throws Exception {
        super.performExtraInitialisation();
        getOWLModelManager().addListener(l);
    }


    public void disposeView() {
        super.disposeView();
        getOWLModelManager().removeListener(l);
    }


    protected InferredSuperClassHierarchyProvider getOWLClassHierarchyProvider() {
        if (provider == null) {
            provider = new InferredSuperClassHierarchyProvider(getOWLModelManager());
            provider.setReasoner(getOWLModelManager().getReasoner());
        }
        return provider;
    }
}
