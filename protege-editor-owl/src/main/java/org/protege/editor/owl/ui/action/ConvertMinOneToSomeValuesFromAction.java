package org.protege.editor.owl.ui.action;

import org.semanticweb.owlapi.change.AddAxiomData;
import org.semanticweb.owlapi.change.AxiomChangeData;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Nick Drummond<br>
 * The University Of Manchester<br>
 * BioHealth Informatics Group<br>
 * Date: May 19, 2008
 */
public class ConvertMinOneToSomeValuesFromAction extends ProtegeOWLAction {

    Logger logger = LoggerFactory.getLogger(ConvertMinOneToSomeValuesFromAction.class);


    public void actionPerformed(ActionEvent actionEvent) {
        MinCardiOneReplacer replacer = new MinCardiOneReplacer(getOWLModelManager().getOWLDataFactory());
        List<OWLOntologyChange> changes = new ArrayList<>();
        int count = 0;
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()){
            for (OWLAxiom ax : ont.getAxioms()){
                if (ax.isLogicalAxiom()){
                    // duplicates, but switching min 1 with svf
                    OWLAxiom ax2 = replacer.duplicateObject(ax);
                    // so if they are different, the axiom using the svf 
                    // needs to replace the axiom using the min 1 in the ontology
                    if (!ax.equals(ax2)){
                        changes.add(new RemoveAxiom(ont, ax));
                        changes.add(new AddAxiom(ont, ax2));
                        count++;
                    }
                }
            }
        }
        getOWLModelManager().applyChanges(changes);
        logger.info("Converted " + count + " qualified min 1 restrictions to someValuesFrom restrictions");
    }

    public void initialise() throws Exception {
        // do nothing
    }

    public void dispose() throws Exception {
        // do nothing
    }

    /**
     * Replaces qualified minimum-cardinality-one restrictions with
     * existential restrictions.
     */
    class MinCardiOneReplacer {

        private final OWLDataFactory dataFactory;

        private final OWLObjectTransformer<OWLClassExpression> transformer;

        public MinCardiOneReplacer(OWLDataFactory dataFactory) {
            this.dataFactory = dataFactory;
            transformer = new OWLObjectTransformer<>(
                    object -> true,
                    this::replace,
                    dataFactory,
                    OWLClassExpression.class);
        }

        public OWLAxiom duplicateObject(OWLAxiom axiom) {
            OWLAxiom result = axiom;
            while (true) {
                List<AxiomChangeData> changes = transformer.change(result);
                OWLAxiom replacement = changes.stream()
                        .filter(AddAxiomData.class::isInstance)
                        .map(AxiomChangeData::getAxiom)
                        .findFirst()
                        .orElse(null);
                if (replacement == null) {
                    return result;
                }
                result = replacement;
            }
        }

        private OWLClassExpression replace(OWLClassExpression expression) {
            if (expression instanceof OWLObjectMinCardinality) {
                OWLObjectMinCardinality min = (OWLObjectMinCardinality) expression;
                if (min.getCardinality() == 1 && min.isQualified()) {
                    return dataFactory.getOWLObjectSomeValuesFrom(min.getProperty(), min.getFiller());
                }
            }
            else if (expression instanceof OWLDataMinCardinality) {
                OWLDataMinCardinality min = (OWLDataMinCardinality) expression;
                if (min.getCardinality() == 1 && min.isQualified()) {
                    return dataFactory.getOWLDataSomeValuesFrom(min.getProperty(), min.getFiller());
                }
            }
            return expression;
        }
    }
}
