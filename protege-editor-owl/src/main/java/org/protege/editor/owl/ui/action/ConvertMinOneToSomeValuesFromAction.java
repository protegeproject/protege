package org.protege.editor.owl.ui.action;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;
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
        MinCardiOneReplacer replacer = new MinCardiOneReplacer(getOWLModelManager().getOWLOntologyManager());
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
     * A variant of the duplicator that changes qualified MinCardi1
     * restrictions into someValueFrom restrictions
     */
    class MinCardiOneReplacer extends OWLObjectDuplicator{

        public MinCardiOneReplacer(OWLOntologyManager manager) {
            super(manager);
        }


        public OWLObjectMinCardinality visit(OWLObjectMinCardinality min) {
            if (min.getCardinality() == 1 && min.isQualified()){
                OWLObjectSomeValuesFrom someValuesFrom =
                        getOWLDataFactory().getOWLObjectSomeValuesFrom(min.getProperty(), min.getFiller());
                return (OWLObjectMinCardinality) visit(someValuesFrom);
            }
            else{
                return super.visit(min);
            }
        }


        public OWLDataMinCardinality visit(OWLDataMinCardinality min) {
            if (min.getCardinality() == 1 && min.isQualified()){
                OWLDataSomeValuesFrom someValuesFrom =
                        getOWLDataFactory().getOWLDataSomeValuesFrom(min.getProperty(), min.getFiller());
                return (OWLDataMinCardinality) visit(someValuesFrom);
            }
            else{
            	return super.visit(min);
            }
        }
    }
}
