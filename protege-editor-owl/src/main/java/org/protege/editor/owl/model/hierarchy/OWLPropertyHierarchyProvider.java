package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 10/03/15
 */
public class OWLPropertyHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLEntity> {

    private OWLObjectHierarchyProvider<OWLObjectProperty> objectPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLDataProperty> dataPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLAnnotationProperty> annotationPropertyHierarchyProvider;

    public OWLPropertyHierarchyProvider(
            OWLOntologyManager owlOntologyManager,
            OWLObjectHierarchyProvider<OWLObjectProperty> objectPropertyHierarchyProvider,
            OWLObjectHierarchyProvider<OWLDataProperty> dataPropertyHierarchyProvider,
            OWLObjectHierarchyProvider<OWLAnnotationProperty> annotationPropertyHierarchyProvider
            ) {
        super(owlOntologyManager);
        this.annotationPropertyHierarchyProvider = annotationPropertyHierarchyProvider;
        this.dataPropertyHierarchyProvider = dataPropertyHierarchyProvider;
        this.objectPropertyHierarchyProvider = objectPropertyHierarchyProvider;

        annotationPropertyHierarchyProvider.addListener(new OWLObjectHierarchyProviderListener<OWLAnnotationProperty>() {
            @Override
            public void nodeChanged(OWLAnnotationProperty node) {
                OWLPropertyHierarchyProvider.this.fireNodeChanged(node);
            }

            @Override
            public void hierarchyChanged() {
                OWLPropertyHierarchyProvider.this.fireHierarchyChanged();
            }
        });
        dataPropertyHierarchyProvider.addListener(new OWLObjectHierarchyProviderListener<OWLDataProperty>() {
            @Override
            public void nodeChanged(OWLDataProperty node) {
                OWLPropertyHierarchyProvider.this.fireNodeChanged(node);
            }

            @Override
            public void hierarchyChanged() {
                OWLPropertyHierarchyProvider.this.fireHierarchyChanged();
            }
        });
        objectPropertyHierarchyProvider.addListener(new OWLObjectHierarchyProviderListener<OWLObjectProperty>() {
            @Override
            public void nodeChanged(OWLObjectProperty node) {
                OWLPropertyHierarchyProvider.this.fireNodeChanged(node);
            }

            @Override
            public void hierarchyChanged() {
                OWLPropertyHierarchyProvider.this.fireHierarchyChanged();
            }
        });
    }

    @Override
    public boolean containsReference(OWLEntity object) {
        return object.accept(new OWLObjectVisitorExAdapter<Boolean>() {
            @Override
            public Boolean visit(OWLAnnotationProperty property) {
                return annotationPropertyHierarchyProvider.containsReference(property);
            }

            @Override
            public Boolean visit(OWLDataProperty property) {
                return dataPropertyHierarchyProvider.containsReference(property);
            }

            @Override
            public Boolean visit(OWLObjectProperty property) {
                return objectPropertyHierarchyProvider.containsReference(property);
            }
        });
    }

    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     *
     * @param ontologies
     */
    @Override
    public void setOntologies(Set<OWLOntology> ontologies) {
        annotationPropertyHierarchyProvider.setOntologies(ontologies);
        dataPropertyHierarchyProvider.setOntologies(ontologies);
        objectPropertyHierarchyProvider.setOntologies(ontologies);
    }

    /**
     * Gets the objects that represent the roots of the hierarchy.
     */
    @Override
    public Set<OWLEntity> getRoots() {
        LinkedHashSet<OWLEntity> roots = new LinkedHashSet<>();
        roots.addAll(objectPropertyHierarchyProvider.getRoots());
        roots.addAll(dataPropertyHierarchyProvider.getRoots());
        roots.addAll(annotationPropertyHierarchyProvider.getRoots());
        return roots;
    }

    @Override
    public Set<OWLEntity> getChildren(OWLEntity object) {
        Set<? extends OWLObject> result = object.accept(new OWLObjectVisitorExAdapter<Set<? extends OWLEntity>>() {
            @Override
            public Set<? extends OWLEntity> visit(OWLAnnotationProperty property) {
                return annotationPropertyHierarchyProvider.getChildren(property);
            }

            @Override
            public Set<? extends OWLEntity> visit(OWLDataProperty property) {
                return dataPropertyHierarchyProvider.getChildren(property);
            }

            @Override
            public Set<? extends OWLEntity> visit(OWLObjectProperty property) {
                return objectPropertyHierarchyProvider.getChildren(property);
            }
        });
        return (Set<OWLEntity>)result;
    }

    @Override
    public Set<OWLEntity> getParents(OWLEntity object) {
        Set<? extends OWLEntity> result = object.accept(new OWLObjectVisitorExAdapter<Set<? extends OWLEntity>>() {
            @Override
            public Set<? extends OWLEntity> visit(OWLAnnotationProperty property) {
                return annotationPropertyHierarchyProvider.getParents(property);
            }

            @Override
            public Set<? extends OWLEntity> visit(OWLDataProperty property) {
                return dataPropertyHierarchyProvider.getParents(property);
            }

            @Override
            public Set<? extends OWLEntity> visit(OWLObjectProperty property) {
                return objectPropertyHierarchyProvider.getParents(property);
            }
        });
        return (Set<OWLEntity>)result;
    }

    @Override
    public Set<OWLEntity> getEquivalents(OWLEntity object) {
        Set<? extends OWLEntity> result = object.accept(new OWLObjectVisitorExAdapter<Set<? extends OWLEntity>>() {
            @Override
            public Set<? extends OWLEntity> visit(OWLAnnotationProperty property) {
                return annotationPropertyHierarchyProvider.getEquivalents(property);
            }

            @Override
            public Set<? extends OWLEntity> visit(OWLDataProperty property) {
                return dataPropertyHierarchyProvider.getEquivalents(property);
            }

            @Override
            public Set<? extends OWLEntity> visit(OWLObjectProperty property) {
                return objectPropertyHierarchyProvider.getEquivalents(property);
            }
        });
        return (Set<OWLEntity>)result;
    }
}
