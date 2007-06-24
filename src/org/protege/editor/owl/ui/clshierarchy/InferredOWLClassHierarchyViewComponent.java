package org.protege.editor.owl.ui.clshierarchy;

import java.awt.Color;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent {

    protected void performExtraInitialisation() throws Exception {
        addAction(new AddSubClassAction(getOWLEditorKit(), getTree()), "A", "A");
        addAction(new AddSiblingClassAction(getOWLEditorKit(), getTree()), "A", "B");
        getTree().setBackground(new Color(255, 255, 215));
    }


    protected OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        return getOWLModelManager().getInferredOWLClassHierarchyProvider();
    }
}
