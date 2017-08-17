package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.*;

import static java.util.stream.Collectors.toList;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyHierarchyProvider extends AbstractOWLPropertyHierarchyProvider<OWLDataRange, OWLDataPropertyExpression, OWLDataProperty> {

    public OWLDataPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
    }


    protected Set<OWLDataProperty> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes) {
        Set<OWLDataProperty> result = new HashSet<>();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                for (OWLEntity entity : change.getSignature()) {
                    if (entity.isOWLDataProperty()) {
                        result.add(entity.asOWLDataProperty());
                    }
                }
            }
        }
        return result;
    }


    /**
     * Gets the relevant properties in the specified ontology that are contained
     * within the property hierarchy.  For example, for an object property hierarchy
     * this would constitute the set of referenced object properties in the specified
     * ontology.
     * @param ont The ontology
     */
    protected Set<? extends OWLDataProperty> getReferencedProperties(OWLOntology ont) {
        return ont.getDataPropertiesInSignature();
    }


    protected boolean containsReference(OWLOntology ont, OWLDataProperty prop) {
        return ont.containsDataPropertyInSignature(prop.getIRI());
    }


    protected Set<? extends OWLSubPropertyAxiom<OWLDataPropertyExpression>> getSubPropertyAxiomForRHS(
            OWLDataProperty prop, OWLOntology ont) {
        return ont.getDataSubPropertyAxiomsForSuperProperty(prop);
    }


    protected OWLDataProperty getRoot() {
        return getManager().getOWLDataFactory().getOWLTopDataProperty();
    }

    @Override
    protected Collection<OWLDataProperty> getSuperProperties(OWLDataProperty subProperty, Set<OWLOntology> ontologies) {
        return EntitySearcher.getSuperProperties(subProperty, ontologies.stream())
                .filter(p -> !p.isAnonymous())
                .map(p -> (OWLDataProperty) p)
                .collect(toList());
    }

    @Override
    protected Collection<OWLDataProperty> getSubProperties(OWLDataProperty superProp, Set<OWLOntology> ontologies) {
        List<OWLDataProperty> result = new ArrayList<>();
        for(OWLOntology ont : ontologies) {
            ont.getDataSubPropertyAxiomsForSuperProperty(superProp)
                    .stream()
                    .map(OWLSubPropertyAxiom::getSubProperty)
                    .filter(p -> !p.isAnonymous())
                    .map(p -> (OWLDataProperty) p)
                    .forEach(result::add);
        }
        return result;
    }
}
