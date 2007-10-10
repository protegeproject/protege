package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.model.hierarchy.AbstractSuperClassHierarchyProvider;
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


    protected AbstractSuperClassHierarchyProvider getOWLClassHierarchyProvider() {
        if (provider == null) {
            provider = new InferredSuperClassHierarchyProvider(getOWLModelManager());
            provider.setReasoner(getOWLModelManager().getReasoner());
        }
        return provider;
    }
}
