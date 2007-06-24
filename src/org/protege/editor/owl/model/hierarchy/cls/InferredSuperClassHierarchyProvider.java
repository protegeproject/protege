package org.protege.editor.owl.model.hierarchy.cls;

import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.AbstractSuperClassHierarchyProvider;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLRuntimeException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class InferredSuperClassHierarchyProvider extends AbstractSuperClassHierarchyProvider {

    private OWLReasoner reasoner;


    public InferredSuperClassHierarchyProvider(OWLModelManager manager) {
        super(manager.getOWLOntologyManager());
    }


    public void setReasoner(OWLReasoner reasoner) {
        this.reasoner = reasoner;
        fireHierarchyChanged();
    }


    protected Set<? extends OWLDescription> getEquivalentClasses(OWLClass cls) {
        try {
            // Get the equivalent classes from the reasoner
            if (reasoner == null) {
                return Collections.emptySet();
            }
            if (!reasoner.isSatisfiable(cls)) {
                // We don't want every class in the ontology
                return Collections.emptySet();
            }
            return reasoner.getEquivalentClasses(cls);
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLClass> getChildren(OWLClass object) {
        try {
            // Simply get the superclasses from the reasoner
            if (reasoner == null) {
                return Collections.emptySet();
            }
            if (!reasoner.isSatisfiable(object)) {
                // We don't want every class in the ontology!!
                return Collections.emptySet();
            }
            return OWLReasonerAdapter.flattenSetOfSets(reasoner.getSuperClasses(object));
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        return Collections.emptySet();
    }


    public Set<OWLClass> getParents(OWLClass object) {
        try {
            // Simply get the superclasses from the reasoner
            if (reasoner == null) {
                return Collections.emptySet();
            }
            if (!reasoner.isSatisfiable(object)) {
                // We don't want every class in the ontology!!
                return Collections.emptySet();
            }
            return OWLReasonerAdapter.flattenSetOfSets(reasoner.getSubClasses(object));
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public boolean containsReference(OWLClass object) {
        return false;
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
