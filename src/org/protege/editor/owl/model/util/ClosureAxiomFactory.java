package org.protege.editor.owl.model.util;

import org.semanticweb.owl.model.*;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClosureAxiomFactory extends ObjectSomeRestrictionFillerExtractor {

    protected OWLDataFactory owlDataFactory;

    private Set<OWLOntology> onts;


    public ClosureAxiomFactory(OWLObjectProperty objectProperty, OWLDataFactory df, Set<OWLOntology> onts) {
        super(df, objectProperty);
        this.owlDataFactory = df;
        this.onts = onts;
    }


    public static OWLAxiom getClosureAxiom(OWLClass cls, OWLObjectProperty prop, OWLDataFactory df, Set<OWLOntology> onts){
        ClosureAxiomFactory fac = new ClosureAxiomFactory(prop, df, onts);
        cls.accept(fac);
        final OWLObjectAllRestriction closure = fac.getClosureRestriction();
        return (closure != null) ? df.getOWLSubClassAxiom(cls, closure) : null;
    }


    /**
     * Gets a universal restriction (<code>OWLObjectAllRestriction</code>) that
     * closes off the existential restrictions that have been visited by this
     * visitor.  For example, if the visitor had visited p some A, p some B, then
     * the restriction p only (A or B) would be returned.
     * @return A universal restriction that represents a closure axiom for visited
     *         restrictions, or <code>null</code> if no existential restrictions have been
     *         visited by this visitor and a universal closure axiom therefore doesn't make
     *         sense.
     */
    public OWLObjectAllRestriction getClosureRestriction() {
        Set<OWLDescription> descriptions = getFillers();
        if (descriptions.isEmpty()) {
            return null;
        }
        else {
            if (descriptions.size() == 1) {
                return owlDataFactory.getOWLObjectAllRestriction(getObjectProperty(), descriptions.iterator().next());
            }
            else {
                return owlDataFactory.getOWLObjectAllRestriction(getObjectProperty(),
                                                                 owlDataFactory.getOWLObjectUnionOf(descriptions));
            }
        }
    }


    /* Get the inherited restrictions also */
    public void visit(OWLClass cls) {
        if (onts != null){
            for (OWLDescription superCls : cls.getSuperClasses(onts)){
                superCls.accept(this);
            }
            for (OWLDescription equiv : cls.getEquivalentClasses(onts)){
                equiv.accept(this);
            }
        }
    }


    public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
        for (OWLDescription op : owlObjectIntersectionOf.getOperands()){
            op.accept(this);
        }
    }


    /* Get min cardinality restriction fillers */
    public void visit(OWLObjectMinCardinalityRestriction restr) {
        handleCardinality(restr);
    }

    /* Get exact cardinality fillers */
    public void visit(OWLObjectExactCardinalityRestriction restr) {
        handleCardinality(restr);
    }


    public void visit(OWLObjectSomeRestriction restr) {
        if (restr.getProperty().equals(getObjectProperty())){
            OWLDescription filler = restr.getFiller();
            if (!filler.equals(owlDataFactory.getOWLThing())) {
                fillers.add(filler);
            }
        }
    }


    private void handleCardinality(OWLObjectCardinalityRestriction restr){
        if (restr.getProperty().equals(getObjectProperty()) && restr.getCardinality() > 0){
            OWLDescription filler = restr.getFiller();
            if (!filler.equals(owlDataFactory.getOWLThing())) {
                fillers.add(filler);
            }
        }
    }



    /**
     * @deprecated use <code>ClosureAxiomFactory(OWLDataFactory df, OWLObjectProperty objectProperty)</code>
     * Strange!
     */
    public ClosureAxiomFactory(OWLDataFactory owlDataFactory, OWLObjectProperty objectProperty, OWLDataFactory owlDataFactory1) {
        this(objectProperty, owlDataFactory, null);
    }


    /**
     * @deprecated use <code>getClosureRestriction()</code>
     */
    public OWLObjectAllRestriction getClosureAxiom(){
        return getClosureRestriction();
    }
}
