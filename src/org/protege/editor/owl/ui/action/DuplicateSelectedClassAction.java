package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectDuplicator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        OWLModelManager mngr = getOWLModelManager();
        OWLObjectDuplicator dup = new OWLObjectDuplicator(mngr.getOWLDataFactory(), replacementURIMap);
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        changes.addAll(set.getOntologyChanges());

        changes.addAll(duplicateClassAxioms(selectedClass, dup));

        UIHelper uiHelper = new UIHelper(getOWLEditorKit());
        if (uiHelper.showOptionPane("Duplicate Class",
                "Would you like to duplicate annotations?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            changes.addAll(duplicateAnnotations(selectedClass, dup));
        }

        mngr.applyChanges(changes);
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(set.getOWLEntity());
    }

    private List<OWLOntologyChange> duplicateClassAxioms(OWLClass selectedClass, OWLObjectDuplicator dup) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLAxiom ax : ont.getAxioms(selectedClass)) {
                if (ax.isLogicalAxiom() && !(ax instanceof OWLDisjointClassesAxiom)) {
                    OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
                    changes.add(new AddAxiom(ont, duplicatedAxiom));
                }
            }
        }
        return changes;
    }

    /**
     * Duplicates all annotations on the class except those that are being used
     * as a label to render the classes (to avoid name duplication).
     * These are determined by matching the value to the name of the selected
     * class and the URI to one of those being used by the annot renderer
     * @param selectedClass the class to duplicate
     * @param dup the OWL Object duplicator
     * @return a list of changes
     */
    private List<OWLOntologyChange> duplicateAnnotations(OWLClass selectedClass, OWLObjectDuplicator dup) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        OWLModelManagerEntityRenderer ren = getOWLModelManager().getOWLEntityRenderer();
        List<URI> annotURIs = null;
        String selectedClassName = null;
        if (ren instanceof OWLEntityAnnotationValueRenderer){
            selectedClassName = ren.render(selectedClass);
            annotURIs = OWLRendererPreferences.getInstance().getAnnotationURIs();
        }

        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLAnnotationAxiom ax : selectedClass.getAnnotationAxioms(ont)){
                final OWLAnnotation annot = ax.getAnnotation();
                if (annotURIs == null ||
                    !annotURIs.contains(annot.getAnnotationURI()) ||
                    !annot.getAnnotationValueAsConstant().getLiteral().equals(selectedClassName)){
                    OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
                    changes.add(new AddAxiom(ont, duplicatedAxiom));
                }
            }
        }
        return changes;
    }
}
