package org.protege.editor.owl.model.hierarchy.tabbed;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.semanticweb.owlapi.model.*;

import java.util.*;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 12, 2008<br><br>
 */
public class OWLClassHierarchyCreator {

    private OWLDataFactory df;
    private OWLEntityFactory entityFactory;

    private OWLClass root;
    private List<Edge> edges;
    private OWLOntology ont;
    private boolean siblingsDisjoint;

    private Map<String, OWLClass> nameMap = new HashMap<>();
    private List<OWLOntologyChange> changes = new ArrayList<>();
    private Map<OWLClass, Set<OWLClass>> parent2ChildMap = new HashMap<>();


    public OWLClassHierarchyCreator(OWLDataFactory df, OWLEntityFactory fac, OWLClass rootClass, boolean makeSiblingClassesDisjoint, OWLOntology ontology, List<Edge> edges) {
        this.df = df;
        this.entityFactory = fac;

        this.root = rootClass;
        this.edges = edges;
        this.ont = ontology;
        this.siblingsDisjoint = makeSiblingClassesDisjoint;
    }


    public List<OWLOntologyChange> createHierarchy() {
        changes.clear();
        for (Edge edge : edges){
            handleEdge(edge);
        }
        if (siblingsDisjoint){
            handleDisjoints();
        }
        return changes;
    }


    private void handleDisjoints() {
        for (Set<OWLClass> clses : parent2ChildMap.values()) {
            if (clses.size() > 1) {
                changes.add(new AddAxiom(ont, df.getOWLDisjointClassesAxiom(clses)));
            }
        }
    }


    private void handleEdge(Edge edge) {
        OWLClass child = getOWLClass(edge.getChild());
        OWLClass parent = root;
        if (!edge.isRoot()){
            parent = getOWLClass(edge.getParent());
        }
        if (siblingsDisjoint){
            addToMap(parent, child);
        }
        changes.add(new AddAxiom(ont, df.getOWLSubClassOfAxiom(child, parent)));
    }


    protected OWLClass getOWLClass(String name){
        OWLClass cls = nameMap.get(name);
        if (cls == null){
            try {
                OWLEntityCreationSet<OWLClass> creationSet = entityFactory.createOWLClass(name, null);
                changes.addAll(creationSet.getOntologyChanges());
                cls = creationSet.getOWLEntity();
                nameMap.put(name, cls);
            }
            catch (OWLEntityCreationException e) {
                ErrorLogPanel.showErrorDialog(e);
            }
        }
        return cls;
    }

        private void addToMap(OWLClass parent, OWLClass child) {
        Set<OWLClass> children = parent2ChildMap.get(parent);
        if (children == null) {
            children = new HashSet<>();
            parent2ChildMap.put(parent, children);
        }
        children.add(child);
    }
}
