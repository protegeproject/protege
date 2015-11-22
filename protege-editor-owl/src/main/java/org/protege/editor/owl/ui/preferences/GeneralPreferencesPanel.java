package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.axiom.FreshAxiomLocation;
import org.protege.editor.owl.model.axiom.FreshAxiomLocationPreferences;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditorPreferences;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GeneralPreferencesPanel extends OWLPreferencesPanel {

    public static final String DIALOGS_ALWAYS_CENTRED = "DIALOGS_ALWAYS_CENTRED";
    
    private JSpinner checkDelaySpinner;

    private static final String SECOND_TOOL_TIP = "1000 = 1 second";

    private JCheckBox alwaysCentreDialogsCheckbox;

    private JCheckBox detachedWindowsFloat;

    private JRadioButton addFreshAxiomsToActiveOntologyRadioButton;

    private JRadioButton addFreshAxiomsToSubjectDefiningOntology;


    public void applyChanges() {
        ExpressionEditorPreferences.getInstance().setCheckDelay((Integer) checkDelaySpinner.getModel().getValue());

        Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        appPrefs.putBoolean(DIALOGS_ALWAYS_CENTRED, alwaysCentreDialogsCheckbox.isSelected());
        appPrefs.putBoolean(View.DETACHED_WINDOWS_FLOAT, detachedWindowsFloat.isSelected());

        FreshAxiomLocationPreferences axiomPrefs = FreshAxiomLocationPreferences.getPreferences();
        if(addFreshAxiomsToActiveOntologyRadioButton.isSelected()) {
            axiomPrefs.setFreshAxiomLocation(FreshAxiomLocation.ACTIVE_ONTOLOGY);
        }
        else if (addFreshAxiomsToSubjectDefiningOntology.isSelected()) {
            axiomPrefs.setFreshAxiomLocation(FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY);
        }
    }

    private void createUI() {
        setLayout(new BorderLayout());
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        add(panel, BorderLayout.NORTH);
        panel.addGroup("Editor delay");
        final int checkDelay = ExpressionEditorPreferences.getInstance().getCheckDelay();
        checkDelaySpinner = new JSpinner(new SpinnerNumberModel(checkDelay, 0, 10000, 50));
        checkDelaySpinner.setToolTipText(SECOND_TOOL_TIP);
        panel.addGroupComponent(checkDelaySpinner);

        Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        alwaysCentreDialogsCheckbox = new JCheckBox("Centre dialogs on workspace");
        alwaysCentreDialogsCheckbox.setSelected(appPrefs.getBoolean(DIALOGS_ALWAYS_CENTRED, false));
        detachedWindowsFloat = new JCheckBox("Detached windows float");
        detachedWindowsFloat.setSelected(appPrefs.getBoolean(View.DETACHED_WINDOWS_FLOAT, true));

        panel.addSeparator();
        panel.addGroup("Windows");
        panel.addGroupComponent(alwaysCentreDialogsCheckbox);
        panel.addGroupComponent(detachedWindowsFloat);

        panel.addSeparator();
        ButtonGroup axiomButtonGroup = new ButtonGroup();
        addFreshAxiomsToActiveOntologyRadioButton = new JRadioButton("Add fresh axioms to active ontology",
                FreshAxiomLocationPreferences.getPreferences().getFreshAxiomLocation() == FreshAxiomLocation.ACTIVE_ONTOLOGY);
        addFreshAxiomsToSubjectDefiningOntology = new JRadioButton("Add fresh axioms to subject defining ontology",
                FreshAxiomLocationPreferences.getPreferences().getFreshAxiomLocation() == FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY);
        addFreshAxiomsToSubjectDefiningOntology.setToolTipText("Adds fresh axioms to the ontology where their subject is defined.  " +
                "If no such ontology exists then axioms are added to the active ontology.");
        axiomButtonGroup.add(addFreshAxiomsToActiveOntologyRadioButton);
        axiomButtonGroup.add(addFreshAxiomsToSubjectDefiningOntology);

        panel.addGroup("Axioms");
        panel.addGroupComponent(addFreshAxiomsToActiveOntologyRadioButton);
        panel.addGroupComponent(addFreshAxiomsToSubjectDefiningOntology);



    }

    public void initialise() throws Exception {


        createUI();
//        // editor box
//
//        JPanel editorDelayPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
//        final int checkDelay = ExpressionEditorPreferences.getInstance().getCheckDelay();
//        checkDelaySpinner = new JSpinner(new SpinnerNumberModel(checkDelay, 0, 10000, 50));
//        checkDelaySpinner.setToolTipText(SECOND_TOOL_TIP);
//        editorDelayPanel.add(new JLabel("Editor delay (ms)"));
//        editorDelayPanel.add(checkDelaySpinner);
//
//
////        Box editorPanel = new Box(BoxLayout.PAGE_AXIS);
////        editorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Editor"),
////                                                                 BorderFactory.createEmptyBorder(7, 7, 7, 7)));
////        editorPanel.add(editorDelayPanel);
//
//
//        // search box
//
//        OWLEntityFinderPreferences prefs = OWLEntityFinderPreferences.getInstance();
//        findDelaySpinner = new JSpinner(new SpinnerNumberModel(prefs.getSearchDelay(), 0, 10000, 50));
//        findDelaySpinner.setToolTipText(SECOND_TOOL_TIP);
//
//        JPanel findDelayPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
//        findDelayPanel.add(new JLabel("Search delay (ms)"));
//        findDelayPanel.add(findDelaySpinner);
//
//        Preferences appPrefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
//        alwaysCentreDialogsCheckbox = new JCheckBox("Centre dialogs on workspace");
//        alwaysCentreDialogsCheckbox.setSelected(appPrefs.getBoolean(DIALOGS_ALWAYS_CENTRED, false));
//        detachedWindowsFloat = new JCheckBox("Detached windows float");
//        detachedWindowsFloat.setSelected(appPrefs.getBoolean(View.DETACHED_WINDOWS_FLOAT, true));
//
////        editorPanel.setAlignmentX(LEFT_ALIGNMENT);
////        searchPanel.setAlignmentX(LEFT_ALIGNMENT);
//
//
//        // Dialogs
//        Box dialogsPanel = new Box(BoxLayout.PAGE_AXIS);
////        dialogsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Dialogs"),
////                BorderFactory.createEmptyBorder(7, 7, 7, 7)));
//        alwaysCentreDialogsCheckbox.setAlignmentX(LEFT_ALIGNMENT);
//        detachedWindowsFloat.setAlignmentX(LEFT_ALIGNMENT);
//        dialogsPanel.add(alwaysCentreDialogsCheckbox);
//        dialogsPanel.add(detachedWindowsFloat);
//
//
//        // Axioms
//        Box axiomsPanel = new Box(BoxLayout.PAGE_AXIS);
////        axiomsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Axioms"),
////                BorderFactory.createEmptyBorder(7, 7, 7, 7)));
//        ButtonGroup axiomButtonGroup = new ButtonGroup();
//        addFreshAxiomsToActiveOntologyRadioButton = new JRadioButton("Add fresh axioms to active ontology",
//                FreshAxiomLocationPreferences.getPreferences().getFreshAxiomLocation() == FreshAxiomLocation.ACTIVE_ONTOLOGY);
//        addFreshAxiomsToSubjectDefiningOntology = new JRadioButton("Add fresh axioms to subject defining ontology",
//                FreshAxiomLocationPreferences.getPreferences().getFreshAxiomLocation() == FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY);
//        addFreshAxiomsToSubjectDefiningOntology.setToolTipText("Adds fresh axioms to the ontology where their subject is defined.  " +
//                "If no such ontology exists then axioms are added to the active ontology.");
//        axiomButtonGroup.add(addFreshAxiomsToActiveOntologyRadioButton);
//        axiomButtonGroup.add(addFreshAxiomsToSubjectDefiningOntology);
//        axiomsPanel.add(addFreshAxiomsToActiveOntologyRadioButton);
//        axiomsPanel.add(addFreshAxiomsToSubjectDefiningOntology);
//
//
//
//        JComponent holder = new JPanel(new GridBagLayout());//new Box(BoxLayout.PAGE_AXIS);
//        holder.add(new JLabel("Editor"),       getConstraints(0, 0, 1, 1));
//        holder.add(editorDelayPanel,       getConstraints(1, 0, 1, 1));
//        holder.add(new JSeparator(), getConstraints(0, 1, 2, 1));
//        holder.add(new JLabel("Windows"), getConstraints(0, 2, 1, 1));
//        holder.add(dialogsPanel,      getConstraints(1, 2, 1, 1));
//        holder.add(new JSeparator(),  getConstraints(0, 3, 2, 1));
//        holder.add(new JLabel("Axioms"), getConstraints(0, 4, 1, 1));
//        holder.add(axiomsPanel,       getConstraints(1, 4, 1, 1));
//        holder.setMinimumSize(new Dimension(800, 600));
//        add(holder, BorderLayout.NORTH);
    }

    public void dispose() {
    }

    public static void main(String[] args) throws Exception {
        GeneralPreferencesPanel pp = new GeneralPreferencesPanel();
        pp.initialise();
        JDialog dlg = new JDialog();
        dlg.getContentPane().add(pp);
        dlg.pack();
        dlg.setVisible(true);
    }
}
