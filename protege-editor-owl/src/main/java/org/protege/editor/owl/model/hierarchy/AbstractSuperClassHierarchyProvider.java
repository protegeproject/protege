package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A "reverse hierarchy" of superclass relationships.
 * Children are superclasses of their parents.
 */
public abstract class AbstractSuperClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {
	
    private volatile OWLClass rootClass;


    public AbstractSuperClassHierarchyProvider(OWLOntologyManager manager) {
        super(manager);
    }


    public void setRoot(OWLClass cls) {
        rootClass = cls;
        fireHierarchyChanged();
    }


    public Set<OWLClass> getRoots() {
    	OWLClass myRoot = rootClass;
        if (myRoot == null) {
            return Collections.emptySet();
        }
        else {
            return Collections.singleton(myRoot);
        }
    }
}
