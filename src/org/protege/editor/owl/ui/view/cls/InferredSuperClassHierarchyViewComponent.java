package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.cls.InferredSuperClassHierarchyProvider;
import org.protege.editor.owl.ui.framelist.OWLFrameList;


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

    /**
     * 
     */
    private static final long serialVersionUID = 1125962405110757405L;

    private InferredSuperClassHierarchyProvider provider;

    private OWLModelManagerListener l = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType() == EventType.REASONER_CHANGED){
                getOWLClassHierarchyProvider().setReasoner(getOWLModelManager().getReasoner());
            }
        }
    };


    protected void performExtraInitialisation() throws Exception {
        getOWLModelManager().addListener(l);
        getTree().setBackground(OWLFrameList.INFERRED_BG_COLOR);
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
