package org.protege.editor.owl.model.hierarchy.property;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.HashSet;
import java.util.Set;

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

    public Set<OWLObjectProperty> getUnfilteredChildren(OWLObjectProperty objectProperty) {
        Set<OWLObjectPropertyExpression> subs = getReasoner().getSubObjectProperties(objectProperty, true).getFlattened();
        subs.remove(objectProperty);
        subs.remove(mngr.getOWLDataFactory().getOWLBottomObjectProperty());
        Set<OWLObjectProperty> children = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyExpression p : subs) {
            if (p instanceof OWLObjectProperty) {
                children.add((OWLObjectProperty) p);
            }
        }
        return children;
    }


    public Set<OWLObjectProperty> getParents(OWLObjectProperty objectProperty) {
        Set<OWLObjectPropertyExpression> supers = getReasoner().getSuperObjectProperties(objectProperty, true).getFlattened();
        supers.remove(objectProperty);
        Set<OWLObjectProperty> parents = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyExpression p : supers) {
            if (p instanceof OWLObjectProperty) {
                parents.add((OWLObjectProperty) p);
            }
        }
        return parents;
    }


    public Set<OWLObjectProperty> getEquivalents(OWLObjectProperty objectProperty) {
        Set<OWLObjectPropertyExpression> equivs = getReasoner().getEquivalentObjectProperties(objectProperty).getEntities();
        equivs.remove(objectProperty);
        Set<OWLObjectProperty> ret = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyExpression p : equivs) {
            if (p instanceof OWLObjectProperty) {
                ret.add((OWLObjectProperty) p);
            }
        }
        return ret;
    }
}
