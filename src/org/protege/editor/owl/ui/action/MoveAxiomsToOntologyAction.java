package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsWizard;
import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class MoveAxiomsToOntologyAction extends ProtegeOWLAction {

    private Logger logger = Logger.getLogger(MoveAxiomsToOntologyAction.class);
    private OWLModelManagerListener l = new OWLModelManagerListener(){
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType().equals(EventType.ONTOLOGY_LOADED) ||
                    event.getType().equals(EventType.ONTOLOGY_CREATED)){
                setEnabled(getOWLModelManager().getOntologies().size() > 1);
            }
        }
    };

    public void actionPerformed(ActionEvent actionEvent) {
        MoveAxiomsWizard wiz = new MoveAxiomsWizard(getOWLEditorKit());

        if (wiz.showModalDialog() == Wizard.FINISH_RETURN_CODE){

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

            OWLOntology targetOnt = wiz.getTargetOntology();
            Set<? extends OWLAxiom> axioms = wiz.getAxioms();
            Set<OWLOntology> sourceOnts =   wiz.getSourceOntologies();

            long start = System.currentTimeMillis();
            logger.info("Moving " + axioms.size() + " axioms to " + targetOnt.getURI() + "...");

            int c = 0;
            for (OWLAxiom ax : axioms){
                c++;
                if (c % 1000 == 0){
                    logger.info(c + " axioms");
                }
                changes.add(new AddAxiom(targetOnt, ax));
                for (OWLOntology ont : sourceOnts){
                    if (ont.containsAxiom(ax)){
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                }
            }

            logger.info("...generated changes in " + (System.currentTimeMillis()-start) + "ms");

            start = System.currentTimeMillis();
            logger.info("Applying changes...");
            getOWLModelManager().applyChanges(changes);

            logger.info("...move axioms done! (applied changes in "+ (System.currentTimeMillis()-start) + "ms)");
        }
    }

//    public void actionPerformed(ActionEvent actionEvent) {
//        OWLClass cls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
//        if (cls != null){
//
//            UIHelper helper = new UIHelper(getOWLEditorKit());
//            OWLOntology targetOnt = helper.pickOWLOntology();
//
//            if (targetOnt != null){
//                List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
//
//                for (OWLOntology ont : getOWLModelManager().getActiveOntologies()){
//
//                    if (!ont.equals(targetOnt)){ // do not move any axioms that are already in the target ontology
//                        for (OWLAxiom ax : ont.getReferencingAxioms(cls)){
//                            changes.add(new RemoveAxiom(ont, ax));
//                            changes.add(new AddAxiom(targetOnt, ax));
//                        }
//                    }
//                }
//
//                logger.info("Moving " + (changes.size()/2) + " axioms to " + targetOnt.getURI() + "...");
//                getOWLModelManager().applyChanges(changes);
//            }
//        }
//    }

    public void initialise() throws Exception {
        getOWLModelManager().addListener(l);
        setEnabled(getOWLModelManager().getOntologies().size() > 1);        
    }

    public void dispose() throws Exception {
        getOWLModelManager().removeListener(l);
    }
}
