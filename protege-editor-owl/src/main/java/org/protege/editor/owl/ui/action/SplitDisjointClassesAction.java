package org.protege.editor.owl.ui.action;

import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Nick Drummond<br>
 * The University Of Manchester<br>
 * BioHealth Informatics Group<br>
 * Date: May 19, 2008
 */
public class SplitDisjointClassesAction extends ProtegeOWLAction {

    private final Logger logger = LoggerFactory.getLogger(SplitDisjointClassesAction.class);

    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent actionEvent) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        int axiomsRemoved = 0;
        int axiomsAdded = 0;
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()){
            for (OWLDisjointClassesAxiom ax : ont.getAxioms(AxiomType.DISJOINT_CLASSES)){
                Set<OWLDisjointClassesAxiom> pairwiseAxioms = split(ax);
                if (!pairwiseAxioms.isEmpty()){
                    changes.add(new RemoveAxiom(ont, ax));
                    axiomsRemoved++;
                    for (OWLDisjointClassesAxiom pairwiseAxiom : pairwiseAxioms){
                        changes.add(new AddAxiom(ont, pairwiseAxiom));
                        axiomsAdded++;
                    }
                }
            }
        }
        getOWLModelManager().applyChanges(changes);
        logger.info("Split " + axiomsRemoved + " disjointClasses axioms into " + axiomsAdded + " pairwise axioms");
    }


    public Set<OWLDisjointClassesAxiom> split(OWLDisjointClassesAxiom ax){
        Set<OWLDisjointClassesAxiom> pairwiseAxioms = new HashSet<>();
        if (ax.getClassExpressions().size() > 2){
            List<OWLClassExpression> orderedOperands = new ArrayList<>(ax.getClassExpressions());
            for (int i=0; i<orderedOperands.size(); i++){
                OWLClassExpression a = orderedOperands.get(i);
                for (int j=i+1; j<orderedOperands.size(); j++){
                    OWLClassExpression b = orderedOperands.get(j);
                    OWLDisjointClassesAxiom pairwiseAxiom = getOWLDataFactory().getOWLDisjointClassesAxiom(a, b);
                    pairwiseAxioms.add(pairwiseAxiom);
                }
            }
        }
        return pairwiseAxioms;
    }


    public void initialise() throws Exception {
        // do nothing
    }

    public void dispose() throws Exception {
        // do nothing
    }
}