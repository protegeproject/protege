package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.semanticweb.owl.vocab.DublinCoreVocabulary;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.URI;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

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

    public Set<OWLAnnotationProperty> getSubPropertiesOfRoot() {
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
            if (ont.getReferencedAnnotationProperties().contains(object)) {
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
            annotationProperties.addAll(ont.getReferencedAnnotationProperties());
        }
        for (OWLAnnotationProperty prop : annotationProperties) {
            if (isRoot(prop)) {
                roots.add(prop);
            }
        }

        for (URI uri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTIES){
            roots.add(df.getOWLAnnotationProperty(uri));
        }
        for (URI uri : DublinCoreVocabulary.ALL_URIS){
            roots.add(df.getOWLAnnotationProperty(uri));
        }
    }
}
