package org.protege.editor.owl.model.refactor.ontology;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyMerger {

    private static final Logger logger = Logger.getLogger(OntologyMerger.class);

    private OWLOntologyManager owlOntologyManager;

    private Set<OWLOntology> ontologies;

    private OWLOntology targetOntology;


    public OntologyMerger(OWLOntologyManager owlOntologyManager, Set<OWLOntology> ontologies, OWLOntology targetOntology) {
        this.ontologies = new HashSet<OWLOntology>(ontologies);
        this.owlOntologyManager = owlOntologyManager;
        this.targetOntology = targetOntology;
    }


    public void mergeOntologies() {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        MoveAxiomFilter filter = new MoveAxiomFilter();
        for (OWLOntology ont : ontologies) {
            if (!ont.equals(targetOntology)){
                for (OWLAxiom ax : ont.getAxioms()) {
                    ax = filter.moveAxiom(ax);
                    if (ax != null){
                        changes.add(new AddAxiom(targetOntology, ax));
                    }
                }
            }
        }
        try {
            owlOntologyManager.applyChanges(changes);
        }
        catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        }
    }


    class MoveAxiomFilter extends OWLAxiomVisitorAdapter {

        private OWLAxiom ax;

        public OWLAxiom moveAxiom(OWLAxiom visitedAxiom) {
            this.ax = visitedAxiom;
            visitedAxiom.accept(this);
            return ax;
        }


        public void visit(OWLImportsDeclaration owlImportsDeclaration) {
            if (owlImportsDeclaration.getURI().equals(targetOntology.getURI())){
                ax = null;
                logger.warn("Merge: ignoring import declaration for URI " + targetOntology.getURI() +
                            " (would result in targetOntology importing itself).");
            }
        }


// @@TODO v3 port
//        // all targetOntology annotations should be asserted on the target
//        public void visit(OWLOntologyAnnotationAxiom owlOntologyAnnotationAxiom) {
//            if (!owlOntologyAnnotationAxiom.getSubject().equals(targetOntology)){
//                ax = owlOntologyManager.getOWLDataFactory().getOWLOntologyAnnotationAxiom(targetOntology, owlOntologyAnnotationAxiom.getAnnotation());
//            }
//        }
    }
}
