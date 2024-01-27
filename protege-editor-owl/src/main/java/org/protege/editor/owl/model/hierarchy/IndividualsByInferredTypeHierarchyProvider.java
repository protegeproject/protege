package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class IndividualsByInferredTypeHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLObject> {

    private Map<OWLObject, Set<OWLObject>> typeNodes = new HashMap<>();

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
                        typeNodes.put(cls, new HashSet<>(inds));
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
            Set<OWLObject> clses = new HashSet<>();
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