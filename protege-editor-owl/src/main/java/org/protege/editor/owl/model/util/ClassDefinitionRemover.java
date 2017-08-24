package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class ClassDefinitionRemover {

    @Nonnull
    private final OWLClass cls;

    @Nonnull
    private final OWLDataFactory dataFactory;

    @Nonnull
    private final Set<OWLOntology> ontologies = new HashSet<>();

    public ClassDefinitionRemover(@Nonnull OWLClass cls,
                                  @Nonnull Set<OWLOntology> ontologies,
                                  @Nonnull OWLDataFactory dataFactory) {
        this.cls = checkNotNull(cls);
        this.dataFactory = checkNotNull(dataFactory);
        this.ontologies.addAll(checkNotNull(ontologies));
    }

    /**
     * Gets a list of changes that are necessary to remove the axioms that constitue the logical definition
     * of the target class.
     * @return A list of changes.
     */
    @Nonnull
    public List<OWLOntologyChange> getChangesToRemoveDefinition() {
        List<OWLOntologyChange> changes = new ArrayList<>();
        ontologies.forEach(o -> generateChangesToRemoveDefinitionFromOntology(o, changes));
        return changes;
    }

    private void generateChangesToRemoveDefinitionFromOntology(@Nonnull OWLOntology o,
                                                               @Nonnull List<OWLOntologyChange> changes) {
        o.getSubClassAxiomsForSubClass(cls).forEach(ax -> changes.add(new RemoveAxiom(o, ax)));
        o.getEquivalentClassesAxioms(cls).forEach(ax -> changes.add(new RemoveAxiom(o, ax)));
        o.getDisjointClassesAxioms(cls).forEach(ax -> {
            changes.add(new RemoveAxiom(o, ax));
            Set<OWLClassExpression> remainingDisjointClasses = ax.getClassExpressionsMinus(cls);
            if(remainingDisjointClasses.size() > 1) {
                OWLAxiom freshAx = dataFactory.getOWLDisjointClassesAxiom(remainingDisjointClasses,
                                                                          ax.getAnnotations());
                changes.add(new AddAxiom(o, freshAx));
            }
        });
        o.getHasKeyAxioms(cls).forEach(ax -> new RemoveAxiom(o, ax));
        o.getDisjointUnionAxioms(cls).forEach(ax -> new RemoveAxiom(o, ax));
    }
}
