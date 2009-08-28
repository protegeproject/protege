package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;

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

    private Set<OWLNamedIndividual> untypedIndividuals = new HashSet<OWLNamedIndividual>();

    private Set<OWLClass> classes = new HashSet<OWLClass>();

    private Set<OWLOntology> ontologies = new HashSet<OWLOntology>();

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){

        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
            handleOntologyChanges(changes);
        }
    };


    public IndividualsByTypeHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        owlOntologyManager.addOntologyChangeListener(ontChangeListener);
    }


    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies.clear();
        this.ontologies.addAll(ontologies);
        rebuild();
    }


    private void rebuild() {
        classes.clear();
        untypedIndividuals.clear();

        Set<OWLNamedIndividual> typedIndividuals = new HashSet<OWLNamedIndividual>();
        for (OWLOntology ont : ontologies) {
            for (OWLNamedIndividual ind : ont.getReferencedIndividuals()) {
                untypedIndividuals.add(ind);
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
                    if (!ax.getClassExpression().isAnonymous()) {
                        classes.add(ax.getClassExpression().asOWLClass());
                        typedIndividuals.add(ind);
                    }
                }
            }
        }

        untypedIndividuals.removeAll(typedIndividuals);

        fireHierarchyChanged();
    }


    public Set<OWLObject> getRoots() {
        Set<OWLObject> roots = new HashSet<OWLObject>(classes);
        roots.addAll(untypedIndividuals);
        return roots;
    }


    public Set<OWLObject> getChildren(OWLObject object) {
        if (object instanceof OWLClass && classes.contains((OWLClass)object)) {
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
        return object instanceof OWLNamedIndividual ||
               (object instanceof OWLClass && classes.contains((OWLClass)object));
    }


    public void dispose() {
        getManager().removeOntologyChangeListener(ontChangeListener);
        super.dispose();
    }


    public Set<OWLClass> getRootClasses() {
        return new HashSet<OWLClass>(classes);
    }


    public Set<OWLNamedIndividual> getUntypedIndividuals(){
        return new HashSet<OWLNamedIndividual>(untypedIndividuals);
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {

        TypesChangeVisitor changeVisitor = new TypesChangeVisitor();

        for (OWLOntologyChange chg : changes){
            chg.accept(changeVisitor);
        }

        for (OWLObject node : changeVisitor.getNodes()){
            fireNodeChanged(node);
        }
    }


    /**
     * Scans changes for nodes that have changed in the tree
     */
    class TypesChangeVisitor extends OWLOntologyChangeVisitorAdapter {

        private Set<OWLObject> changedNodes = new HashSet<OWLObject>();

        Set<OWLNamedIndividual> checkIndividuals = new HashSet<OWLNamedIndividual>();

        private OWLAxiomVisitor addAxiomVisitor = new OWLAxiomVisitorAdapter(){
            public void visit(OWLClassAssertionAxiom ax) {
                handleAddClassAssertionAxiom(ax);
            }
        };

        private OWLAxiomVisitor removeAxiomVisitor = new OWLAxiomVisitorAdapter(){
            public void visit(OWLClassAssertionAxiom ax) {
                handleRemoveClassAssertionAxiom(ax);
            }
        };


        public Set<OWLObject> getNodes(){
            for (OWLNamedIndividual ind : checkIndividuals){
                if (untypedIndividuals.contains(ind)){
                    if (isTyped(ind) || !isReferenced(ind)){
                        untypedIndividuals.remove(ind);
                        changedNodes.add(ind);
                    }
                }
                else if (isReferenced(ind) && !isTyped(ind)){
                    untypedIndividuals.add(ind);
                    changedNodes.add(ind);
                }
            }

            checkIndividuals.clear(); // only do this once

            return changedNodes;
        }


        public void visit(AddAxiom addAxiom) {
            if (ontologies.contains(addAxiom.getOntology())){

                handleAxiomChange(addAxiom);

                addAxiom.getAxiom().accept(addAxiomVisitor);
            }
        }


        public void visit(RemoveAxiom removeAxiom) {
            if (ontologies.contains(removeAxiom.getOntology())){

                handleAxiomChange(removeAxiom);

                removeAxiom.getAxiom().accept(removeAxiomVisitor);
            }
        }


        private void handleAxiomChange(OWLAxiomChange chg) {
            for (OWLEntity ref : chg.getAxiom().getReferencedEntities()){
                if (ref.isOWLNamedIndividual()){
                    checkIndividuals.add(ref.asOWLNamedIndividual());
                }
            }
        }


        private void handleAddClassAssertionAxiom(OWLClassAssertionAxiom ax) {
            if (!ax.getClassExpression().isAnonymous()){
                final OWLClass type = ax.getClassExpression().asOWLClass();
                if (classes.contains(type)){
                    changedNodes.add(type);
                }
                else{
                    classes.add(type);
                    changedNodes.add(type);
                }
            }
        }


        private void handleRemoveClassAssertionAxiom(OWLClassAssertionAxiom ax) {
            if (!ax.getClassExpression().isAnonymous()){
                final OWLClass type = ax.getClassExpression().asOWLClass();
                if (classes.contains(type)){
                    if (getChildren(type).isEmpty()){
                        classes.remove(type);
                    }
                    changedNodes.add(type);
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
                if (ont.containsIndividualReference(ind.getIRI())){
                    return true;
                }
            }
            return false;
        }
    }
}
