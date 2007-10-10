package org.protege.editor.owl.ui.debugging;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.semanticweb.owl.debugging.JustificationMap;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Mar-2007<br><br>
 */
public class JustificationHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLAxiom> {

    private OWLDescription description;

    private Set<OWLAxiom> rootAxioms;

    private JustificationMap map;


    public JustificationHierarchyProvider(OWLOntologyManager owlOntologyManager, OWLDescription description,
                                          Set<OWLAxiom> axioms) {
        super(owlOntologyManager);
        this.description = description;
        rootAxioms = new HashSet<OWLAxiom>();
        map = new JustificationMap(description, axioms);
        rootAxioms.addAll(map.getRootAxioms());
    }


    public boolean containsReference(OWLAxiom object) {
        return false;
    }


    public Set<OWLAxiom> getChildren(OWLAxiom object) {
        return map.getChildAxioms(object);
    }


    public Set<OWLAxiom> getEquivalents(OWLAxiom object) {
        return Collections.emptySet();
    }


    public Set<OWLAxiom> getParents(OWLAxiom object) {
        return Collections.emptySet();
    }


    public Set<OWLAxiom> getRoots() {
        return rootAxioms;
    }


    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
