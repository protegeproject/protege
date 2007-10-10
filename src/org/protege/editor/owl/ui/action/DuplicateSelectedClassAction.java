package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.util.OWLObjectDuplicator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DuplicateSelectedClassAction extends SelectedOWLClassAction {

    private static final Logger logger = Logger.getLogger(DuplicateSelectedClassAction.class);


    protected void initialiseAction() throws Exception {
    }


    public void actionPerformed(ActionEvent e) {
        OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        Map<URI, URI> replacementURIMap = new HashMap<URI, URI>();
        replacementURIMap.put(selectedClass.getURI(), set.getOWLEntity().getURI());
        OWLObjectDuplicator dup = new OWLObjectDuplicator(getOWLModelManager().getOWLDataFactory(), replacementURIMap);
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(set.getOntologyChanges());
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLAxiom ax : ont.getAxioms(selectedClass)) {
                if (ax.isLogicalAxiom() && !(ax instanceof OWLDisjointClassesAxiom)) {
                    OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
                    changes.add(new AddAxiom(ont, duplicatedAxiom));
                }
            }
        }

        getOWLModelManager().applyChanges(changes);
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(set.getOWLEntity());
    }
}
