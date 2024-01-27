package org.protege.editor.owl.model.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.util.OWLUtilities;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31 Aug 2017
 * <p>
 * Manages lookup for deprecated classes
 */
public class DeprecationCache {

    private static final Logger logger = LoggerFactory.getLogger(DeprecationCache.class);

    private final Set<IRI> deprecatedEntities = new HashSet<>();

    @Nonnull
    private final OWLAnnotationProperty owlDeprecatedAnnotationProperty;

    /**
     * Constructs a {@link DeprecationCache}.
     *
     * @param owlDeprecatedAnnotationProperty The annotation property that represents owl:deprecated
     */
    public DeprecationCache(@Nonnull OWLAnnotationProperty owlDeprecatedAnnotationProperty) {
        this.owlDeprecatedAnnotationProperty = owlDeprecatedAnnotationProperty;
    }

    /**
     * Determines if the specified object is deprecated.  The method will only ever return a true value for
     * OWLEntity and IRI objects.
     *
     * @param object The object.
     * @return true if the entity is deprecated, otherwise false.
     */
    public boolean isDeprecated(@Nonnull OWLObject object) {
        IRI iri;
        if(object instanceof IRI) {
            iri = (IRI) object;
        }
        else if(object instanceof OWLEntity) {
            iri = ((OWLEntity) object).getIRI();
        }
        else {
            return false;
        }
        return deprecatedEntities.contains(iri);
    }

    /**
     * Rebuild the underlying cache of deprecated entities using the specified ontologies.
     *
     * @param ontologies A stream of ontologies
     */
    public void rebuildCacheFromActiveOntologies(@Nonnull Collection<OWLOntology> ontologies) {
        logger.debug("[DeprecationCache] Rebuilding deprecated entities cache");
        deprecatedEntities.clear();
        ontologies.forEach(o -> {
            o.getReferencingAxioms(owlDeprecatedAnnotationProperty).stream()
             .filter(ax -> ax instanceof OWLAnnotationAssertionAxiom)
             .map(ax -> (OWLAnnotationAssertionAxiom) ax)
             .filter(ax -> ax.getSubject() instanceof IRI)
             .map(ax -> (IRI) ax.getSubject())
             .forEach(deprecatedEntities::add);
        });
    }

    /**
     * Updates the cache from the specified ontology changes.  It is assumed that the ontologies referenced in the
     * changes are the ontologies that have been used to build the cache.  If this is not the case then the update may
     * produce strange results.
     *
     * @param changes The changes.
     */
    public void handleOntologyChanges(@Nonnull List<? extends OWLOntologyChange> changes,
                                      @Nonnull Collection<OWLOntology> ontologies) {
        logger.debug("[DeprecationCache] Updating deprecated entities cache");
        // Work out what might have changed
        changes.stream()
               .filter(OWLOntologyChange::isAxiomChange)
               .map(chg -> (OWLAxiomChange) chg)
               .filter(chg -> chg.getAxiom() instanceof OWLAnnotationAssertionAxiom)
               .map(chg -> (OWLAnnotationAssertionAxiom) chg.getAxiom())
               .filter(ax -> ax.getProperty().isDeprecated())
               // The change adds/removes an annotation that COULD affect the deprecation status of an entity.
               // Whether it actually affects the deprecation status depends upon other annotation assertions
               // that are possibly spread throughout the specified set of ontologies.  Also, just because an
               // axiom that specifies deprecation was removed doesn't mean all axioms that specifiy deprecation
               // for that entity were removed due to axiom annotations
               .filter(ax -> ax.getSubject() instanceof IRI)
               .map(ax -> (IRI) ax.getSubject())
               .distinct()
               .forEach(iri -> {
                   if(OWLUtilities.isDeprecated(iri, ontologies)) {
                       deprecatedEntities.add(iri);
                   }
                   else {
                       deprecatedEntities.remove(iri);
                   }
               });
    }
}
