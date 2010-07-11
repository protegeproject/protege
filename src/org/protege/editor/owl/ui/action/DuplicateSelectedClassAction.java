package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private Preferences prefs;

    private static final String DUPLICATE_ANNOTATIONS_KEY = "DUPLICATE_ANNOTATIONS_KEY";

    private static final String DUPLICATE_INTO_ACTIVE_ONTOLOGY_KEY = "DUPLICATE_INTO_ACTIVE_ONTOLOGY_KEY";


    protected void initialiseAction() throws Exception {
    }


    public void actionPerformed(ActionEvent e) {
        OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();

        if (selectedClass != null){

            prefs = PreferencesManager.getInstance().getApplicationPreferences(DuplicateSelectedClassAction.class);

            DuplicateClassPreferencesPanel panel = new DuplicateClassPreferencesPanel(selectedClass);
            UIHelper uiHelper = new UIHelper(getOWLEditorKit());

            if (uiHelper.showValidatingDialog("Duplicate Class",
                                              panel,
                                              panel.getFocusComponent()) == JOptionPane.OK_OPTION){

                panel.saveSettings();

                OWLEntityCreationSet<OWLClass> set = panel.createOWLClass();
                if (set != null){
                    Map<IRI, IRI> replacementIRIMap = new HashMap<IRI, IRI>();
                    replacementIRIMap.put(selectedClass.getIRI(), set.getOWLEntity().getIRI());
                    OWLModelManager mngr = getOWLModelManager();
                    OWLObjectDuplicator dup = new OWLObjectDuplicator(mngr.getOWLDataFactory(), replacementIRIMap);
                    List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>(set.getOntologyChanges());

                    changes.addAll(duplicateClassAxioms(selectedClass, dup));

                    if (prefs.getBoolean(DUPLICATE_ANNOTATIONS_KEY, false)){
                        changes.addAll(duplicateAnnotations(selectedClass, dup));
                    }

                    mngr.applyChanges(changes);
                    getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(set.getOWLEntity());
                }
            }
        }
    }

    private List<OWLOntologyChange> duplicateClassAxioms(OWLClass selectedClass, OWLObjectDuplicator dup) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        boolean useActiveOnt = prefs.getBoolean(DUPLICATE_INTO_ACTIVE_ONTOLOGY_KEY, false);

        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLAxiom ax : ont.getAxioms(selectedClass)) {
                if (ax.isLogicalAxiom() && !(ax instanceof OWLDisjointClassesAxiom)) {
                    OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
                    changes.add(new AddAxiom(useActiveOnt ? getOWLModelManager().getActiveOntology() : ont, duplicatedAxiom));
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
        List<IRI> annotIRIs = null;
        String selectedClassName = null;
        if (ren instanceof OWLEntityAnnotationValueRenderer){
            selectedClassName = getOWLModelManager().getRendering(selectedClass);
            annotIRIs = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        }

        LiteralExtractor literalExtractor = new LiteralExtractor();

        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            for (OWLAnnotationAssertionAxiom ax : selectedClass.getAnnotationAssertionAxioms(ont)){
                final OWLAnnotation annot = ax.getAnnotation();
                if (annotIRIs == null || !annotIRIs.contains(annot.getProperty().getIRI())){

                    String label = literalExtractor.getLiteral(annot.getValue());
                    if (label == null || !label.equals(selectedClassName)){
                        OWLAxiom duplicatedAxiom = dup.duplicateObject(ax);
                        changes.add(new AddAxiom(ont, duplicatedAxiom));
                    }
                }
            }
        }
        return changes;
    }


    class LiteralExtractor implements OWLAnnotationValueVisitor {

        private String label;

        public String getLiteral(OWLAnnotationValue value){
            label = null;
            value.accept(this);
            return label;
        }

        public void visit(IRI iri) {
            // do nothing
        }


        public void visit(OWLAnonymousIndividual owlAnonymousIndividual) {
            // do nothing
        }


        public void visit(OWLLiteral literal) {
            label = literal.getLiteral();
        }
    }


    class DuplicateClassPreferencesPanel extends JComponent implements VerifiedInputEditor {

        private JCheckBox duplicateAnnotationsCheckbox;

        private JRadioButton activeOntologyButton;

        private JRadioButton originalOntologyButton;

        private OWLEntityCreationPanel<OWLClass> entityNamePanel;


        DuplicateClassPreferencesPanel(OWLClass selectedClass) {
            setLayout(new BorderLayout(6, 6));

            entityNamePanel = new OWLEntityCreationPanel<OWLClass>(getOWLEditorKit(), "Class name", OWLClass.class);
            entityNamePanel.setName(getOWLModelManager().getRendering(selectedClass));

            final boolean duplicateIntoActiveOnt = prefs.getBoolean(DUPLICATE_INTO_ACTIVE_ONTOLOGY_KEY, false);

            activeOntologyButton = new JRadioButton("active ontology", duplicateIntoActiveOnt);
            originalOntologyButton = new JRadioButton("original ontology(ies)", !duplicateIntoActiveOnt);

            ButtonGroup group = new ButtonGroup();
            group.add(activeOntologyButton);
            group.add(originalOntologyButton);

            Box box = new Box(BoxLayout.PAGE_AXIS);
            box.add(activeOntologyButton);
            box.add(originalOntologyButton);

            JComponent locationPanel = new JPanel(new BorderLayout(6, 6));
            locationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            locationPanel.add(new JLabel("Where you would like to duplicate the class?"), BorderLayout.NORTH);
            locationPanel.add(box, BorderLayout.CENTER);

            duplicateAnnotationsCheckbox = new JCheckBox("Duplicate annotations");
            duplicateAnnotationsCheckbox.setSelected(prefs.getBoolean(DUPLICATE_ANNOTATIONS_KEY, true));

            JComponent annotationPanel = new JPanel(new BorderLayout(6, 6));
            annotationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            annotationPanel.add(duplicateAnnotationsCheckbox, BorderLayout.WEST);

            add(entityNamePanel, BorderLayout.NORTH);
            add(locationPanel, BorderLayout.CENTER);
            add(annotationPanel, BorderLayout.SOUTH);
        }


        public OWLEntityCreationSet<OWLClass> createOWLClass(){
            return entityNamePanel.getOWLEntityCreationSet();
        }


        public void saveSettings() {
            prefs.putBoolean(DUPLICATE_ANNOTATIONS_KEY, duplicateAnnotationsCheckbox.isSelected());
            prefs.putBoolean(DUPLICATE_INTO_ACTIVE_ONTOLOGY_KEY, activeOntologyButton.isSelected());
        }


        public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
            entityNamePanel.addStatusChangedListener(listener);
        }


        public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
            entityNamePanel.removeStatusChangedListener(listener);
        }


        public JComponent getFocusComponent() {
            return entityNamePanel.getFocusComponent();
        }
    }
}
