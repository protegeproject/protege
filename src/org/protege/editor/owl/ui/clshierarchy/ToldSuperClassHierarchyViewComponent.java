package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.model.hierarchy.AbstractSuperClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.AssertedSuperClassHierarchyProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ToldSuperClassHierarchyViewComponent extends AbstractSuperClassHierarchyViewComponent {

    private AssertedSuperClassHierarchyProvider provider;


    protected AbstractSuperClassHierarchyProvider getOWLClassHierarchyProvider() {
        if (provider == null) {
//            try {
            provider = new AssertedSuperClassHierarchyProvider(getOWLModelManager());
//                provider.setOntologies(getOWLModelManager().getActiveOntologies());
            provider.setRoot(getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass());
//            } catch (OWLException e) {
//                e.printStackTrace();
//            }
        }
        return provider;
    }


    protected void performExtraInitialisation() throws Exception {
        // do nothing
    }
}
