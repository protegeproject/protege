package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

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
            final Set<OWLClass> changedNodes = new HashSet<OWLClass>();
            for (OWLOntologyChange chg : changes){
                if (chg.isAxiomChange()){
                    chg.getAxiom().accept(new OWLAxiomVisitorAdapter(){
                        public void visit(OWLClassAssertionAxiom ax) {
                            if (!ax.getDescription().isAnonymous()){
                                changedNodes.add(ax.getDescription().asOWLClass());
                            }
                        }
                    });
                }
            }
            for (OWLClass cls : changedNodes){
                fireNodeChanged(cls);
            }
        }
    };

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
        for (OWLOntology ont : ontologies) {
            for (OWLIndividual ind : ont.getReferencedIndividuals()) {
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
                    if (!ax.getDescription().isAnonymous()) {
                        roots.add(ax.getDescription());
                    }
                }
            }
        }
        fireHierarchyChanged();
    }


    public Set<OWLObject> getRoots() {
        return roots;
    }


    public Set<OWLObject> getChildren(OWLObject object) {
        if (roots.contains(object)) {
            OWLClass cls = (OWLClass) object;
            Set<OWLObject> individuals = new HashSet<OWLObject>();
            for (OWLOntology ont : ontologies) {
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(cls)) {
                    individuals.add(ax.getIndividual());
                }
            }
            return individuals;
        }
        else {
            return Collections.emptySet();
        }
    }


    public Set<OWLObject> getParents(OWLObject object) {
        if (object instanceof OWLIndividual) {
            OWLIndividual ind = (OWLIndividual) object;
            Set<OWLObject> clses = new HashSet<OWLObject>();
            for (OWLOntology ont : ontologies) {
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
                    if (!ax.getDescription().isAnonymous()) {
                        clses.add(ax.getDescription().asOWLClass());
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
        return true;
    }


    public void dispose() {
        getManager().removeOntologyChangeListener(ontChangeListener);
        super.dispose();
    }
}
