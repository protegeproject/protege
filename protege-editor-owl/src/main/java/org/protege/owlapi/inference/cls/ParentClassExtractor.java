package org.protege.owlapi.inference.cls;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Set;


public class ParentClassExtractor implements OWLAxiomVisitor {

    private NamedClassExtractor extractor = new NamedClassExtractor();

    private OWLClass current;


    public void setCurrentClass(OWLClass current) {
        this.current = current;
    }


    public void reset() {
        extractor.reset();
    }


    public Set<OWLClass> getResult() {
        return extractor.getResult();
    }

    @Override
    public void visit(@Nonnull OWLSubClassOfAxiom axiom) {
        axiom.getSuperClass().accept(extractor);
    }

    @Override
    public void visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
        for (OWLClassExpression desc : axiom.getClassExpressions()) {
            if (desc.equals(current)) {
                continue;
            }
            desc.accept(extractor);
        }
    }
}

