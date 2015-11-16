package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class IndividualsByInferredTypeHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLObject> {

    private Map<OWLObject, Set<OWLObject>> typeNodes = new HashMap<OWLObject, Set<OWLObject>>();

    private OWLReasoner reasoner;

    private boolean showDirect = true;


    public IndividualsByInferredTypeHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
    }


    public void setReasoner(OWLReasoner reasoner) {
        this.reasoner = reasoner;
        rebuild();
    }


    private void rebuild() {
        typeNodes.clear();
        if (reasoner != null){
            Set<OWLOntology> importsClosure = reasoner.getRootOntology().getImportsClosure();
            for (OWLOntology ont : importsClosure){
                for (OWLClass cls : ont.getClassesInSignature()) {
                    final Set<OWLNamedIndividual> inds = reasoner.getInstances(cls, showDirect).getFlattened();
                    if (!inds.isEmpty()){
                        typeNodes.put(cls, new HashSet<OWLObject>(inds));
                    }
                }
            }
        }
        fireHierarchyChanged();
    }


    public void setOntologies(Set<OWLOntology> ontologies) {
        throw new RuntimeException("Use setReasoner()");
    }


    public Set<OWLObject> getRoots() {
        return typeNodes.keySet();
    }


    public Set<OWLObject> getUnfilteredChildren(OWLObject object) {
        if (reasoner != null && typeNodes.containsKey(object)) {
            return typeNodes.get(object);
        }
        else {
            return Collections.emptySet();
        }
    }


    public Set<OWLObject> getParents(OWLObject object) {
        if (reasoner != null && typeNodes.containsKey(object)) {
            return Collections.emptySet();
        }
        else {
            OWLNamedIndividual ind = (OWLNamedIndividual) object;
            Set<OWLObject> clses = new HashSet<OWLObject>();
            for (OWLClass cls : reasoner.getTypes(ind, showDirect).getFlattened()) {
                clses.add(cls);
            }
            return clses;
        }
    }


    public Set<OWLObject> getEquivalents(OWLObject object) {
        return Collections.emptySet();
    }


    public boolean containsReference(OWLObject object) {
        return true;
    }


    public void dispose() {
        super.dispose();
    }
}