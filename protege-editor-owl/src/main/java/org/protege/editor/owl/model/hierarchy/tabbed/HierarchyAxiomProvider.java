package org.protege.editor.owl.model.hierarchy.tabbed;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 Sep 16
 */
public interface HierarchyAxiomProvider<E extends OWLEntity> {

    /**
     * Creates an axiom that places the specified child and parent (sub/super) in a hierarchy.
     * @param child The child.
     * @param parent The parent.
     * @return The hierarchy axiom, or an empty optional if there should not be an axiom (e.g. SubClassOf(A owl:Thing)).
     */
    @Nonnull
    Optional<OWLAxiom> getAxiom(@Nonnull E child, @Nonnull E parent);
}
