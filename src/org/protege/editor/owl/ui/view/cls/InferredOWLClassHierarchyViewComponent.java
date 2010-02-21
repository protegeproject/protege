package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredOWLClassHierarchyViewComponent extends AbstractOWLEntityHierarchyViewComponent<OWLClass> {

    protected void performExtraInitialisation() throws Exception {
        getTree().setBackground(OWLFrameList.INFERRED_BG_COLOR);
    }


    protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getInferredOWLClassHierarchyProvider();
    }

    public List<OWLClass> find(String match) {
        return new ArrayList<OWLClass>(getOWLModelManager().getOWLEntityFinder().getMatchingOWLClasses(match));
    }


    protected OWLObject updateView() {
        return updateView(getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
    }
}
