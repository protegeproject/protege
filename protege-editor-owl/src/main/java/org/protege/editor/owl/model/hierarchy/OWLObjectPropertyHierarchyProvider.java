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
public class OWLObjectPropertyHierarchyProvider extends AbstractOWLPropertyHierarchyProvider<OWLClassExpression, OWLObjectPropertyExpression, OWLObjectProperty> {

    public OWLObjectPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
    }


    protected Set<OWLObjectProperty> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes) {
        Set<OWLObjectProperty> properties = new HashSet<>();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                OWLAxiomChange axiomChange = (OWLAxiomChange) change;
                for (OWLEntity entity : axiomChange.getSignature()) {
                    if (entity.isOWLObjectProperty()) {
                        properties.add(entity.asOWLObjectProperty());
                    }
                }
            }
        }
        return properties;
    }


    /**
     * Gets the relevant properties in the specified ontology that are contained
     * within the property hierarchy.  For example, for an object property hierarchy
     * this would constitute the set of referenced object properties in the specified
     * ontology.
     * @param ont The ontology
     */
    protected Set<? extends OWLObjectProperty> getReferencedProperties(OWLOntology ont) {
        return ont.getObjectPropertiesInSignature();
    }


    protected Set<? extends OWLSubPropertyAxiom> getSubPropertyAxiomForRHS(OWLObjectProperty prop, OWLOntology ont) {
        return ont.getObjectSubPropertyAxiomsForSuperProperty(prop);
    }


    protected OWLObjectProperty getRoot() {
        return getManager().getOWLDataFactory().getOWLTopObjectProperty();
    }


    protected boolean containsReference(OWLOntology ont, OWLObjectProperty prop) {
        return ont.containsObjectPropertyInSignature(prop.getIRI());
    }

    @Override
    protected Collection<OWLObjectProperty> getSuperProperties(OWLObjectProperty subProperty, Set<OWLOntology> ontologies) {
        return EntitySearcher.getSuperProperties(subProperty, ontologies)
                .stream()
                .filter(p -> !p.isAnonymous())
                .map(p -> (OWLObjectProperty) p)
                .collect(toList());
    }

    @Override
    protected Collection<OWLObjectProperty> getSubProperties(OWLObjectProperty superProp, Set<OWLOntology> ontologies) {
        List<OWLObjectProperty> result = new ArrayList<>();
        for(OWLOntology ont : ontologies) {
            ont.getObjectSubPropertyAxiomsForSuperProperty(superProp)
                    .stream()
                    .map(OWLSubPropertyAxiom::getSubProperty)
                    .filter(p -> !p.isAnonymous())
                    .map(p -> (OWLObjectProperty) p)
                    .forEach(result::add);
        }
        return result;
    }
}
