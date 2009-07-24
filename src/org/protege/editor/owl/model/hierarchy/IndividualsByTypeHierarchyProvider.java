package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class IndividualsByTypeHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLObject> {

    private Set<OWLObject> roots;

    private Set<OWLOntology> ontologies;

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){

        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            handleOntologyChanges(changes);
        }
    };


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        final Set<OWLClass> changedNodes = new HashSet<OWLClass>();
        final Set<OWLObject> newRoots = new HashSet<OWLObject>(roots);

        Set<OWLNamedIndividual> checkIndividuals = new HashSet<OWLNamedIndividual>();

        for (OWLOntologyChange chg : changes){

            if (chg.isAxiomChange()){
                for (OWLEntity ref : chg.getAxiom().getReferencedEntities()){
                    if (ref.isOWLNamedIndividual()){
                        checkIndividuals.add(ref.asOWLNamedIndividual());
                    }
                }

                if (chg instanceof AddAxiom){
                    chg.getAxiom().accept(new OWLAxiomVisitorAdapter(){
                        public void visit(OWLClassAssertionAxiom ax) {
                            if (!ax.getClassExpression().isAnonymous()){
                                if (roots.contains(ax.getClassExpression().asOWLClass())){
                                    changedNodes.add(ax.getClassExpression().asOWLClass());
                                }
                                else{
                                    newRoots.add(ax.getClassExpression().asOWLClass());
                                }
                            }
                        }
                    });
                }
                else if (chg instanceof RemoveAxiom){
                    chg.getAxiom().accept(new OWLAxiomVisitorAdapter(){
                        public void visit(OWLClassAssertionAxiom ax) {
                            if (!ax.getClassExpression().isAnonymous()){
                                if (roots.contains(ax.getClassExpression().asOWLClass())){
                                    changedNodes.add(ax.getClassExpression().asOWLClass());
                                    // @@TODO should remove the type node if no other members remain
                                }
                            }
                        }
                    });
                }
            }
        }

        for (OWLNamedIndividual ind : checkIndividuals){
            if (isTyped(ind) || !isReferenced(ind)){
                newRoots.remove(ind);
            }
            else {
                newRoots.add(ind);
            }
        }

        if (!newRoots.equals(roots)){
            roots = newRoots;
            fireHierarchyChanged();
        }
        else{
            for (OWLClass cls : changedNodes){
                fireNodeChanged(cls);
            }
        }
    }


    private boolean isTyped(OWLNamedIndividual ind) {
        for (OWLOntology ont : ontologies) {
            if (!ont.getClassAssertionAxioms(ind).isEmpty()){
                return true;
            }
        }
        return false;
    }


    private boolean isReferenced(OWLNamedIndividual ind) {
        for (OWLOntology ont : ontologies){
            if (ont.containsIndividualReference(ind.getURI())){
                return true;
            }
        }
        return false;
    }


    public IndividualsByTypeHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.roots = new HashSet<OWLObject>();
        this.ontologies = new HashSet<OWLOntology>();

        owlOntologyManager.addOntologyChangeListener(ontChangeListener);
    }


    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies.clear();
        this.ontologies.addAll(ontologies);
        rebuild();
    }


    private void rebuild() {
        roots.clear();
        Set<OWLNamedIndividual> allIndividuals = new HashSet<OWLNamedIndividual>();
        Set<OWLNamedIndividual> typedIndividuals = new HashSet<OWLNamedIndividual>();
        for (OWLOntology ont : ontologies) {
            for (OWLNamedIndividual ind : ont.getReferencedIndividuals()) {
                allIndividuals.add(ind);
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
                    if (!ax.getClassExpression().isAnonymous()) {
                        roots.add(ax.getClassExpression());
                        typedIndividuals.add(ind);
                    }
                }
            }
        }
        allIndividuals.removeAll(typedIndividuals);

        roots.addAll(allIndividuals);

        fireHierarchyChanged();
    }


    public Set<OWLObject> getRoots() {
        return roots;
    }


    public Set<OWLObject> getChildren(OWLObject object) {
        if (object instanceof OWLClass && roots.contains(object)) {
            OWLClass cls = (OWLClass) object;
            Set<OWLObject> individuals = new HashSet<OWLObject>();
            for (OWLOntology ont : ontologies) {
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(cls)) {
                    if (!ax.getIndividual().isAnonymous()){
                        individuals.add(ax.getIndividual());
                    }
                }
            }
            return individuals;
        }
        else {
            return Collections.emptySet();
        }
    }


    public Set<OWLObject> getParents(OWLObject object) {
        if (object instanceof OWLNamedIndividual) {
            OWLIndividual ind = (OWLNamedIndividual) object;
            Set<OWLObject> clses = new HashSet<OWLObject>();
            for (OWLOntology ont : ontologies) {
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
                    if (!ax.getClassExpression().isAnonymous()) {
                        clses.add(ax.getClassExpression().asOWLClass());
                    }
                }
            }
            return clses;
        }
        return Collections.emptySet();
    }


    public Set<OWLObject> getEquivalents(OWLObject object) {
        return Collections.emptySet();
    }


    public boolean containsReference(OWLObject object) {
        return object instanceof OWLNamedIndividual || roots.contains(object);
    }


    public void dispose() {
        getManager().removeOntologyChangeListener(ontChangeListener);
        super.dispose();
    }
}
