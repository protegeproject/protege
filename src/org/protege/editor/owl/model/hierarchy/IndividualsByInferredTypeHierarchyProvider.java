package org.protege.editor.owl.model.hierarchy;

import org.apache.log4j.Logger;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class IndividualsByInferredTypeHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLObject> {

    private static final Logger logger = Logger.getLogger(IndividualsByInferredTypeHierarchyProvider.class);

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
            for (OWLOntology ont : reasoner.getLoadedOntologies()){
                for (OWLClass cls : ont.getReferencedClasses()) {
                    try {
                        final Set<OWLIndividual> inds = reasoner.getIndividuals(cls, showDirect);
                        if (!inds.isEmpty()){
                            typeNodes.put(cls, new HashSet<OWLObject>(inds));
                        }
                    }
                    catch (OWLReasonerException e) {
                        logger.error(e);
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


    public Set<OWLObject> getChildren(OWLObject object) {
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
            OWLIndividual ind = (OWLIndividual) object;
            Set<OWLObject> clses = new HashSet<OWLObject>();
            try {
                for (OWLClass cls : OWLReasonerAdapter.flattenSetOfSets(reasoner.getTypes(ind, showDirect))) {
                    clses.add(cls);
                }
            }
            catch (OWLReasonerException e) {
                logger.error(e);
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