package org.protege.editor.owl.model.util;

import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;


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
        owlDataFactory = df;
        this.onts = onts;
    }


    public static OWLAxiom getClosureAxiom(OWLClass cls, OWLObjectProperty prop, OWLDataFactory df, Set<OWLOntology> onts){
        ClosureAxiomFactory fac = new ClosureAxiomFactory(prop, df, onts);
        cls.accept(fac);
        final OWLObjectAllValuesFrom closure = fac.getClosureRestriction();
        return closure != null ? df.getOWLSubClassOfAxiom(cls, closure) : null;
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
    @Override
    public void visit(OWLClass cls) {
    	if (visitedClasses.contains(cls)) {
    		return;
    	}
    	else if (onts != null){
    		visitedClasses.add(cls);
            for (OWLClassExpression superCls : EntitySearcher.getSuperClasses(
                    cls, onts)) {
                superCls.accept(this);
            }
            for (OWLClassExpression equiv : EntitySearcher
                    .getEquivalentClasses(cls, onts)) {
                equiv.accept(this);
            }
        }
    }


    @Override
    public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
        for (OWLClassExpression op : owlObjectIntersectionOf.getOperands()){
            op.accept(this);
        }
    }


    /* Get min cardinality restriction fillers */
    @Override
    public void visit(OWLObjectMinCardinality restr) {
        handleCardinality(restr);
    }

    /* Get exact cardinality fillers */
    @Override
    public void visit(OWLObjectExactCardinality restr) {
        handleCardinality(restr);
    }


    @Override
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
