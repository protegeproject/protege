package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 23, 2009<br><br>
 */
public class OWLAnnotationPropertyHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLAnnotationProperty> {

//    private static final Logger logger = Logger.getLogger(OWLAnnotationPropertyHierarchyProvider.class);

    private Set<OWLOntology> ontologies;

    private Set<OWLAnnotationProperty> roots;

    private OWLOntologyChangeListener listener;


    public OWLAnnotationPropertyHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.roots = new HashSet<OWLAnnotationProperty>();
        ontologies = new HashSet<OWLOntology>();
        listener = new OWLOntologyChangeListener() {
            /**
             * Called when some changes have been applied to various ontologies.  These
             * may be an axiom added or an axiom removed changes.
             * @param changes A list of changes that have occurred.  Each change may be examined
             *                to determine which ontology it was applied to.
             */
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleChanges(changes);
            }
        };
        owlOntologyManager.addOntologyChangeListener(listener);
    }

    public Set<OWLAnnotationProperty> getRoots() {
        return Collections.unmodifiableSet(roots);
    }


    final public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies.clear();
        this.ontologies.addAll(ontologies);
        rebuildRoots();
        fireHierarchyChanged();
    }


    public boolean containsReference(OWLAnnotationProperty object) {
        for (OWLOntology ont : ontologies) {
            if (ont.getAnnotationPropertiesInSignature().contains(object)) {
                return true;
            }
        }
        return false;
    }


    public Set<OWLAnnotationProperty> getChildren(OWLAnnotationProperty object) {
        Set<OWLAnnotationProperty> result = new HashSet<OWLAnnotationProperty>();
        for (OWLOntology ont : ontologies) {
            for (OWLSubAnnotationPropertyOfAxiom ax : ont.getAxioms(AxiomType.SUB_ANNOTATION_PROPERTY_OF)) {
                if (ax.getSuperProperty().equals(object)){
                    OWLAnnotationProperty subProp = ax.getSubProperty();
                    // prevent cycles
                    if (!getAncestors(subProp).contains(subProp)) {
                        result.add(subProp);
                    }
                }
            }
        }
        return result;
    }


    public Set<OWLAnnotationProperty> getEquivalents(OWLAnnotationProperty object) {
        Set<OWLAnnotationProperty> result = new HashSet<OWLAnnotationProperty>();
        Set<OWLAnnotationProperty> ancestors = getAncestors(object);
        if (ancestors.contains(object)) {
            for (OWLAnnotationProperty anc : ancestors) {
                if (getAncestors(anc).contains(object)) {
                    result.add(anc);
                }
            }
        }
        result.remove(object);
        return result;
    }


    public Set<OWLAnnotationProperty> getParents(OWLAnnotationProperty object) {
        Set<OWLAnnotationProperty> result = new HashSet<OWLAnnotationProperty>();

        for (OWLOntology ont : ontologies) {
            for (OWLSubAnnotationPropertyOfAxiom ax : ont.getSubAnnotationPropertyOfAxioms(object)){
                if (ax.getSubProperty().equals(object)){
                    OWLAnnotationProperty superProp = ax.getSuperProperty();
                    result.add(superProp);
                }
            }
        }
        return result;
    }


    public void dispose() {
        super.dispose();
        getManager().removeOntologyChangeListener(listener);
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<OWLAnnotationProperty> properties = new HashSet<OWLAnnotationProperty>(getPropertiesReferencedInChange(changes));
        for (OWLAnnotationProperty prop : properties) {
            if (isRoot(prop)) {
                roots.add(prop);
            }
            else {
                if (getAncestors(prop).contains(prop)) {
                    roots.add(prop);
                    for (OWLAnnotationProperty anc : getAncestors(prop)) {
                        if (getAncestors(anc).contains(prop)) {
                            roots.add(anc);
                            fireNodeChanged(anc);
                        }
                    }
                }
                else {
                    roots.remove(prop);
                }
            }
            fireNodeChanged(prop);
        }
    }


    private Set<OWLAnnotationProperty> getPropertiesReferencedInChange(List<? extends OWLOntologyChange> changes){
        final Set<OWLAnnotationProperty> props = new HashSet<OWLAnnotationProperty>();
        for (OWLOntologyChange chg : changes){
            if(chg.isAxiomChange()){
                chg.getAxiom().accept(new OWLAxiomVisitorAdapter(){
                    public void visit(OWLSubAnnotationPropertyOfAxiom owlSubAnnotationPropertyOfAxiom) {
                        props.add(owlSubAnnotationPropertyOfAxiom.getSubProperty());
                        props.add(owlSubAnnotationPropertyOfAxiom.getSuperProperty());
                    }

                    public void visit(OWLDeclarationAxiom owlDeclarationAxiom) {
                        if (owlDeclarationAxiom.getEntity().isOWLAnnotationProperty()){
                            props.add(owlDeclarationAxiom.getEntity().asOWLAnnotationProperty());
                        }
                    }
                });
            }
        }
        return props;
    }


    private boolean isRoot(OWLAnnotationProperty prop) {

        // We deem a property to be a root property if it doesn't have
        // any super properties (i.e. it is not on
        // the LHS of a subproperty axiom
        // Assume the property is a root property to begin with
        boolean isRoot = getParents(prop).isEmpty();
        if (isRoot && containsReference(prop)) {
            return true;
        }
        else {
            // Additional condition: If we have  P -> Q and Q -> P, then
            // there is no path to the root, so put P and Q as root properties
            // Collapse any cycles and force properties that are equivalent
            // through cycles to appear at the root.
            return getAncestors(prop).contains(prop);
        }
    }


    private void rebuildRoots() {
        roots.clear();

        final OWLDataFactory df = getManager().getOWLDataFactory();

        final Set<OWLAnnotationProperty> annotationProperties = new HashSet<OWLAnnotationProperty>();

        for (OWLOntology ont : ontologies) {
            annotationProperties.addAll(ont.getAnnotationPropertiesInSignature());
        }
        for (OWLAnnotationProperty prop : annotationProperties) {
            if (isRoot(prop)) {
                roots.add(prop);
            }
        }

        for (IRI uri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTY_IRIS){
            roots.add(df.getOWLAnnotationProperty(uri));
        }
    }
}
