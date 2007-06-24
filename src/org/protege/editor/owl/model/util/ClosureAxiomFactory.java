package org.protege.editor.owl.model.util;

import java.util.Set;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;


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


    public ClosureAxiomFactory(OWLDataFactory dataFactory, OWLObjectProperty objectProperty,
                               OWLDataFactory owlDataFactory) {
        super(dataFactory, objectProperty);
        this.owlDataFactory = owlDataFactory;
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
    public OWLObjectAllRestriction getClosureAxiom() {
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
}
