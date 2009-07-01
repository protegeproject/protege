package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

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

    Logger logger = Logger.getLogger(ConvertMinOneToSomeValuesFromAction.class);


    public void actionPerformed(ActionEvent actionEvent) {
        MinCardiOneReplacer replacer = new MinCardiOneReplacer(getOWLModelManager().getOWLDataFactory());
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
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

        public MinCardiOneReplacer(OWLDataFactory owlDataFactory) {
            super(owlDataFactory);
        }


        public void visit(OWLObjectMinCardinality min) {
            if (min.getCardinality() == 1 && min.isQualified()){
                OWLObjectSomeValuesFrom someValuesFrom =
                        getOWLDataFactory().getOWLObjectSomeValuesFrom(min.getProperty(), min.getFiller());
                visit(someValuesFrom);
            }
            else{
                super.visit(min);
            }
        }


        public void visit(OWLDataMinCardinality min) {
            if (min.getCardinality() == 1 && min.isQualified()){
                OWLDataSomeValuesFrom someValuesFrom =
                        getOWLDataFactory().getOWLDataSomeValuesFrom(min.getProperty(), min.getFiller());
                visit(someValuesFrom);
            }
            else{
                super.visit(min);
            }
        }
    }
}
