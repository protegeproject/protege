package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.model.hierarchy.AbstractSuperClassHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractSuperClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent {


    protected OWLClass updateView(OWLClass selectedClass) {
        getOWLClassHierarchyProvider().setRoot(selectedClass);
        OWLClass cls = super.updateView(selectedClass);
        // Expand
//        getTree().expandAll();
        getTree().expandRow(0);
        return cls;
    }


    protected final OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        return getOWLClassHierarchyProvider();
    }


    protected abstract AbstractSuperClassHierarchyProvider getOWLClassHierarchyProvider();
}
