package org.protege.editor.owl.model.hierarchy.property;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public class InferredObjectPropertyHierarchyProvider extends OWLObjectPropertyHierarchyProvider {

    private OWLModelManager mngr;

    public static final String ID = "inferredObjectPropertyHierarchyProvider";


    public InferredObjectPropertyHierarchyProvider(OWLModelManager mngr) {
        super(mngr.getOWLOntologyManager());
        this.mngr = mngr;
    }

    protected OWLReasoner getReasoner() {
        return mngr.getOWLReasonerManager().getCurrentReasoner();
    }

    public Set<OWLObjectProperty> getChildren(OWLObjectProperty objectProperty) {
        Set<OWLObjectProperty> subs = getReasoner().getSubObjectProperties(objectProperty, true).getFlattened();
        subs.remove(objectProperty);
        return subs;
    }


    public Set<OWLObjectProperty> getParents(OWLObjectProperty objectProperty) {
        Set<OWLObjectProperty> supers = getReasoner().getSuperObjectProperties(objectProperty, true).getFlattened();
        supers.remove(objectProperty);
        return supers;
    }


    public Set<OWLObjectProperty> getEquivalents(OWLObjectProperty objectProperty) {
        Set<OWLObjectProperty> equivs = getReasoner().getEquivalentObjectProperties(objectProperty).getEntities();
        equivs.remove(objectProperty);
        return equivs;
    }
}
