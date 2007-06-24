package org.protege.editor.owl.model.hierarchy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyHierarchyProvider extends AbstractOWLPropertyHierarchyProvider<OWLDataPropertyExpression, OWLDataProperty> {

    public OWLDataPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
    }


    protected Set<OWLDataProperty> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes) {
        Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                for (OWLEntity entity : ((OWLAxiomChange) change).getEntities()) {
                    if (entity instanceof OWLDataProperty) {
                        result.add((OWLDataProperty) entity);
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
        return ont.getReferencedDataProperties();
    }


    protected boolean containsReference(OWLOntology ont, OWLDataProperty prop) {
        return ont.containsDataPropertyReference(prop.getURI());
    }


    protected Set<? extends OWLSubPropertyAxiom<OWLDataPropertyExpression>> getSubPropertyAxiomForRHS(
            OWLDataProperty prop, OWLOntology ont) {
        return ont.getDataSubPropertyAxiomsForRHS(prop);
    }
}
