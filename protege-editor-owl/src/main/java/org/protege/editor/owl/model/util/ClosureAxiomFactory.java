package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Set;
import java.util.TreeSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClosureAxiomFactory extends ObjectSomeValuesFromFillerExtractor {

    protected OWLDataFactory owlDataFactory;

    private Set<OWLOntology> onts;
    
    private Set<OWLClass> visitedClasses = new TreeSet<OWLClass>();


    private ClosureAxiomFactory(OWLObjectProperty objectProperty, OWLDataFactory df, Set<OWLOntology> onts) {
        super(df, objectProperty);
        this.owlDataFactory = df;
        this.onts = onts;
    }


    public static OWLAxiom getClosureAxiom(OWLClass cls, OWLObjectProperty prop, OWLDataFactory df, Set<OWLOntology> onts){
        ClosureAxiomFactory fac = new ClosureAxiomFactory(prop, df, onts);
        cls.accept(fac);
        final OWLObjectAllValuesFrom closure = fac.getClosureRestriction();
        return (closure != null) ? df.getOWLSubClassOfAxiom(cls, closure) : null;
    }


    /**
     * Gets a universal restriction (<code>OWLObjectAllValuesFrom</code>) that
     * closes off the existential restrictions that have been visited by this
     * visitor.  For example, if the visitor had visited p some A, p some B, then
     * the restriction p only (A or B) would be returned.
     * @return A universal restriction that represents a closure axiom for visited
     *         restrictions, or <code>null</code> if no existential restrictions have been
     *         visited by this visitor and a universal closure axiom therefore doesn't make
     *         sense.
     */
    public OWLObjectAllValuesFrom getClosureRestriction() {
        Set<OWLClassExpression> descriptions = getFillers();
        if (descriptions.isEmpty()) {
            return null;
        }
        else {
            if (descriptions.size() == 1) {
                return owlDataFactory.getOWLObjectAllValuesFrom(getObjectProperty(), descriptions.iterator().next());
            }
            else {
                return owlDataFactory.getOWLObjectAllValuesFrom(getObjectProperty(),
                                                                 owlDataFactory.getOWLObjectUnionOf(descriptions));
            }
        }
    }


    /* Get the inherited restrictions also */
    public void visit(OWLClass cls) {
    	if (visitedClasses.contains(cls)) {
    		return;
    	}
    	else if (onts != null){
    		visitedClasses.add(cls);
            for (OWLClassExpression superCls : EntitySearcher.getSuperClasses(cls, onts)){
                superCls.accept(this);
            }
            for (OWLClassExpression equiv : EntitySearcher.getEquivalentClasses(cls, onts)){
                equiv.accept(this);
            }
        }
    }


    public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
        for (OWLClassExpression op : owlObjectIntersectionOf.getOperands()){
            op.accept(this);
        }
    }


    /* Get min cardinality restriction fillers */
    public void visit(OWLObjectMinCardinality restr) {
        handleCardinality(restr);
    }

    /* Get exact cardinality fillers */
    public void visit(OWLObjectExactCardinality restr) {
        handleCardinality(restr);
    }


    public void visit(OWLObjectSomeValuesFrom restr) {
        if (restr.getProperty().equals(getObjectProperty())){
            OWLClassExpression filler = restr.getFiller();
            if (!filler.equals(owlDataFactory.getOWLThing())) {
                fillers.add(filler);
            }
        }
    }


    private void handleCardinality(OWLObjectCardinalityRestriction restr){
        if (restr.getProperty().equals(getObjectProperty()) && restr.getCardinality() > 0){
            OWLClassExpression filler = restr.getFiller();
            if (!filler.equals(owlDataFactory.getOWLThing())) {
                fillers.add(filler);
            }
        }
    }
}
