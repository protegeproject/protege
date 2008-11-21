package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.owl.model.find.EntityFinderPreferences;
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

    private JRadioButton simpleSearchButton;

    private JRadioButton regularExpressionSearchButton;

    private JSpinner findDelaySpinner;

    private JSpinner checkDelaySpinner;

    private static final String SECOND_TOOL_TIP = "1000 = 1 second";


    public void applyChanges() {
        ExpressionEditorPreferences.getInstance().setCheckDelay((Integer) checkDelaySpinner.getModel().getValue());

        EntityFinderPreferences prefs = EntityFinderPreferences.getInstance();
        prefs.setSearchDelay(((Double) findDelaySpinner.getModel().getValue()).intValue());
        prefs.setUseRegularExpressions(regularExpressionSearchButton.isSelected());
    }


    public void initialise() throws Exception {
        setLayout(new PreferencesPanelLayoutManager(this));
        final int checkDelay = ExpressionEditorPreferences.getInstance().getCheckDelay();
        checkDelaySpinner = new JSpinner(new SpinnerNumberModel(checkDelay, 0, 10000, 50));
        add(checkDelaySpinner, "Editor delay (ms)");
        checkDelaySpinner.setToolTipText(SECOND_TOOL_TIP);


        EntityFinderPreferences prefs = EntityFinderPreferences.getInstance();
        findDelaySpinner = new JSpinner(new SpinnerNumberModel(prefs.getSearchDelay(), 0, 10000, 50));
        add(findDelaySpinner, "Search delay (ms)");
        findDelaySpinner.setToolTipText(SECOND_TOOL_TIP);
        simpleSearchButton = new JRadioButton("Simple search (using simple wildcards *)",
                                              !prefs.isUseRegularExpressions());
        regularExpressionSearchButton = new JRadioButton("Full regular expression search",
                                                         prefs.isUseRegularExpressions());
        ButtonGroup bg = new ButtonGroup();
        bg.add(simpleSearchButton);
        bg.add(regularExpressionSearchButton);
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.add(simpleSearchButton, BorderLayout.NORTH);
        panel.add(regularExpressionSearchButton, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search type"),
                                                           BorderFactory.createEmptyBorder(7, 7, 7, 7)));
        add(panel, null);
    }


    public void dispose() {
    }
}
