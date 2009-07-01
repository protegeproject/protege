package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;

import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Author: Nick Drummond<br>
 * The University Of Manchester<br>
 * BioHealth Informatics Group<br>
 * Date: May 19, 2008
 */
public class AmalgamateDisjointClassesAction extends ProtegeOWLAction {

    Logger logger = Logger.getLogger(AmalgamateDisjointClassesAction.class);


    public void actionPerformed(ActionEvent actionEvent) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        int axiomsRemoved = 0;
        int axiomsAdded = 0;
        int numberOfDisjoints = 0;

        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()){

            // act on each ontology in turn
            CliqueFinder<OWLClassExpression> merger = new CliqueFinder<OWLClassExpression>();

            Set<OWLDisjointClassesAxiom> oldAxioms = ont.getAxioms(AxiomType.DISJOINT_CLASSES);
            numberOfDisjoints += oldAxioms.size();
            for (OWLDisjointClassesAxiom ax : oldAxioms){
                merger.add(ax.getClassExpressions());
            }

            for (Set<OWLClassExpression> newAxioms : merger.getResults()){
                OWLDisjointClassesAxiom newAxiom = getOWLModelManager().getOWLDataFactory().getOWLDisjointClassesAxiom(newAxioms);
                if (oldAxioms.contains(newAxiom)){
                    oldAxioms.remove(newAxiom);
                }
                else{
                    changes.add(new AddAxiom(ont, newAxiom));
                    axiomsAdded++;
                }
            }

            for (OWLDisjointClassesAxiom oldAxiom : oldAxioms){
                changes.add(new RemoveAxiom(ont, oldAxiom));
                axiomsRemoved++;
            }
        }
        getOWLModelManager().applyChanges(changes);
        logger.info(axiomsRemoved + " (of " + numberOfDisjoints  + " total) disjoint class axioms replaced with " + axiomsAdded);
    }


    public void initialise() throws Exception {
        // do nothing
    }

    public void dispose() throws Exception {
        // do nothing
    }


    /**
     * A clique is a complete subgraph - that is, one in which all the vertices are connect to each other.
     * Given a set of existing cliques, this class generates a more optimal set
     * eg pairwise disjoints can be turned into disjoint sets
     */
    static class CliqueFinder<O> {

        private Map<O, Set<O>> edgesByVertex = new HashMap<O, Set<O>>();

        private Set<Set<O>> originalCliques = new HashSet<Set<O>>();

        private Set<Set<O>> resultCliques;

        public void add(Set<O> clique) {
            resultCliques = null;
            final Set<O> unmodClique = Collections.unmodifiableSet(clique);
            originalCliques.add(unmodClique);
            List<O> orderedOperands = new ArrayList<O>(clique);
            for (int i=0; i<orderedOperands.size(); i++){
                O a = orderedOperands.get(i);
                for (int j=i+1; j<orderedOperands.size(); j++){
                    O b = orderedOperands.get(j);
                    addEdge(a, b);
                    addEdge(b, a);
                }
            }
        }

        public void clear(){
            resultCliques = null;
            originalCliques.clear();
            edgesByVertex.clear();
        }

        public Set<Set<O>> getResults() {
            if (resultCliques == null){
                resultCliques = new HashSet<Set<O>>();
                Set<Integer> skip = new HashSet<Integer>();

                List<Set<O>> workingCliques = new ArrayList<Set<O>>(originalCliques);
                for (int i=0; i<workingCliques.size(); i++){
                    if (!skip.contains(i)){
                        Set<O> g1 = new HashSet<O>(workingCliques.get(i));
                        for (int j=i+1; j<workingCliques.size(); j++){
                            if (!skip.contains(j)){
                                Set<O> g2 = workingCliques.get(j);
                                if (canMerge(g1, g2)){
                                    g1.addAll(g2);
                                    skip.add(j);
                                }
                            }
                        }
                        resultCliques.add(g1);
                    }
                }
            }
            return resultCliques;
        }


        private void addEdge(O d1, O d2){
            Set<O> values = edgesByVertex.get(d1);
            if (values == null){
                values = new HashSet<O>();
                edgesByVertex.put(d1, values);
            }
            values.add(d2);
        }


        /**
         * @return true if the two subgraphs can be merged because they form a complete graph
         */
        private boolean canMerge(Set<O> g1, Set<O> g2){
            for (O vertexInG2 : g2){
                if (!g1.contains(vertexInG2)){
                    for (O vertexInG1 : g1){
                        if (!isEdge(vertexInG2, vertexInG1)){
                            return false; // found a vertex in g2 that is not adjacent to a vertex in g1
                        }
                    }
                }
            }
            return true;
        }

        private boolean isEdge(O v1, O v2){
            return edgesByVertex.get(v1).contains(v2);
        }
    }


    public static void main(String[] args) {
        CliqueFinder<String> finder = new CliqueFinder<String>();
        finder.add(new HashSet<String>(Arrays.asList("A", "B")));
        finder.add(new HashSet<String>(Arrays.asList("B", "C")));
        finder.add(new HashSet<String>(Arrays.asList("C", "D")));
        finder.add(new HashSet<String>(Arrays.asList("A", "D")));
        finder.add(new HashSet<String>(Arrays.asList("B", "D")));

        Set<Set<String>> results = finder.getResults();
        assert(results.size() == 2);
        assert(results.contains(new HashSet<String>(Arrays.asList("C", "B", "D"))));
        assert(results.contains(new HashSet<String>(Arrays.asList("A", "B", "D"))));


        finder.clear();
        finder.add(new HashSet<String>(Arrays.asList("A", "X")));
        finder.add(new HashSet<String>(Arrays.asList("B", "X")));
        finder.add(new HashSet<String>(Arrays.asList("Y", "A")));
        finder.add(new HashSet<String>(Arrays.asList("B", "D", "A")));
        finder.add(new HashSet<String>(Arrays.asList("C", "B")));

        results = finder.getResults();
        assert(results.size() == 4);
        assert(results.contains(new HashSet<String>(Arrays.asList("D", "A", "B"))));
        assert(results.contains(new HashSet<String>(Arrays.asList("A", "B", "X"))));
        assert(results.contains(new HashSet<String>(Arrays.asList("A", "Y"))));
        assert(results.contains(new HashSet<String>(Arrays.asList("C", "B"))));


        finder.clear();
        finder.add(new HashSet<String>(Arrays.asList("A", "B", "C")));
        finder.add(new HashSet<String>(Arrays.asList("X", "Y", "Z")));
        finder.add(new HashSet<String>(Arrays.asList("X", "A")));
        finder.add(new HashSet<String>(Arrays.asList("X", "C")));

        for (Set<String> result : finder.getResults()){
            System.out.print("<");
            for (String s : result){
                System.out.print(s + " ");
            }
            System.out.println(">");
        }

    }
}