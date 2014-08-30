package org.protege.editor.owl.model.hierarchy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;


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


    @Override
    protected Set<OWLObjectProperty> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes) {
        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
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
    @Override
    protected Set<? extends OWLObjectProperty> getReferencedProperties(OWLOntology ont) {
        return ont.getObjectPropertiesInSignature();
    }


    @Override
    protected Set<? extends OWLSubPropertyAxiom> getSubPropertyAxiomForRHS(OWLObjectProperty prop, OWLOntology ont) {
        return ont.getObjectSubPropertyAxiomsForSuperProperty(prop);
    }


    @Override
    protected OWLObjectProperty getRoot() {
        return getManager().getOWLDataFactory().getOWLTopObjectProperty();
    }


    @Override
    protected boolean containsReference(OWLOntology ont, OWLObjectProperty prop) {
        return ont.containsObjectPropertyInSignature(prop.getIRI(),
                Imports.EXCLUDED);
    }
}
