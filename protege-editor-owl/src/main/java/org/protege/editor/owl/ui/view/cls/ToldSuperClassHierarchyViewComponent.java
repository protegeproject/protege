package org.protege.editor.owl.ui.view.cls;

import java.util.Optional;

import org.protege.editor.owl.model.hierarchy.AbstractSuperClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.AssertedSuperClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.cls.InferredSuperClassHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ToldSuperClassHierarchyViewComponent extends AbstractSuperClassHierarchyViewComponent {

    private AssertedSuperClassHierarchyProvider provider;

    private InferredSuperClassHierarchyProvider inferredProvider;

    protected AbstractSuperClassHierarchyProvider getOWLClassHierarchyProvider() {
        if (provider == null) {
            provider = new AssertedSuperClassHierarchyProvider(getOWLModelManager());
            provider.setRoot(getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
        }
        return provider;
    }


    protected void performExtraInitialisation() throws Exception {
        // do nothing
    }

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLClass>> getInferredHierarchyProvider() {
        if(inferredProvider == null) {
            inferredProvider = new InferredSuperClassHierarchyProvider(getOWLModelManager());
        }
        return Optional.of(inferredProvider);
    }
}
