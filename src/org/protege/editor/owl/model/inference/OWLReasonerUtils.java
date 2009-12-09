package org.protege.editor.owl.model.inference;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 18, 2008<br><br>
 */
public class OWLReasonerUtils {

    private OWLReasoner r;
    private OWLDataFactory factory;


    public OWLReasonerUtils(OWLReasoner r) {
        this.r = r;
        factory = r.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
    }


    public Set<Set<OWLClassExpression>> getMostSpecificSets(Set<Set<OWLClassExpression>> setOfSets) {
        Set<Set<OWLClassExpression>> results = new HashSet<Set<OWLClassExpression>>();
        for(Set<OWLClassExpression> descrs : setOfSets) {
            if (results.isEmpty()){
                results.add(descrs);
            }
            else{
                for (Set<OWLClassExpression> result : results){
                    if (getReasoner().isEntailed(factory.getOWLSubClassOfAxiom(descrs.iterator().next(), result.iterator().next()))) {
                        // if the new set is more specific than an existing one, replace the existing one
                        results.remove(result);
                        results.add(descrs);
                    }
                    else if (!getReasoner().isEntailed(factory.getOWLSubClassOfAxiom(result.iterator().next(), descrs.iterator().next()))){
                        // if the new set is not more general than an existing one, add to the set
                        results.add(descrs);
                    }
                }
            }
        }
        return results;
    }


    protected OWLReasoner getReasoner() {
        return r;
    }


    public Set<OWLClassExpression> getMostSpecific(Set<OWLClassExpression> descriptions)  {
        Set<OWLClassExpression> results = new HashSet<OWLClassExpression>();
        for(OWLClassExpression descr : descriptions) {
            if (results.isEmpty()){
                results.add(descr);
            }
            else{
                for (OWLClassExpression result : results){
                    if (getReasoner().isEntailed(factory.getOWLSubClassOfAxiom(descr, result))){
                        // if the new descr is more specific than an existing one, replace the existing one
                        results.remove(result);
                        results.add(descr);
                    }
                    else if (!getReasoner().isEntailed(factory.getOWLSubClassOfAxiom(result, descr))){
                        // if the new set is not more general than an existing one, add to the set
                        results.add(descr);
                    }
                }
            }
        }
        return results;
    }
}
