package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class ObjectPropertyDefinitionExtractor {

    @Nonnull
    private final OWLObjectProperty property;

    @Nonnull
    private final OWLOntology ontology;

    @Nonnull
    private final OWLDataFactory dataFactory;

    public ObjectPropertyDefinitionExtractor(@Nonnull OWLObjectProperty property,
                                             @Nonnull OWLOntology ontology,
                                             @Nonnull OWLDataFactory dataFactory) {
        this.property = checkNotNull(property);
        this.ontology = checkNotNull(ontology);
        this.dataFactory = checkNotNull(dataFactory);
    }

    public Set<OWLAxiom> getDefiningAxioms() {
        return Stream.concat(getUnaryAxioms(),
                             ontology.getDisjointObjectPropertiesAxioms(property).stream())
                     .collect(toSet());
    }

    private Stream<OWLAxiom> getUnaryAxioms() {
        return Stream.of(
                ontology.getObjectSubPropertyAxiomsForSubProperty(property),
                ontology.getEquivalentObjectPropertiesAxioms(property),
                ontology.getFunctionalObjectPropertyAxioms(property),
                ontology.getInverseFunctionalObjectPropertyAxioms(property),
                ontology.getSymmetricObjectPropertyAxioms(property),
                ontology.getTransitiveObjectPropertyAxioms(property),
                ontology.getAsymmetricObjectPropertyAxioms(property),
                ontology.getReflexiveObjectPropertyAxioms(property),
                ontology.getIrreflexiveObjectPropertyAxioms(property),
                ontology.getObjectPropertyDomainAxioms(property),
                ontology.getObjectPropertyRangeAxioms(property))
                     .flatMap(Collection::stream);
    }


    public List<OWLOntologyChange> getChangesToRemoveDefinition() {
        List<OWLOntologyChange> changes = new ArrayList<>();
        getUnaryAxioms().forEach(ax -> changes.add(new RemoveAxiom(ontology, ax)));
        ontology.getDisjointObjectPropertiesAxioms(property).forEach(ax -> {
            changes.add(new RemoveAxiom(ontology, ax));
            if(ax.getProperties().size() > 2) {
                OWLDisjointObjectPropertiesAxiom replacement = dataFactory.getOWLDisjointObjectPropertiesAxiom(
                        ax.getPropertiesMinus(property),
                        ax.getAnnotations()
                );
                changes.add(new RemoveAxiom(ontology, replacement));
            }
        });
        return changes;
    }
}
